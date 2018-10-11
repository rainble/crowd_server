package fudan.mcd.servlet;

import fudan.mcd.service.CollectTemplateService;
import fudan.mcd.utils.JSONUtils;
import fudan.mcd.vo.CollectionVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/CollectTemplateServlet")
public class CollectTemplateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(CollectTemplateServlet.class);

	public CollectTemplateServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		CollectionVO cvo = new CollectionVO();

		// Parse request parameters
		RequestBO requestBO;
		try {
			String jsonString = request.getParameter("data");
			requestBO = JSONUtils.toBean(jsonString, RequestBO.class);
			cvo.setTemplateId(requestBO.getTemplateId());
			cvo.setUserId(requestBO.getUserId());
			//LOG.info(String.format("receive CollectTemplateServlet request by [ userId = %d ].", requestBO.getUserId()));

		}
		catch (Exception e) {
			String responseString = JSONUtils.toJSONString(ServletUtils.generateParseFailedData());
			response.getOutputStream().println(responseString);
			LOG.info(String.format("Receive collectTemplate request [ parameter parse failed ] at [ %s ].", ServletUtils.getTime()));
			return;
		}

		// Do business operation
		CollectTemplateService service = new CollectTemplateService(getServletContext());
		int collectResult = service.collectTemplate(cvo, requestBO.getIndicator());

		// Generate response data and output it
		ServletResponseData responseData = new ServletResponseData();
		ResponseBO responseBO = new ResponseBO();

		if (collectResult == CollectTemplateService.COLLECT_SUCCESS) {
			int pk = service.getCollectionPrimaryKey();
			responseBO.setCollectionId(pk);
			responseData.setResult(1);
		}
		else {
			responseData.setResult(-1);
			responseBO.setCollectionId(-1);
		}
		responseData.setData(JSONUtils.toJSONString(responseBO));
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().println(JSONUtils.toJSONString(responseData));
	}

	public static class RequestBO {
		private int templateId;
		private int userId;
		private int indicator;

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

		public int getIndicator() {
			return indicator;
		}

		public void setIndicator(int indicator) {
			this.indicator = indicator;
		}
	}

	public static class ResponseBO {
		private int collectionId;

		public int getCollectionId() {
			return collectionId;
		}

		public void setCollectionId(int collectionId) {
			this.collectionId = collectionId;
		}
	}
}
