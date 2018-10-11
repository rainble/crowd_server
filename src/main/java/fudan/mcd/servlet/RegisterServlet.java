package fudan.mcd.servlet;

import fudan.mcd.service.RegisterService;
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

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(RegisterServlet.class);

	public RegisterServlet() {
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
			user.setTag(requestBO.userTag);
			user.setPhoneNum(requestBO.phone);
		}
		catch (Exception e) {
			String responseString = JSONUtils.toJSONString(ServletUtils.generateParseFailedData());
			response.getOutputStream().println(responseString);
			LOG.info(String.format("Receive register request [ parameter parse failed ] at [ %s ].", ServletUtils.getTime()));
			return;
		}

		// Do business operation
		RegisterService service = new RegisterService(getServletContext());
		int registerResult = service.register(user);

		// Generate response data
		ServletResponseData responseData = new ServletResponseData();
		ResponseBO responseBO = new ResponseBO();
		if (registerResult == RegisterService.RESULT_SUCCESS) {
			int pk = service.getPrimaryKey();
			responseBO.setUserId(pk);
			responseData.setData(JSONUtils.toJSONString(responseBO));
		}
		responseData.setResult(registerResult);
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().println(JSONUtils.toJSONString(responseData));
	}

	public static class RequestBO {
		private String account;
		private String password;
		private int userTag;
		private String phone;

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

		public int getUserTag() {
			return userTag;
		}

		public void setUserTag(int userTag) {
			this.userTag = userTag;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}
		
	}

	public static class ResponseBO {
		private int userId;

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}

	}
}
