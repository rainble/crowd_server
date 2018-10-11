package fudan.mcd.servlet;

import fudan.mcd.service.SaveTemplateService;
import fudan.mcd.utils.JSONUtils;
import fudan.mcd.vo.TemplateVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;

@WebServlet("/SaveTemplateServlet")
public class SaveTemplateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(SaveTemplateServlet.class);

	public SaveTemplateServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		// Parse request parameters
		TemplateVO tpvo = new TemplateVO();
		try {
			String jsonString = request.getParameter("data");
			RequestBO requestBO = JSONUtils.toBean(jsonString, RequestBO.class);
			tpvo.setId(requestBO.getTemplateId());
			tpvo.setUserId(requestBO.getUserId());
			tpvo.setName(requestBO.getName());
			tpvo.setDescription(requestBO.getDescription());
			tpvo.setHeat(requestBO.getHeat());
			tpvo.setCreateTime(requestBO.getCreateTime());
			tpvo.setUri(requestBO.getUri());
			tpvo.setTotalStageNum(requestBO.getTotalStageNum());
			//LOG.info(String.format("receive saveTemplate request [ templateId = %d, userId = %d].", requestBO.getTemplateId(), requestBO.getUserId()));

		}
		catch (Exception e) {
			String responseString = JSONUtils.toJSONString(ServletUtils.generateParseFailedData());
			response.getOutputStream().println(responseString);
			LOG.info(String.format("Receive save template request [ parameter parse failed ] at [ %s ].", ServletUtils.getTime()));
			return;
		}

		// Do business operation
		SaveTemplateService service = new SaveTemplateService(getServletContext());
		int result = service.saveTemplate(tpvo);

		// Generate response data
		ServletResponseData responseData = new ServletResponseData();
		ResponseBO responseBO = new ResponseBO();
		if (result == SaveTemplateService.SAVE_SUCCESS) {
			responseData.setResult(1);
			responseBO.setTemplateID(service.getPrimaryKey());
		}
		else {
			responseData.setResult(-1);
		}
		responseData.setData(JSONUtils.toJSONString(responseBO));
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().println(JSONUtils.toJSONString(responseData));
	}

	public static class RequestBO {
		private int templateId;
		private int userId;
		private String name;
		private String description;
		private int heat;
		private String uri;
		private Timestamp createTime;
		private int totalStageNum;

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

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public int getHeat() {
			return heat;
		}

		public void setHeat(int heat) {
			this.heat = heat;
		}

		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

		public Timestamp getCreateTime() {
			return createTime;
		}

		public void setCreateTime(Timestamp createTime) {
			this.createTime = createTime;
		}

		public int getTotalStageNum() {
			return totalStageNum;
		}

		public void setTotalStageNum(int totalStageNum) {
			this.totalStageNum = totalStageNum;
		}
	}

	public static class ResponseBO {
		private int templateID;

		public int getTemplateID() {
			return templateID;
		}

		public void setTemplateID(int templateID) {
			this.templateID = templateID;
		}

	}
}
