package fudan.mcd.servlet;

import fudan.mcd.service.GetStageInfoService;
import fudan.mcd.service.GetTaskInfoService;
import fudan.mcd.servlet.GetTaskInfoServlet.ResponseBO.InputBO;
import fudan.mcd.servlet.GetTaskInfoServlet.ResponseBO.LocationBO;
import fudan.mcd.servlet.GetTaskInfoServlet.ResponseBO.OutputBO;
import fudan.mcd.servlet.GetTaskInfoServlet.ResponseBO.StageBO;
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

@WebServlet("/GetTaskInfoServlet")
public class GetTaskInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(GetTaskInfoServlet.class);
	private static final int PICTURE_OUTPUT = 0, TEXT_OUTPUT = 1, NUMERICAL_OUTPUT = 2, ENUM_OUTPUT = 3;

	public GetTaskInfoServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		// Parse request parameters

//		int taskId = 3;
//		double longitude = 121.605929;
//		double latitude = 31.197345;
		int taskId;
		double longitude,latitude;
		try {
			String jsonString = request.getParameter("data");
			RequestBO requestBO = JSONUtils.toBean(jsonString, RequestBO.class);
			taskId = requestBO.taskId;
			longitude = requestBO.longitude;
			latitude = requestBO.latitude;
			//LOG.info(String.format("receive getTaskInfo request of [ taskId = %d ].", taskId));
		}
		catch (Exception e) {
			String responseString = JSONUtils.toJSONString(ServletUtils.generateParseFailedData());
			response.getOutputStream().println(responseString);
			LOG.info(String.format("Receive get task info request [ parameter parse failed ] at [ %s ].", ServletUtils.getTime()));
			return;
		}

		// Do business operation
		GetTaskInfoService service = new GetTaskInfoService(getServletContext());
		GetTaskInfoService.ResultVO rvo = service.getTaskInfo(taskId,longitude,latitude);

		// Generate response data
		ServletResponseData responseData = new ServletResponseData();
		ResponseBO responseBO = new ResponseBO();

		if (rvo != null) {
			// Set the information about the task itself
			TaskVO tvo = rvo.getTvo();
			responseBO.setId(tvo.getId());
			responseBO.setTemplateId(tvo.getTemplateId());
			responseBO.setRequesterId(tvo.getUserId());
			responseBO.setTitle(tvo.getTitle());
			responseBO.setDescription(tvo.getDescription());
			responseBO.setStatus(tvo.getStatus());
			responseBO.setProgress(tvo.getCurrentStage() + "/" + rvo.getTpvo().getTotalStageNum());
			responseBO.setBonusReward(tvo.getBonusReward());
			responseBO.setPublishTime(tvo.getPublishTime());
			responseBO.setDeadline(tvo.getDeadline());
			responseBO.setCurrentStage(tvo.getCurrentStage());

			// Set the information about stage and the corresponding location
			List<StageBO> stageBOList = new ArrayList<StageBO>();
			for (int i = 0; i < rvo.getStageInfoList().size(); i++) {
				StageBO slvo = new StageBO();
				GetStageInfoService.ResultVO stageInfoVO = rvo.getStageInfoList().get(i);
				StageVO svo = stageInfoVO.getSvo();
				slvo.setStageId(svo.getId());
				slvo.setName(svo.getName());
				slvo.setDesc(svo.getDescription());
				slvo.setReward(svo.getReward());
				slvo.setDdl(svo.getDeadline());
				slvo.setWorkerNum(svo.getWorkerNum());
				slvo.setWorkerNames(stageInfoVO.getWorkers());
				slvo.setStageStatus(stageInfoVO.getStageStatus());
				if(svo.getIndex() == tvo.getCurrentStage()){
					slvo.setContractTime(rvo.getContractTime());
				}

				// Get the info about src and dest
				LocationBO src = new LocationBO();
				if(stageInfoVO.getSrcLinvo() != null){
					setLocation(src, stageInfoVO.getSrcLinvo().getLvo());
					setInputs(src, stageInfoVO.getSrcLinvo().getIvoList());
					setOutputs(src, stageInfoVO.getSrcLinvo().getOvoList());
				}
				else{
					src.setType(LocationVO.TYPE_NOTNEED);
				}
				LocationBO dest = new LocationBO();
				if(stageInfoVO.getDestLinvo() != null){
					setLocation(dest, stageInfoVO.getDestLinvo().getLvo());
					setInputs(dest, stageInfoVO.getDestLinvo().getIvoList());
					setOutputs(dest, stageInfoVO.getDestLinvo().getOvoList());
				}
				else{
					dest.setType(LocationVO.TYPE_NOTNEED);
				}
				List<LocationBO> locations = new ArrayList<LocationBO>();
				locations.add(src);
				locations.add(dest);

				// Set the info of location src and dest
				slvo.setLocations(locations);
				stageBOList.add(slvo);
			}
			responseBO.setStages(stageBOList);
		}

		// Transform the responseBO to json string and output it
		if (rvo != null)
			responseData.setResult(1);
		else
			responseData.setResult(-1);
		responseData.setData(JSONUtils.toJSONString(responseBO));
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().println(JSONUtils.toJSONString(responseData));
	}

	private void setLocation(LocationBO locationBO, LocationVO locationVO) {
		locationBO.setType(locationVO.getType());
		locationBO.setAddress(locationVO.getAddress());
		locationBO.setLongitude(locationVO.getLongitude());
		locationBO.setLatitude(locationVO.getLatitude());
	}

	private void setInputs(LocationBO locationBO, List<InputVO> inputVOList) {
		List<InputBO> inputBOList = new ArrayList<InputBO>();
		for (InputVO inputVO : inputVOList) {
			InputBO inputBO = new InputBO();
			inputBO.setActionId(inputVO.getActionId());
			inputBO.setDesc(inputVO.getDesc());
			inputBO.setId(inputVO.getId());
			inputBO.setType(inputVO.getType());
			inputBO.setValue(inputVO.getValue());
			inputBOList.add(inputBO);
		}
		locationBO.setInputs(inputBOList);
	}

	private void setOutputs(LocationBO locationBO, List<OutputVO> outputVOList) {
		List<OutputBO> outputBOList = new ArrayList<OutputBO>();
		for (OutputVO outputVO : outputVOList) {
			OutputBO outputBO = new OutputBO();
			//Return the four types of output
			if(outputVO instanceof PictureOutputVO){
				outputBO.setId(outputVO.getId());
				outputBO.setActionId(outputVO.getActionId());
				outputBO.setActive(((PictureOutputVO) outputVO).isActive());
				outputBO.setType(PICTURE_OUTPUT);
				outputBO.setDesc(outputVO.getDesc());
				outputBO.setValue(outputVO.getValue());
			}
			else if(outputVO instanceof TextOutputVO){
				outputBO.setId(outputVO.getId());
				outputBO.setActionId(outputVO.getActionId());
				outputBO.setType(TEXT_OUTPUT);
				outputBO.setDesc(outputVO.getDesc());
				outputBO.setValue(outputVO.getValue());
			}
			else if(outputVO instanceof NumericalOutputVO){
				outputBO.setId(outputVO.getId());
				outputBO.setActionId(outputVO.getActionId());
				outputBO.setIntervalValue(((NumericalOutputVO) outputVO).getInterval());
				outputBO.setUpBound(((NumericalOutputVO) outputVO).getUpperBound());
				outputBO.setLowBound(((NumericalOutputVO) outputVO).getLowerBound());
				outputBO.setAggregaMethod(((NumericalOutputVO) outputVO).getAggregationMethod());
				outputBO.setType(NUMERICAL_OUTPUT);
				outputBO.setDesc(outputVO.getDesc());
				outputBO.setValue(outputVO.getValue());
			}
			else if(outputVO instanceof EnumOutputVO){
				outputBO.setId(outputVO.getId());
				outputBO.setActionId(outputVO.getActionId());
				outputBO.setEntries(((EnumOutputVO) outputVO).getEntries());
				outputBO.setAggregaMethod(((EnumOutputVO) outputVO).getAggregationMethod());
				outputBO.setType(ENUM_OUTPUT);
				outputBO.setDesc(outputVO.getDesc());
				outputBO.setValue(outputVO.getValue());
			}
			outputBOList.add(outputBO);
		}
		locationBO.setOutputs(outputBOList);
	}

	public static class RequestBO {
		private int taskId;
		private double longitude;
		private double latitude;

		public int getTaskId() {
			return taskId;
		}

		public void setTaskId(int taskId) {
			this.taskId = taskId;
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

	}

	public static class ResponseBO {
		private int id;
		private int templateId;
		private int requesterId;
		private String title;
		private String description;
		private int status;
		private String progress;
		private int currentStage;
		private double bonusReward;
		private Timestamp publishTime;
		private Timestamp deadline;
		private List<StageBO> stages;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

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

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public String getProgress() {
			return progress;
		}

		public void setProgress(String progress) {
			this.progress = progress;
		}

		public int getCurrentStage() {
			return currentStage;
		}

		public void setCurrentStage(int currentStage) {
			this.currentStage = currentStage;
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

		public List<StageBO> getStages() {
			return stages;
		}

		public void setStages(List<StageBO> stages) {
			this.stages = stages;
		}

		public static class StageBO {
			private int stageId;
			private String name;
			private String desc;
			private double reward;
			private Timestamp ddl;
			private Timestamp contractTime;
			private int workerNum;
			private List<String> workerNames;
			private int stageStatus;
			List<LocationBO> locations;

			public int getStageId() {
				return stageId;
			}

			public void setStageId(int stageId) {
				this.stageId = stageId;
			}

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

			public Timestamp getDdl() {
				return ddl;
			}

			public void setDdl(Timestamp ddl) {
				this.ddl = ddl;
			}

			public Timestamp getContractTime() {
				return contractTime;
			}

			public void setContractTime(Timestamp contractTime) {
				this.contractTime = contractTime;
			}

			public int getWorkerNum() {
				return workerNum;
			}

			public void setWorkerNum(int workerNum) {
				this.workerNum = workerNum;
			}

			public List<String> getWorkerNames() {
				return workerNames;
			}

			public void setWorkerNames(List<String> workerNames) {
				this.workerNames = workerNames;
			}

			public int getStageStatus() {
				return stageStatus;
			}

			public void setStageStatus(int stageStatus) {
				this.stageStatus = stageStatus;
			}

			public List<LocationBO> getLocations() {
				return locations;
			}

			public void setLocations(List<LocationBO> locations) {
				this.locations = locations;
			}

		}

		public static class LocationBO {
			private String address;
			private double longitude;
			private double latitude;
			private int type;
			private List<InputBO> inputs;
			private List<OutputBO> outputs;

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

			public int getType() {
				return type;
			}

			public void setType(int type) {
				this.type = type;
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
}
