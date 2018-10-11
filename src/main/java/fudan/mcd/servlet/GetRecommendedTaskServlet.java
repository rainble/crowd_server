package fudan.mcd.servlet;

import fudan.mcd.service.GetRecommendedTaskService;
import fudan.mcd.servlet.GetRecommendedTaskServlet.ResponseBO.TaskBO;
import fudan.mcd.utils.JSONUtils;
import fudan.mcd.vo.StageVO;
import fudan.mcd.vo.TaskVO;
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

/**
 * Servlet implementation class GetRecommendedTaskServlet
 */
@WebServlet("/GetRecommendedTaskServlet")
public class GetRecommendedTaskServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(GetRecommendedTaskServlet.class);

	public GetRecommendedTaskServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		// Parse request parameters
//		int userId = 1;
//		double longitude = 121.605929;
//		double latitude = 31.197345;
		int userId;
		double longitude,latitude;
		try {
			String jsonString = request.getParameter("data");
			RequestBO requestBO = JSONUtils.toBean(jsonString, RequestBO.class);
			userId = requestBO.userId;
			longitude = requestBO.longitude;
			latitude = requestBO.latitude;
			//LOG.info(String.format("receive getRecommendedTask request [ userId = %d, templateId = %d ].", requestBO.getUserId(), requestBO.getUserId()));
		}
		catch (Exception e) {
			String responseString = JSONUtils.toJSONString(ServletUtils.generateParseFailedData());
			response.getOutputStream().println(responseString);
			LOG.info(String.format("Receive get recommended task request [ parameter parse failed ] at [ %s ].", ServletUtils.getTime()));
			return;
		}
		
		// Do business operation
		GetRecommendedTaskService service = new GetRecommendedTaskService(getServletContext());
		List<GetRecommendedTaskService.ResultVO> taskVOList = service.getRecommendedTask(userId,longitude,latitude);

		// Generate response data
		ServletResponseData responseData = new ServletResponseData();
		ResponseBO responseBO = new ResponseBO();
		List<TaskBO> tasks = new ArrayList<TaskBO>();

		for (GetRecommendedTaskService.ResultVO task : taskVOList) {
			TaskBO taskBO = new TaskBO();
			TaskVO tvo = task.getTvo();
			StageVO svo = task.getSvo();
			taskBO.setId(tvo.getId());
			taskBO.setTemplateId(tvo.getTemplateId());
			taskBO.setUserId(tvo.getUserId());
			taskBO.setRequester(task.getUvo().getAccount());
			taskBO.setTaskTitle(tvo.getTitle());
			taskBO.setStageDesc(svo.getDescription());
			taskBO.setStageReward(svo.getReward());
			taskBO.setProgress(tvo.getCurrentStage() + "/" + task.getTpvo().getTotalStageNum());
			taskBO.setCurrentStage(tvo.getCurrentStage());
			taskBO.setBonusReward(tvo.getBonusReward());
			taskBO.setPublishTime(tvo.getPublishTime());
			taskBO.setDeadline(tvo.getDeadline());
			if (task.getLvo() != null) {
				taskBO.setLatitude(task.getLvo().getLatitude());
				taskBO.setLongitude(task.getLvo().getLongitude());
			}
			taskBO.setStageContract(task.getContract());
			tasks.add(taskBO);
		}
		responseBO.setTasks(tasks);

		responseData.setResult(1);
		responseData.setData(JSONUtils.toJSONString(responseBO));
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.getWriter().println(JSONUtils.toJSONString(responseData));
	}

	public static class RequestBO {
		private int userId;
		private double longitude;
		private double latitude;

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
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
		private List<TaskBO> tasks;

		public List<TaskBO> getTasks() {
			return tasks;
		}

		public void setTasks(List<TaskBO> tasks) {
			this.tasks = tasks;
		}

		public static class TaskBO {
			private int id;
			private int templateId;
			private int userId;
			private String requester;
			private String taskTitle;
			private String stageDesc;
			private double stageReward;
			private String progress;
			private int currentStage;
			private double bonusReward;
			private Timestamp publishTime;
			private Timestamp deadline;
			private double longitude;
			private double latitude;
			private Timestamp stageContract;

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

			public int getUserId() {
				return userId;
			}

			public void setUserId(int userId) {
				this.userId = userId;
			}

			public String getRequester() {
				return requester;
			}

			public void setRequester(String requester) {
				this.requester = requester;
			}

			public String getTaskTitle() {
				return taskTitle;
			}

			public void setTaskTitle(String taskTitle) {
				this.taskTitle = taskTitle;
			}

			public String getStageDesc() {
				return stageDesc;
			}

			public void setStageDesc(String stageDesc) {
				this.stageDesc = stageDesc;
			}

			public double getStageReward() {
				return stageReward;
			}

			public void setStageReward(double stageReward) {
				this.stageReward = stageReward;
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

			public Timestamp getStageContract() {
				return stageContract;
			}

			public void setStageContract(Timestamp stageContract) {
				this.stageContract = stageContract;
			}
			
		}
	}
}
