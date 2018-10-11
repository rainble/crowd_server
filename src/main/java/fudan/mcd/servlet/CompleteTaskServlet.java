package fudan.mcd.servlet;

import fudan.mcd.service.CompleteTaskService;
import fudan.mcd.service.PublishTaskService;
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

@WebServlet("/CompleteTaskServlet")
public class CompleteTaskServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(CompleteTaskServlet.class);
	private static final int PICTURE_OUTPUT = 0, TEXT_OUTPUT = 1, NUMERICAL_OUTPUT = 2, ENUM_OUTPUT = 3;
	private static final int OUTPUT_WITH_VALUE = 1;

	public CompleteTaskServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		UndertakeVO uvo = new UndertakeVO();
		List<OutputBO> outputs = new ArrayList<OutputBO>();
		double longitude = 0;
		double latitude = 0;
		// Parse request parameters
		try {
			String jsonString = request.getParameter("data");
			RequestBO requestBO = JSONUtils.toBean(jsonString, RequestBO.class);
			uvo.setUserId(requestBO.getUserId());
			uvo.setStageId(requestBO.getStageId());
			uvo.setEndTime(requestBO.finishTime);
			longitude = requestBO.getLongitude();
			latitude = requestBO.getLatitude();
			outputs = requestBO.getOutputs();
			//LOG.info(String.format("Receive complete task request [ userId = %d, stageId = %d ].", requestBO.getUserId(), requestBO.getStageId()));
		}
		catch (Exception e) {
			String responseString = JSONUtils.toJSONString(ServletUtils.generateParseFailedData());
			response.getOutputStream().println(responseString);
			LOG.info(String.format("Receive complete task request [ parameter parse failed ] at [ %s ].", ServletUtils.getTime()));
			return;
		}

		// Do business operation
		CompleteTaskService service = new CompleteTaskService(getServletContext(),longitude,latitude);
		int updateResult = service.updataUndertakeVO(uvo);
		
		// Update the output
		PictureOutputVO pictureOutputVO = null;
		List<TextOutputVO> textOutputList = new ArrayList<TextOutputVO>();
		List<NumericalOutputVO> numericalOutputList = new ArrayList<NumericalOutputVO>();
		List<EnumOutputVO> enumOutputList = new ArrayList<EnumOutputVO>();
		
		for (OutputBO outputBO : outputs){
			int type = outputBO.getType();
			switch(type){
			case PICTURE_OUTPUT:
				pictureOutputVO = new PictureOutputVO();
				pictureOutputVO.setId(outputBO.getId());
				pictureOutputVO.setActionId(outputBO.getActionId());
				pictureOutputVO.setDesc(outputBO.getDesc());
				pictureOutputVO.setValue(outputBO.getValue());
				pictureOutputVO.setActive(outputBO.isActive());
				pictureOutputVO.setWorkerId(uvo.getUserId());
				pictureOutputVO.setIndicator(OUTPUT_WITH_VALUE);
				break;
			case TEXT_OUTPUT:
				TextOutputVO textOutput = new TextOutputVO();
				textOutput.setId(outputBO.getId());
				textOutput.setActionId(outputBO.getActionId());
				textOutput.setDesc(outputBO.getDesc());
				textOutput.setValue(outputBO.getValue());
				textOutput.setWorkerId(uvo.getUserId());
				textOutput.setIndicator(OUTPUT_WITH_VALUE);
				textOutputList.add(textOutput);
				break;
			case NUMERICAL_OUTPUT:
				NumericalOutputVO numericalOutput = new NumericalOutputVO();
				numericalOutput.setId(outputBO.getId());
				numericalOutput.setActionId(outputBO.getActionId());
				numericalOutput.setDesc(outputBO.getDesc());
				numericalOutput.setValue(outputBO.getValue());
				numericalOutput.setInterval(outputBO.getIntervalValue());
				numericalOutput.setUpperBound(outputBO.getUpBound());
				numericalOutput.setLowerBound(outputBO.getLowBound());
				numericalOutput.setAggregationMethod(outputBO.getAggregaMethod());
				numericalOutput.setWorkerId(uvo.getUserId());
				numericalOutput.setIndicator(OUTPUT_WITH_VALUE);
				numericalOutputList.add(numericalOutput);
				break;
			case ENUM_OUTPUT:
				EnumOutputVO enumOutput = new EnumOutputVO();
				enumOutput.setId(outputBO.getId());
				enumOutput.setActionId(outputBO.getActionId());
				enumOutput.setDesc(outputBO.getDesc());
				enumOutput.setValue(outputBO.getValue());
				enumOutput.setEntries(outputBO.getEntries());
				enumOutput.setAggregationMethod(outputBO.getAggregaMethod());
				enumOutput.setWorkerId(uvo.getUserId());
				enumOutput.setIndicator(OUTPUT_WITH_VALUE);
				enumOutputList.add(enumOutput);
			}
		}
		
		//Update four types of output
		int result = -1;
		PublishTaskService publishService = new PublishTaskService(getServletContext());
		if(pictureOutputVO != null)
			result = publishService.insertPictureOutput(pictureOutputVO);
		if(textOutputList.size() > 0)
			result = publishService.insertTextOutput(textOutputList);
		if(numericalOutputList.size() > 0)
			result = publishService.insertNumericalOutput(numericalOutputList);
		if(enumOutputList.size() > 0)
			result = publishService.insertEnumOutput(enumOutputList);

		if (result > 0){
			System.out.println("Update all of the output successfully!");
		}
		else
			System.out.println("Fail to update all of the output!");
		// Generate the output
		ServletResponseData responseData = new ServletResponseData();
		ResponseBO responseBO = new ResponseBO();
		if (updateResult > 0) {
			responseData.setResult(1);
			responseBO.setUndertakeId(service.getPrimaryKey());
			//service.pushMessage();
		}
		else {
			responseData.setResult(updateResult);
			responseBO.setUndertakeId(-1);
		}
		responseData.setData(JSONUtils.toJSONString(responseBO));
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().println(JSONUtils.toJSONString(responseData));
	}

	public static class RequestBO {
		private int userId;
		private int stageId;
		private Timestamp finishTime;
		private double longitude;
		private double latitude;
		List<OutputBO> outputs;

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}

		public int getStageId() {
			return stageId;
		}

		public void setStageId(int stageId) {
			this.stageId = stageId;
		}

		public Timestamp getFinishTime() {
			return finishTime;
		}

		public void setFinishTime(Timestamp finishTime) {
			this.finishTime = finishTime;
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

		public List<OutputBO> getOutputs() {
			return outputs;
		}

		public void setOutputs(List<OutputBO> outputs) {
			this.outputs = outputs;
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
	
	public static class ResponseBO {
		private int undertakeId;

		public int getUndertakeId() {
			return undertakeId;
		}

		public void setUndertakeId(int undertakeId) {
			this.undertakeId = undertakeId;
		}
	}
}
