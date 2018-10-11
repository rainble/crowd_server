package fudan.mcd.servlet;

import fudan.mcd.service.GetPublishedTaskService;
import fudan.mcd.servlet.GetPublishedTaskServlet.ResponseBO.TaskBO;
import fudan.mcd.utils.JSONUtils;
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

@WebServlet("/GetPublishedTaskServlet")
public class GetPublishedTaskServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(GetPublishedTaskServlet.class);

	public GetPublishedTaskServlet() {
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
			//LOG.info(String.format("receive getPublishedTask request by [ userId = %d ].", userId));
		}
		catch (Exception e) {
			String responseString = JSONUtils.toJSONString(ServletUtils.generateParseFailedData());
			response.getOutputStream().println(responseString);
			LOG.info(String.format("Receive get published task request [ parameter parse failed ] at [ %s ].", ServletUtils.getTime()));
			return;
		}

		// Do business operation
		GetPublishedTaskService service = new GetPublishedTaskService(getServletContext());
		List<GetPublishedTaskService.ResultVO> taskVOList = service.getPublishedTask(userId);

		// Generate response data
		ServletResponseData responseData = new ServletResponseData();
		ResponseBO responseBO = new ResponseBO();
		List<TaskBO> tasks = new ArrayList<TaskBO>();

		for (GetPublishedTaskService.ResultVO task : taskVOList) {
			TaskBO taskBO = new TaskBO();
			TaskVO tvo = task.getTvo();
			taskBO.setId(tvo.getId());
			taskBO.setTemplateId(tvo.getTemplateId());
			taskBO.setTitle(tvo.getTitle());
			taskBO.setDescription(tvo.getDescription());
			taskBO.setStatus(tvo.getStatus());
			taskBO.setProgress(tvo.getCurrentStage() + "/" + task.getTpvo().getTotalStageNum());
			taskBO.setCurrentStage(tvo.getCurrentStage());
			taskBO.setBonusReward(tvo.getBonusReward());
			taskBO.setPublishTime(tvo.getPublishTime());
			taskBO.setDeadline(tvo.getDeadline());
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
			private int id;
			private int templateId;
			private String title;
			private String description;
			private int status;
			private String progress;
			private int currentStage;
			private double bonusReward;
			private Timestamp publishTime;
			private Timestamp deadline;

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
		}
	}
}
