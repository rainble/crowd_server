package fudan.mcd.servlet;

import fudan.mcd.service.GetAcceptedTaskService;
import fudan.mcd.servlet.GetAcceptedTaskServlet.ResponseBO.InputBO;
import fudan.mcd.servlet.GetAcceptedTaskServlet.ResponseBO.LocationBO;
import fudan.mcd.servlet.GetAcceptedTaskServlet.ResponseBO.OutputBO;
import fudan.mcd.servlet.GetAcceptedTaskServlet.ResponseBO.TaskBO;
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

@WebServlet("/GetAcceptedTaskServlet")
public class GetAcceptedTaskServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(GetAcceptedTaskServlet.class);
	private static final int PICTURE_OUTPUT = 0, TEXT_OUTPUT = 1, NUMERICAL_OUTPUT = 2, ENUM_OUTPUT = 3;

	public GetAcceptedTaskServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		// Parse request parameters
		int userId;
		try {
			String jsonString = request.getParameter("data");
			RequestBO requestBO = JSONUtils.toBean(jsonString, RequestBO.class);
			userId = requestBO.userId;
			//LOG.info(String.format("receive getAcceptedTask request [ userId = %d, templateId = %d ].", requestBO.getUserId(), requestBO.getUserId()));
		}
		catch (Exception e) {
			String responseString = JSONUtils.toJSONString(ServletUtils.generateParseFailedData());
			response.getOutputStream().println(responseString);
			LOG.info(String.format("Receive getAcceptedTask request [ parameter parse failed ] at [ %s ].", ServletUtils.getTime()));
			return;
		}

		// Do business operation
		GetAcceptedTaskService service = new GetAcceptedTaskService(getServletContext());
		List<GetAcceptedTaskService.ResultVO> rvoList = service.getAcceptedTask(userId);

		// Generate response data
		ServletResponseData responseData = new ServletResponseData();
		ResponseBO responseBO = new ResponseBO();
		List<TaskBO> taskBOList = new ArrayList<TaskBO>();

		for (GetAcceptedTaskService.ResultVO rvo : rvoList) {
			List<LocationBO> locationBOList = new ArrayList<LocationBO>();
			TaskBO taskBO = new TaskBO();
			TaskVO tvo = rvo.getTvo();
			StageVO svo = rvo.getSvo();

			// Set the information about the task itself
			taskBO.setTaskId(tvo.getId());
			taskBO.setTaskTitle(tvo.getTitle());
			taskBO.setTaskDesc(tvo.getDescription());
			taskBO.setTaskProgress(tvo.getCurrentStage() + "/" + rvo.getTpvo().getTotalStageNum());
			taskBO.setCurrentStage(tvo.getCurrentStage());
			taskBO.setBonusReward(tvo.getBonusReward());
			taskBO.setTaskDeadline(tvo.getDeadline());

			// Set the information about the stage
			taskBO.setStageId(svo.getId());
			taskBO.setStageName(svo.getName());
			taskBO.setStageDesc(svo.getDescription());
			taskBO.setReward(svo.getReward());
			taskBO.setDdl(svo.getDeadline());
			taskBO.setContract(svo.getContract());

			// Set the info about src and dest
			if (rvo.getSrcLinvo() != null) {
				LocationBO src = new LocationBO();
				parseLocation(src, rvo.getSrcLinvo().getLvo());
				parseInputs(src, rvo.getSrcLinvo().getIvoList());
				parseOutputs(src, rvo.getSrcLinvo().getOvoList());
				locationBOList.add(src);
			}
			else {
				LocationBO src = new LocationBO();
				src.setType(LocationVO.TYPE_NOTNEED);
				locationBOList.add(src);
			}
			if (rvo.getDestLinvo() != null) {
				LocationBO dest = new LocationBO();
				parseLocation(dest, rvo.getDestLinvo().getLvo());
				parseInputs(dest, rvo.getDestLinvo().getIvoList());
				parseOutputs(dest, rvo.getDestLinvo().getOvoList());
				locationBOList.add(dest);
			}
			else {
				LocationBO dest = new LocationBO();
				dest.setType(LocationVO.TYPE_NOTNEED);
				locationBOList.add(dest);
			}

			taskBO.setLocations(locationBOList);
			taskBOList.add(taskBO);
		}
		responseBO.setTasks(taskBOList);

		// Transform the responseBO to json string and output it
		responseData.setResult(1);
		responseData.setData(JSONUtils.toJSONString(responseBO));
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().println(JSONUtils.toJSONString(responseData));
	}

	private void parseLocation(LocationBO bo, LocationVO vo) {
		if (vo != null) {
			bo.setAddress(vo.getAddress());
			bo.setId(vo.getId());
			bo.setLatitude(vo.getLatitude());
			bo.setLongitude(vo.getLongitude());
			bo.setStageId(vo.getStageId());
			bo.setType(vo.getType());
		}
	}

	private void parseInputs(LocationBO bo, List<InputVO> inputVOList) {
		List<InputBO> inputBOList = new ArrayList<InputBO>();
		if (inputVOList != null) {
			for (InputVO inputVO : inputVOList) {
				InputBO inputBO = new InputBO();
				inputBO.setActionId(inputVO.getActionId());
				inputBO.setDesc(inputVO.getDesc());
				inputBO.setId(inputVO.getId());
				inputBO.setType(inputVO.getType());
				inputBO.setValue(inputVO.getValue());
				inputBOList.add(inputBO);
			}
		}
		bo.setInputs(inputBOList);
	}

	private void parseOutputs(LocationBO bo, List<OutputVO> outputVOList) {
		List<OutputBO> outputBOList = new ArrayList<OutputBO>();
		if (outputVOList != null) {
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
		}
		bo.setOutputs(outputBOList);
	}

	public static class RequestBO {
		private int userId;

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}
	}

	public static class ResponseBO {
		private List<TaskBO> tasks;

		public List<TaskBO> getTasks() {
			return tasks;
		}

		public void setTasks(List<TaskBO> tasks) {
			this.tasks = tasks;
		}

		public static class TaskBO {
			private int taskId;
			private String taskTitle;
			private String taskDesc;
			private String taskProgress;
			private int currentStage;
			private double bonusReward;
			private Timestamp taskDeadline;
			private int stageId;
			private String stageName;
			private String stageDesc;
			private double reward;
			private Timestamp ddl;
			private Timestamp contract;
			List<LocationBO> locations;

			public int getTaskId() {
				return taskId;
			}

			public void setTaskId(int taskId) {
				this.taskId = taskId;
			}

			public String getTaskTitle() {
				return taskTitle;
			}

			public void setTaskTitle(String taskTitle) {
				this.taskTitle = taskTitle;
			}

			public String getTaskDesc() {
				return taskDesc;
			}

			public void setTaskDesc(String taskDesc) {
				this.taskDesc = taskDesc;
			}

			public String getTaskProgress() {
				return taskProgress;
			}

			public void setTaskProgress(String taskProgress) {
				this.taskProgress = taskProgress;
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

			public Timestamp getTaskDeadline() {
				return taskDeadline;
			}

			public void setTaskDeadline(Timestamp taskDeadline) {
				this.taskDeadline = taskDeadline;
			}

			public int getStageId() {
				return stageId;
			}

			public void setStageId(int stageId) {
				this.stageId = stageId;
			}

			public String getStageName() {
				return stageName;
			}

			public void setStageName(String stageName) {
				this.stageName = stageName;
			}

			public String getStageDesc() {
				return stageDesc;
			}

			public void setStageDesc(String stageDesc) {
				this.stageDesc = stageDesc;
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

			public Timestamp getContract() {
				return contract;
			}

			public void setContract(Timestamp contract) {
				this.contract = contract;
			}

			public List<LocationBO> getLocations() {
				return locations;
			}

			public void setLocations(List<LocationBO> locations) {
				this.locations = locations;
			}
		}

		public static class LocationBO {
			private int id;
			private int stageId;
			private String address;
			private double longitude;
			private double latitude;
			private int type;
			private List<InputBO> inputs;
			private List<OutputBO> outputs;

			public int getId() {
				return id;
			}

			public void setId(int id) {
				this.id = id;
			}

			public int getStageId() {
				return stageId;
			}

			public void setStageId(int stageId) {
				this.stageId = stageId;
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
