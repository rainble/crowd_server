package fudan.mcd.servlet;

import fudan.mcd.service.LoginService;
import fudan.mcd.utils.JSONUtils;
import fudan.mcd.utils.RSAUtils;
import fudan.mcd.vo.UserVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(LoginServlet.class);

	public LoginServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		// Parse request parameters
		UserVO user = new UserVO();
		try {
			String jsonString = request.getParameter("data");
			RequestBO requestBO = JSONUtils.toBean(jsonString, RequestBO.class);
			user.setAccount(RSAUtils.decryptByPrivateKey(requestBO.account));
			user.setPassword(RSAUtils.decryptByPrivateKey(requestBO.password));
			//LOG.info(String.format("Receive login request [ account = %s, password = %s ].", user.getAccount(), user.getPassword()));
		}
		catch (Exception e) {
			String responseString = JSONUtils.toJSONString(ServletUtils.generateParseFailedData());
			response.getOutputStream().println(responseString);
			LOG.info(String.format("Receive login request [ parameter parse failed ] at [ %s ].", ServletUtils.getTime()));
			return;
		}

		// Do business operation
		LoginService service = new LoginService(getServletContext());
		int loginResult = service.login(user);

		// Generate response data
		ServletResponseData responseData = new ServletResponseData();
		ResponseBO responseBO = new ResponseBO();
		if (loginResult == LoginService.RESULT_SUCCESS) {
			UserVO dbUserVO = service.getUserData();
			responseBO.setUserId(dbUserVO.getId());
			responseBO.setCreditPublish(dbUserVO.getPublishCredit());
			responseBO.setCreditWithdraw(dbUserVO.getWithdrawCredit());
			responseData.setData(JSONUtils.toJSONString(responseBO));
			LOG.info(String.format("User [ account = %s ] login at [ %s ] successfully!.", user.getAccount(), ServletUtils.getTime()));
		}
			
		responseData.setResult(loginResult);
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().println(JSONUtils.toJSONString(responseData));
	}

	public static class RequestBO {
		private String account;
		private String password;

		public String getAccount() {
			return account;
		}

		public void setAccount(String account) {
			this.account = account;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}

	public static class ResponseBO {
		private int userId;
		private double creditPublish;
		private double creditWithdraw;

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}

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
