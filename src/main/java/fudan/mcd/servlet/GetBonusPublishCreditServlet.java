package fudan.mcd.servlet;

import fudan.mcd.service.GetBonusPublishCreditService;
import fudan.mcd.utils.JSONUtils;
import fudan.mcd.vo.UserVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/GetBonusPublishCreditServlet")
public class GetBonusPublishCreditServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(GetBonusPublishCreditServlet.class);
       
    public GetBonusPublishCreditServlet() {
        super();
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		// Parse request parameters
		int userId;
		try {
			String jsonString = request.getParameter("data");
			RequestBO requestBO = JSONUtils.toBean(jsonString, RequestBO.class);
			userId = requestBO.getUserId();
			//LOG.info(String.format("receive GetBonusPublishCredit request [ userId = %d ].", requestBO.getUserId()));
		}
		catch (Exception e) {
			String responseString = JSONUtils.toJSONString(ServletUtils.generateParseFailedData());
			response.getOutputStream().println(responseString);
			LOG.info(String.format("Receive getBonusPublishCredit request [ parameter parse failed ] at [ %s ].", ServletUtils.getTime()));
			return;
		}

		// Do business operation
		GetBonusPublishCreditService service = new GetBonusPublishCreditService(getServletContext());
		UserVO uvo = service.getBonusReward(userId);
		
		// Generate response data
		ServletResponseData responseData = new ServletResponseData();
		ResponseBO responseBO = new ResponseBO();
		if(uvo != null){
			responseBO.setCreditPublish(uvo.getPublishCredit());
			responseBO.setCreditWithdraw(uvo.getWithdrawCredit());
			responseData.setResult(1);
		}
		else
			responseData.setResult(-1);
		
		// Transform the responseBO to json string and output it
		responseData.setData(JSONUtils.toJSONString(responseBO));
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().println(JSONUtils.toJSONString(responseData));		
	}

	public static class RequestBO{
		private int userId;

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}
	}
	
	public static class ResponseBO{
		private double creditPublish;
		private double creditWithdraw;

		public double getCreditPublish() {
			return creditPublish;
		}

		public void setCreditPublish(double creditPublish) {
			this.creditPublish = creditPublish;
		}

		public double getCreditWithdraw() {
			return creditWithdraw;
		}

		public void setCreditWithdraw(double creditWithdraw) {
			this.creditWithdraw = creditWithdraw;
		}
	}
}
