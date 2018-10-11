package fudan.mcd.servlet;

import fudan.mcd.service.GetCompletedTaskService;
import fudan.mcd.servlet.GetCompletedTaskServlet.ResponseBO.TaskBO;
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

@WebServlet("/GetCompletedTaskServlet")
public class GetCompletedTaskServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(GetCompletedTaskServlet.class);

	public GetCompletedTaskServlet() {
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
		}
		catch (Exception e) {
			String responseString = JSONUtils.toJSONString(ServletUtils.generateParseFailedData());
			response.getOutputStream().println(responseString);
			LOG.info(String.format("Receive getCompletedTask request [ parameter parse failed ] at [ %s ].", ServletUtils.getTime()));
			return;
		}

		// Do business operation
		GetCompletedTaskService service = new GetCompletedTaskService(getServletContext());
		List<GetCompletedTaskService.ResultVO> rvoList = service.getCompletedTask(userId);

		// Generate response data
		ServletResponseData responseData = new ServletResponseData();
		ResponseBO responseBO = new ResponseBO();
		List<TaskBO> tasks = new ArrayList<TaskBO>();

		for (GetCompletedTaskService.ResultVO rvo : rvoList) {
			TaskBO taskBO = new TaskBO();

			// Set the information about the task itself
			TaskVO tvo = rvo.getTvo();
			taskBO.setTaskId(tvo.getId());
			taskBO.setTaskTitle(tvo.getTitle());
			taskBO.setTaskDesc(tvo.getDescription());
			taskBO.setTaskProgress(tvo.getCurrentStage() + "/" + rvo.getTpvo().getTotalStageNum());
			taskBO.setCurrentStage(tvo.getCurrentStage());
			taskBO.setBonusReward(tvo.getBonusReward());
			taskBO.setTaskDeadline(tvo.getDeadline());

			// Set the information about the stage
			StageVO svo = rvo.getSvo();
			taskBO.setStageName(svo.getName());
			taskBO.setStageDesc(svo.getDescription());
			taskBO.setReward(svo.getReward());
			taskBO.setStageStatus(rvo.getUvo().getStatus());
			taskBO.setFinishTime(rvo.getUvo().getEndTime());

			tasks.add(taskBO);
		}
		responseBO.setTasks(tasks);

		// Transform the responseBO to json string and output it
		responseData.setResult(1);
		responseData.setData(JSONUtils.toJSONString(responseBO));
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().println(JSONUtils.toJSONString(responseData));
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
			private String stageName;
			private String stageDesc;
			private double reward;
			private int stageStatus;
			private Timestamp finishTime;

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

			public int getStageStatus() {
				return stageStatus;
			}

			public void setStageStatus(int stageStatus) {
				this.stageStatus = stageStatus;
			}

			public Timestamp getFinishTime() {
				return finishTime;
			}

			public void setFinishTime(Timestamp finishTime) {
				this.finishTime = finishTime;
			}
		}
	}
}
