package fudan.mcd.servlet;

import fudan.mcd.service.GetStageInfoService;
import fudan.mcd.servlet.GetStageInfoServlet.ResponseBO.InputBO;
import fudan.mcd.servlet.GetStageInfoServlet.ResponseBO.LocationBO;
import fudan.mcd.servlet.GetStageInfoServlet.ResponseBO.OutputBO;
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

@WebServlet("/GetStageInfoServlet")
public class GetStageInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(GetStageInfoServlet.class);
	private static final int PICTURE_OUTPUT = 0, TEXT_OUTPUT = 1, NUMERICAL_OUTPUT = 2, ENUM_OUTPUT = 3;

	public GetStageInfoServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		// Parse the request parameters
		int stageId,userId,mode;
		try {
			String jsonString = request.getParameter("data");
			RequestBO requestBO = JSONUtils.toBean(jsonString, RequestBO.class);
			stageId = requestBO.getStageId();
			userId = requestBO.getUserId();
			mode = requestBO.getMode();
		}
		catch (Exception e) {
			String responseString = JSONUtils.toJSONString(ServletUtils.generateParseFailedData());
			response.getOutputStream().println(responseString);
			LOG.info(String.format("Receive get stage info request [ parameter parse failed ] at [ %s ].", ServletUtils.getTime()));
			return;
		}

		// Do business operation
		GetStageInfoService service = new GetStageInfoService(getServletContext());
		GetStageInfoService.ResultVO rvo = service.getStageInfo(stageId,userId,mode);

		// Generate response data
		ServletResponseData responseData = new ServletResponseData();
		ResponseBO responseBO = new ResponseBO();

		if (rvo != null) {
			// Set the info about stage itself
			StageVO svo = rvo.getSvo();
			responseBO.setId(svo.getId());
			responseBO.setName(svo.getName());
			responseBO.setDesc(svo.getDescription());
			responseBO.setReward(svo.getReward());
			responseBO.setDdl(svo.getDeadline());
			responseBO.setWorkerNum(svo.getWorkerNum());
			responseBO.setWorkerNames(rvo.getWorkers());
			responseBO.setStageStatus(rvo.getStageStatus());
			responseBO.setRestrictions(svo.getRestrictions());

			// Set the info about src and dest
			LocationBO src = new LocationBO();
			setLocation(src, rvo.getSrcLinvo().getLvo());
			setInputs(src, rvo.getSrcLinvo().getIvoList());
			setOutputs(src, rvo.getSrcLinvo().getOvoList());
			LocationBO dest = new LocationBO();
			setLocation(dest, rvo.getDestLinvo().getLvo());
			setInputs(dest, rvo.getDestLinvo().getIvoList());
			setOutputs(dest, rvo.getDestLinvo().getOvoList());
			List<LocationBO> locations = new ArrayList<LocationBO>();
			locations.add(src);
			locations.add(dest);
			responseBO.setLocations(locations);

			// Set result for response
			responseData.setResult(1);
		}
		else {
			responseData.setResult(-1);
		}

		// Transform the responseBO to json string and output it
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
			inputBO.setDesc(inputVO.getDesc());
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
		private int stageId;
		private int userId;
		private int mode;

		public int getStageId() {
			return stageId;
		}

		public void setStageId(int stageId) {
			this.stageId = stageId;
		}

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}

		public int getMode() {
			return mode;
		}

		public void setMode(int mode) {
			this.mode = mode;
		}

	}

	public static class ResponseBO {
		private int id;
		private String name;
		private String desc;
		private double reward;
		private Timestamp ddl;
		private int workerNum;
		private List<String> workerNames;
		private int stageStatus;
		private long restrictions;
		List<LocationBO> locations;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
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
			private int type;
			private String desc;
			private String value;

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
