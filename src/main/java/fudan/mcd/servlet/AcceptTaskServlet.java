package fudan.mcd.servlet;

import fudan.mcd.runtime.AcceptStructure;
import fudan.mcd.runtime.Constant;
import fudan.mcd.runtime.MyTimerTask;
import fudan.mcd.service.AcceptTaskService;
import fudan.mcd.utils.JSONUtils;
import fudan.mcd.vo.UndertakeVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@WebServlet("/AcceptTaskServlet")
public class AcceptTaskServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(AcceptTaskServlet.class);
	public static Map<Integer,AcceptStructure> map = new HashMap<Integer,AcceptStructure>();

	public AcceptTaskServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		// Parse request parameters
		UndertakeVO uvo = new UndertakeVO();
		RequestBO requestBO;
		try {
			String jsonString = request.getParameter("data");
			requestBO = JSONUtils.toBean(jsonString, RequestBO.class);
			uvo.setUserId(requestBO.getUserId());
			uvo.setStartTime(requestBO.getStartTime());
			//LOG.info(String.format("Receive acceptTask request [ userId = %d, taskId = %d ].", requestBO.getUserId(), requestBO.getTaskId()));
		}
		catch (Exception e) {
			String responseString = JSONUtils.toJSONString(ServletUtils.generateParseFailedData());
			response.getOutputStream().println(responseString);
			LOG.info(String.format("Receive acceptTask request [ parameter parse failed ] at [ %s ].", ServletUtils.getTime()));
			return;
		}
		
		// Do business operation
		AcceptTaskService service = new AcceptTaskService(getServletContext());
		
		//将请求插入application表当中
		int stageId = service.getCurrentStageId(requestBO.getTaskId(), requestBO.getCurrentStage());
		service.insertApplication(stageId,requestBO.getUserId());
		boolean isNeedWorker = service.isNeedMoreWorker(stageId);
		int result;
		/**
		 * map里存储的是stageId以及与stageId对应的AcceptStructure
		 */
		//如果map里已有相应的structure
		if(map.get(stageId) != null){
			//如果未超时，将该requestBO添加到对应的list里
			if(map.get(stageId).isFlag()){
				AcceptStructure acceptStructure = map.get(stageId);
				acceptStructure.getRequests().add(requestBO);
				result = 1;
			}
			else{
				//超时分两种情况：第一种情况是该stage不再需要worker，那么直接返回-1；第二种情况则是需要额外的worker，那么新建structure覆盖并启动计时
				if(isNeedWorker){
					result = createNewStructure(stageId,requestBO,service);
				}
				else
					result = -1;
			}
		}
		//为null表示第一次有人申请接受该任务，新建structure
		else{
			result = createNewStructure(stageId,requestBO,service);
		}
		//int acceptResult = service.acceptTask(uvo, requestBO.getTaskId(), requestBO.getCurrentStage());

		// Generate response data
		ServletResponseData responseData = new ServletResponseData();
		ResponseBO responseBO = new ResponseBO();
		responseData.setResult(result);
		responseData.setData(JSONUtils.toJSONString(responseBO));
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().println(JSONUtils.toJSONString(responseData));
	}
	
	private int createNewStructure(int stageId,RequestBO requestBO,AcceptTaskService service){
		List<RequestBO> requests = new ArrayList<RequestBO>();
		requests.add(requestBO);
		int waitingTime = (int)(Constant.WAITING_TIME_WINDOW * 60);
		AcceptStructure acceptStructure = new AcceptStructure(requests,waitingTime);
		map.put(stageId, acceptStructure);
		//计时并在MyTimerTask里面进行worker的选择
		Timer timer = new Timer();
		MyTimerTask myTask = new MyTimerTask(stageId,service);
		timer.schedule(myTask, 30000);
		return 1;
	}

	public static class RequestBO {
		private int userId;
		private int taskId;
		private int currentStage;
		private Timestamp startTime;
		private Timestamp contractTime;

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}

		public int getTaskId() {
			return taskId;
		}

		public void setTaskId(int taskId) {
			this.taskId = taskId;
		}

		public int getCurrentStage() {
			return currentStage;
		}

		public void setCurrentStage(int currentStage) {
			this.currentStage = currentStage;
		}

		public Timestamp getStartTime() {
			return startTime;
		}

		public void setStartTime(Timestamp startTime) {
			this.startTime = startTime;
		}

		public Timestamp getContractTime() {
			return contractTime;
		}

		public void setContractTime(Timestamp contractTime) {
			this.contractTime = contractTime;
		}
		
	}

	public static class ResponseBO {
	}
}
