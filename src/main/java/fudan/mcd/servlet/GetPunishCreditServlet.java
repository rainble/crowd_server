package fudan.mcd.servlet;

import fudan.mcd.service.GetPunishCreditService;
import fudan.mcd.utils.JSONUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/GetPunishCreditServlet")
public class GetPunishCreditServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(GetDesignedTemplateServlet.class);
    
    public GetPunishCreditServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		// Parse request parameters
		int userId;
		int stageId;
		try {
			String jsonString = request.getParameter("data");
			RequestBO requestBO = JSONUtils.toBean(jsonString, RequestBO.class);
			userId = requestBO.userId;
			stageId = requestBO.stageId;
		}
		catch (Exception e) {
			String responseString = JSONUtils.toJSONString(ServletUtils.generateParseFailedData());
			response.getOutputStream().println(responseString);
			LOG.info(String.format("Receive get punish credit request [ parameter parse failed ] at [ %s ].", ServletUtils.getTime()));
			return;
		}

		// Do business operation
		GetPunishCreditService service = new GetPunishCreditService(getServletContext());
		double creditPunish = service.getPunishCredit(userId, stageId);
		
		// Generate the output
		ServletResponseData responseData = new ServletResponseData();
		ResponseBO responseBO = new ResponseBO();
		if (creditPunish > 0) {
			responseData.setResult(1);
			responseBO.setCreditPunish(creditPunish);
		}
		else {
			responseData.setResult(-1);
			responseBO.setCreditPunish(-1);
		}
		responseData.setData(JSONUtils.toJSONString(responseBO));
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().println(JSONUtils.toJSONString(responseData));
	}

	public static class RequestBO{
		private int userId;
		private int stageId;
		
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
		
	}
	
	public static class ResponseBO{
		private double creditPunish;

		public double getCreditPunish() {
			return creditPunish;
		}

		public void setCreditPunish(double creditPunish) {
			this.creditPunish = creditPunish;
		}
		
	}
}
