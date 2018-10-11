package fudan.mcd.servlet;

import fudan.mcd.service.PublishTaskService;
import fudan.mcd.servlet.PublishTaskServlet.RequestBO.InputBO;
import fudan.mcd.servlet.PublishTaskServlet.RequestBO.LocationBO;
import fudan.mcd.servlet.PublishTaskServlet.RequestBO.OutputBO;
import fudan.mcd.servlet.PublishTaskServlet.RequestBO.StageBO;
import fudan.mcd.utils.JSONUtils;
import fudan.mcd.vo.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/PublishTaskServlet")
public class PublishTaskServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(PublishTaskServlet.class);
	private static final int STAGE_ONGOING = 0;
	private static final int STAGE_BEGIN = 1;
	private static final int PICTURE_OUTPUT = 0, TEXT_OUTPUT = 1, NUMERICAL_OUTPUT = 2, ENUM_OUTPUT = 3;
	private static final int OUTPUT_WITHOUT_VALUE = 0;

	public PublishTaskServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Parse request parameters
		request.setCharacterEncoding("UTF-8");
		TaskVO tvo = new TaskVO();
		RequestBO requestBO;
		try {
			String jsonString = request.getParameter("data");
			System.out.print(jsonString);
			requestBO = JSONUtils.toBean(jsonString, RequestBO.class);
			LOG.info(String.format("User [ %d ] request to publish task request at [ %s ].", requestBO.getRequesterId(), ServletUtils.getTime()));
			// Set the info of TaskVO
			tvo.setTemplateId(requestBO.getTemplateId());
			tvo.setUserId(requestBO.getRequesterId());
			tvo.setTitle(requestBO.getTitle());
			tvo.setDescription(requestBO.getDescription());
			tvo.setStatus(STAGE_ONGOING);
			tvo.setCurrentStage(STAGE_BEGIN);
			tvo.setBonusReward(requestBO.getBonusReward());
			tvo.setPublishTime(requestBO.getPublishTime());
			tvo.setDeadline(requestBO.getDeadline());
			tvo.setUserType(requestBO.getUserScope());
		}
		catch (Exception e) {
			String responseString = JSONUtils.toJSONString(ServletUtils.generateParseFailedData());
			response.getOutputStream().println(responseString);
			LOG.info(String.format("Receive publish task request [ parameter parse failed ] at [ %s ].", ServletUtils.getTime()));
			return;
		}

		// Do business operation
		PublishTaskService service = new PublishTaskService(getServletContext());

		// Insert the task first
		int insertTask = service.insertTask(tvo);
		if (insertTask == PublishTaskService.INSERT_SUCCESS) {

			// Insert the stages
			int taskPK = service.getTaskPK();
			for (StageBO slvo : requestBO.getStages()) {
				StageVO svo = new StageVO();

				svo.setTaskId(taskPK);
				svo.setName(slvo.getName());
				svo.setDescription(slvo.getDesc());
				svo.setDeadline(slvo.getDdl());
				svo.setReward(slvo.getReward());
				svo.setDuration(slvo.getStageDuration());
				svo.setIndex(slvo.getStageIndex());
				svo.setWorkerNum(slvo.getWorkerNum());
				svo.setAggregateMethod(slvo.getAggregateMethod());
				svo.setAggregateResult("");
				svo.setRestrictions(slvo.getRestrictions());

				// Insert the Location
				int stageId = service.insertStage(svo);
				if (stageId != PublishTaskService.INSERT_FAIL) {
					for (int i = 0; i < slvo.getLocations().size(); i++) {
						insertLoc(tvo.getUserId(),slvo.getLocations().get(i), stageId, service);
					}
				}
				else
					LOG.info(String.format("User [ %d ] fail to publish task at [ %s ]: insert stage error", tvo.getUserId(), ServletUtils.getTime()));
			}
		}
		else
			LOG.info(String.format("User [ %d ] fail to publish task at [ %s ]: insert task error", tvo.getUserId(), ServletUtils.getTime()));

		ServletResponseData responseData = new ServletResponseData();
		ResponseBO responseBO = new ResponseBO();
		int taskPK = service.getTaskPK();
		responseBO.setTaskId(taskPK);
		tvo.setId(taskPK);

		if(taskPK > 0){
			//更新template的heat信息
			int updateTempResult = service.updateTemplate(tvo.getTemplateId());
			if (updateTempResult > 0){
				System.out.println(String.format("Update the template info successfully!"));
			}
			else
				System.out.println(String.format("Update the template info fail!"));
			//更新credit的信息
			double totalReward = requestBO.getBonusReward();
			for (StageBO slvo : requestBO.getStages()) {
				double singleReward = slvo.getReward() * slvo.getWorkerNum();
				totalReward += singleReward;
			}
			int updateCredit = service.updateCredit(tvo,requestBO.getRequesterId(),totalReward);
			if (updateCredit > 0){
				System.out.println(String.format("Update the credit info successfully!"));
			}
			else
				System.out.println(String.format("Update the credit info fail!"));
		}

		responseData.setResult(1);
		responseData.setData(JSONUtils.toJSONString(responseBO));
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().println(JSONUtils.toJSONString(responseData));
	}

	private void insertLoc(int userId, LocationBO locationBO, int stageId, PublishTaskService service) {
		LocationVO location = new LocationVO();
		location.setStageId(stageId);
		location.setAddress(locationBO.getAddress());
		location.setLatitude(locationBO.getLatitude());
		location.setLongitude(locationBO.getLongitude());
		location.setType(locationBO.getType());

		int locationId = service.insertLocation(location);
		if (locationId != PublishTaskService.INSERT_FAIL) {
			ActionVO actionVO = new ActionVO();
			actionVO.setLocationId(locationId);
			actionVO.setDuration(locationBO.getDuration());//Add: duration of src and dest location
			// Insert the action
			int actionId = service.insertAction(actionVO);
			if (actionId != PublishTaskService.INSERT_FAIL) {
				// Insert the input
				List<InputVO> inputVOList = new ArrayList<InputVO>();
				for (InputBO inputBO : locationBO.getInputs()) {
					InputVO inputVO = new InputVO();
					inputVO.setActionId(actionId);
					inputVO.setType(inputBO.getType());
					inputVO.setDesc(inputBO.getDesc());
					inputVO.setValue(inputBO.getValue());
					inputVOList.add(inputVO);
				}
				// Insert the output
				PictureOutputVO pictureOutputVO = new PictureOutputVO();
				List<TextOutputVO> textOutputList = new ArrayList<TextOutputVO>();
				List<NumericalOutputVO> numericalOutputList = new ArrayList<NumericalOutputVO>();
				List<EnumOutputVO> enumOutputList = new ArrayList<EnumOutputVO>();

				for (OutputBO outputBO : locationBO.getOutputs()) {
					int type = outputBO.getType();
					switch(type){
					case PICTURE_OUTPUT:
						pictureOutputVO.setActionId(actionId);
						pictureOutputVO.setDesc(outputBO.getDesc());
						pictureOutputVO.setActive(outputBO.isActive());
						pictureOutputVO.setIndicator(OUTPUT_WITHOUT_VALUE);
						break;
					case TEXT_OUTPUT:
						TextOutputVO textOutput = new TextOutputVO();
						textOutput.setActionId(actionId);
						textOutput.setDesc(outputBO.getDesc());
						textOutput.setIndicator(OUTPUT_WITHOUT_VALUE);
						textOutputList.add(textOutput);
						break;
					case NUMERICAL_OUTPUT:
						NumericalOutputVO numericalOutput = new NumericalOutputVO();
						numericalOutput.setActionId(actionId);
						numericalOutput.setDesc(outputBO.getDesc());
						numericalOutput.setInterval(outputBO.getIntervalValue());
						numericalOutput.setUpperBound(outputBO.getUpBound());
						numericalOutput.setLowerBound(outputBO.getLowBound());
						numericalOutput.setAggregationMethod(outputBO.getAggregaMethod());
						numericalOutput.setIndicator(OUTPUT_WITHOUT_VALUE);
						numericalOutputList.add(numericalOutput);
						break;
					case ENUM_OUTPUT:
						EnumOutputVO enumOutput = new EnumOutputVO();
						enumOutput.setActionId(actionId);
						enumOutput.setDesc(outputBO.getDesc());
						enumOutput.setEntries(outputBO.getEntries());
						enumOutput.setAggregationMethod(outputBO.getAggregaMethod());
						enumOutput.setIndicator(OUTPUT_WITHOUT_VALUE);
						enumOutputList.add(enumOutput);
					}
				}
				int result = service.insertInput(inputVOList);
				//Insert four types of output
				if(pictureOutputVO.isActive())
					result = service.insertPictureOutput(pictureOutputVO);
				if(textOutputList.size() > 0)
					result = service.insertTextOutput(textOutputList);
				if(numericalOutputList.size() > 0)
					result = service.insertNumericalOutput(numericalOutputList);
				if(enumOutputList.size() > 0)
					result = service.insertEnumOutput(enumOutputList);
				if (result > 0)
					LOG.info(String.format("User [ %d ] publish task at [ %s ] successfully!", userId, ServletUtils.getTime()));
				else
					LOG.info(String.format("User [ %d ] fail to publish task at [ %s ]: insert output error", userId, ServletUtils.getTime()));
			}
			else
				LOG.info(String.format("User [ %d ] fail to publish task at [ %s ]: insert action error", userId, ServletUtils.getTime()));
		}
		else
			LOG.info(String.format("User [ %d ] fail to publish task at [ %s ]: insert location error", userId, ServletUtils.getTime()));
	}

	public static class RequestBO {
		private int templateId;
		private int requesterId;
		private String title;
		private String description;
		private double bonusReward;
		private Timestamp publishTime;
		private Timestamp deadline;
		private int userScope;
		private List<StageBO> stages;

		public int getTemplateId() {
			return templateId;
		}

		public void setTemplateId(int templateId) {
			this.templateId = templateId;
		}

		public int getRequesterId() {
			return requesterId;
		}

		public void setRequesterId(int requesterId) {
			this.requesterId = requesterId;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public double getBonusReward() {
			return bonusReward;
		}

		public void setBonusReward(double bonusReward) {
			this.bonusReward = bonusReward;
		}

		public Timestamp getPublishTime() {
			return publishTime;
		}

		public void setPublishTime(Timestamp publishTime) {
			this.publishTime = publishTime;
		}

		public Timestamp getDeadline() {
			return deadline;
		}

		public void setDeadline(Timestamp deadline) {
			this.deadline = deadline;
		}

		public int getUserScope() {
			return userScope;
		}

		public void setUserScope(int userScope) {
			this.userScope = userScope;
		}

		public List<StageBO> getStages() {
			return stages;
		}

		public void setStages(List<StageBO> stages) {
			this.stages = stages;
		}

		public static class StageBO {
			private String name;
			private String desc;
			private double reward;
			private double stageDuration;
			private int stageIndex;
			private Timestamp ddl;
			private int workerNum;
			private int aggregateMethod;
			private long restrictions;
			private List<LocationBO> locations;

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getDesc() {
				return desc;
			}

			public void setDesc(String desc) {
				this.desc = desc;
			}

			public double getReward() {
				return reward;
			}

			public void setReward(double reward) {
				this.reward = reward;
			}

			public double getStageDuration() {
				return stageDuration;
			}

			public void setStageDuration(double stageDuration) {
				this.stageDuration = stageDuration;
			}

			public int getStageIndex() {
				return stageIndex;
			}

			public void setStageIndex(int stageIndex) {
				this.stageIndex = stageIndex;
			}

			public Timestamp getDdl() {
				return ddl;
			}

			public void setDdl(Timestamp ddl) {
				this.ddl = ddl;
			}

			public int getWorkerNum() {
				return workerNum;
			}

			public void setWorkerNum(int workerNum) {
				this.workerNum = workerNum;
			}

			public int getAggregateMethod() {
				return aggregateMethod;
			}

			public void setAggregateMethod(int aggregateMethod) {
				this.aggregateMethod = aggregateMethod;
			}

			public long getRestrictions() {
				return restrictions;
			}

			public void setRestrictions(long restrictions) {
				this.restrictions = restrictions;
			}

			public List<LocationBO> getLocations() {
				return locations;
			}

			public void setLocations(List<LocationBO> locations) {
				this.locations = locations;
			}

		}

		public static class LocationBO {
			private int type;
			private String address;
			private double longitude;
			private double latitude;
			private double duration;
			private List<InputBO> inputs;
			private List<OutputBO> outputs;

			public int getType() {
				return type;
			}

			public void setType(int type) {
				this.type = type;
			}

			public String getAddress() {
				return address;
			}

			public void setAddress(String address) {
				this.address = address;
			}

			public double getLongitude() {
				return longitude;
			}

			public void setLongitude(double longitude) {
				this.longitude = longitude;
			}

			public double getLatitude() {
				return latitude;
			}

			public void setLatitude(double latitude) {
				this.latitude = latitude;
			}

			public double getDuration() {
				return duration;
			}

			public void setDuration(double duration) {
				this.duration = duration;
			}

			public List<InputBO> getInputs() {
				return inputs;
			}

			public void setInputs(List<InputBO> inputs) {
				this.inputs = inputs;
			}

			public List<OutputBO> getOutputs() {
				return outputs;
			}

			public void setOutputs(List<OutputBO> outputs) {
				this.outputs = outputs;
			}
		}

		public static class InputBO {
			private int id;
			private int actionId;
			private int type;
			private String desc;
			private String value;

			public int getId() {
				return id;
			}

			public void setId(int id) {
				this.id = id;
			}

			public int getActionId() {
				return actionId;
			}

			public void setActionId(int actionId) {
				this.actionId = actionId;
			}

			public int getType() {
				return type;
			}

			public void setType(int type) {
				this.type = type;
			}

			public String getDesc() {
				return desc;
			}

			public void setDesc(String desc) {
				this.desc = desc;
			}

			public String getValue() {
				return value;
			}

			public void setValue(String value) {
				this.value = value;
			}
		}

		public static class OutputBO {
			private int id;
			private int actionId;
			private int type;
			private String desc;
			private String value;
			boolean isActive;
			private int intervalValue;
			private double upBound;
			private double lowBound;
			private String entries;
			private int aggregaMethod;
			

			public int getId() {
				return id;
			}

			public void setId(int id) {
				this.id = id;
			}

			public int getActionId() {
				return actionId;
			}

			public void setActionId(int actionId) {
				this.actionId = actionId;
			}

			public int getType() {
				return type;
			}

			public void setType(int type) {
				this.type = type;
			}

			public String getDesc() {
				return desc;
			}

			public void setDesc(String desc) {
				this.desc = desc;
			}

			public String getValue() {
				return value;
			}

			public void setValue(String value) {
				this.value = value;
			}

			public boolean isActive() {
				return isActive;
			}

			public void setActive(boolean isActive) {
				this.isActive = isActive;
			}

			public int getIntervalValue() {
				return intervalValue;
			}

			public void setIntervalValue(int intervalValue) {
				this.intervalValue = intervalValue;
			}

			public double getUpBound() {
				return upBound;
			}

			public void setUpBound(double upBound) {
				this.upBound = upBound;
			}

			public double getLowBound() {
				return lowBound;
			}

			public void setLowBound(double lowBound) {
				this.lowBound = lowBound;
			}

			public String getEntries() {
				return entries;
			}

			public void setEntries(String entries) {
				this.entries = entries;
			}

			public int getAggregaMethod() {
				return aggregaMethod;
			}

			public void setAggregaMethod(int aggregaMethod) {
				this.aggregaMethod = aggregaMethod;
			}
		}
	}

	public static class ResponseBO {
		private int taskId;

		public int getTaskId() {
			return taskId;
		}

		public void setTaskId(int taskId) {
			this.taskId = taskId;
		}
	}
}
