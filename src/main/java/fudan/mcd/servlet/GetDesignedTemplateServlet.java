package fudan.mcd.servlet;

import fudan.mcd.service.GetDesignedTemplateService;
import fudan.mcd.servlet.GetDesignedTemplateServlet.ResponseBO.TemplateBO;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/GetDesignedTemplateServlet")
public class GetDesignedTemplateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(GetDesignedTemplateServlet.class);

	public GetDesignedTemplateServlet() {
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
			//LOG.info(String.format("receive getDesignedTemplateServlet request by [ userId = %d ].", userId));
		}
		catch (Exception e) {
			String responseString = JSONUtils.toJSONString(ServletUtils.generateParseFailedData());
			response.getOutputStream().println(responseString);
			LOG.info(String.format("Receive get designed template request [ parameter parse failed ] at [ %s ].", ServletUtils.getTime()));
			return;
		}

		// Do business operation
		GetDesignedTemplateService service = new GetDesignedTemplateService(getServletContext());
		List<TemplateVO> tpvoList = service.getDesignedTemplateService(userId);

		// Generate response data
		ServletResponseData responseData = new ServletResponseData();
		ResponseBO responseBO = new ResponseBO();
		List<TemplateBO> templates = new ArrayList<TemplateBO>();
		for (TemplateVO tpvo : tpvoList) {
			TemplateBO templateBO = new TemplateBO();
			templateBO.setTemplateId(tpvo.getId());
			templateBO.setName(tpvo.getName());
			templateBO.setDescription(tpvo.getDescription());
			templateBO.setHeat(tpvo.getHeat());
			templateBO.setUri(tpvo.getUri());
			templateBO.setCreateTime(tpvo.getCreateTime());
			templateBO.setTotalStageNum(tpvo.getTotalStageNum());
			templates.add(templateBO);
		}
		responseBO.setTemplates(templates);

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
		private List<TemplateBO> templates;

		public List<TemplateBO> getTemplates() {
			return templates;
		}

		public void setTemplates(List<TemplateBO> templates) {
			this.templates = templates;
		}

		public static class TemplateBO {
			private int templateId;
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
	}
}
