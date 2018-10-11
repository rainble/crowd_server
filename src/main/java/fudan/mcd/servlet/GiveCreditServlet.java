package fudan.mcd.servlet;

import fudan.mcd.service.GiveCreditService;
import fudan.mcd.utils.JSONUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/GiveCreditServlet")
public class GiveCreditServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(GiveCreditServlet.class);
       
    public GiveCreditServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		// Parse request parameters
		int userId;
		int taskId;

		try {
			String jsonString = request.getParameter("data");
			RequestBO requestBO = JSONUtils.toBean(jsonString, RequestBO.class);
			userId = requestBO.userId;
			taskId = requestBO.taskId;
		}
		catch (Exception e) {
			String responseString = JSONUtils.toJSONString(ServletUtils.generateParseFailedData());
			response.getOutputStream().println(responseString);
			LOG.info(String.format("Receive give credit request [ parameter parse failed ] at [ %s ].", ServletUtils.getTime()));
			return;
		}

		// Do business operation
		GiveCreditService service = new GiveCreditService(getServletContext());
		int result = service.giveCredit(userId,taskId);
		System.out.println("give credit result = "+result);
		
		// Generate the output
		ServletResponseData responseData = new ServletResponseData();
		if (result > 0) {
			responseData.setResult(1);
		}
		else {
			responseData.setResult(-1);
		}
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().println(JSONUtils.toJSONString(responseData));
	}

	public static class RequestBO{
		private int userId;
		private int taskId;
		
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

	}
	
	public static class ResponseBO{
		
	}
}
