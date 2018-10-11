package fudan.mcd.servlet;

import fudan.mcd.service.ModifyUserInfoService;
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

@WebServlet("/ModifyUserInfoServlet")
public class ModifyUserInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(ModifyUserInfoServlet.class);
   
    public ModifyUserInfoServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		// Parse request parameters
		UserVO user = new UserVO();
		try {
			String jsonString = request.getParameter("data");
			RequestBO requestBO = JSONUtils.toBean(jsonString, RequestBO.class);
			user.setId(requestBO.id);
			if(requestBO.password != "")
				user.setPassword(RSAUtils.decryptByPrivateKey(requestBO.password));
			else
				user.setPassword("");
			user.setAvatar(requestBO.avatar);
			user.setTag(requestBO.tag);
		}
		catch (Exception e) {
			String responseString = JSONUtils.toJSONString(ServletUtils.generateParseFailedData());
			response.getOutputStream().println(responseString);
			LOG.info("Receive modify user info request [ parameter parse failed ].");
			return;
		}

		// Do business operation
		ModifyUserInfoService service = new ModifyUserInfoService(getServletContext());
		int modifyResult = service.modifyUserInfo(user);
		
		// Generate response data
		ServletResponseData responseData = new ServletResponseData();
		ResponseBO responseBO = new ResponseBO();
		if(modifyResult > 0){
			UserVO userUpdated = service.queryUserInfo(user.getId());
			if(userUpdated != null){
				responseBO.setId(userUpdated.getId());
				responseBO.setAccount(userUpdated.getAccount());
				responseBO.setPublishCredit(userUpdated.getPublishCredit());
				responseBO.setWithdrawCredit(userUpdated.getWithdrawCredit());
				responseBO.setAvatar(userUpdated.getAvatar());
				responseBO.setTag(userUpdated.getTag());
				responseBO.setLoginFlag(userUpdated.getLoginFlag());
				responseData.setData(JSONUtils.toJSONString(responseBO));
			}
		}
		responseData.setResult(modifyResult);
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().println(JSONUtils.toJSONString(responseData));
		
	}

	public static class RequestBO{
		private int id;
		private String password;
		private String avatar;
		private int tag;
		
		public int getId() {
			return id;
		}
		
		public void setId(int id) {
			this.id = id;
		}
		
		public String getPassword() {
			return password;
		}
		
		public void setPassword(String password) {
			this.password = password;
		}
		
		public String getAvatar() {
			return avatar;
		}
		
		public void setAvatar(String avatar) {
			this.avatar = avatar;
		}
		
		public int getTag() {
			return tag;
		}
		
		public void setTag(int tag) {
			this.tag = tag;
		}
		
	}
	
	public static class ResponseBO{
		private int id;
		private String account;
		private double publishCredit;
		private double withdrawCredit;
		private String avatar;
		private int tag;
		private int loginFlag;
		
		public int getId() {
			return id;
		}
		
		public void setId(int id) {
			this.id = id;
		}
		
		public String getAccount() {
			return account;
		}
		
		public void setAccount(String account) {
			this.account = account;
		}
		
		public double getPublishCredit() {
			return publishCredit;
		}
		
		public void setPublishCredit(double publishCredit) {
			this.publishCredit = publishCredit;
		}
		
		public double getWithdrawCredit() {
			return withdrawCredit;
		}
		
		public void setWithdrawCredit(double withdrawCredit) {
			this.withdrawCredit = withdrawCredit;
		}
		
		public String getAvatar() {
			return avatar;
		}
		
		public void setAvatar(String avatar) {
			this.avatar = avatar;
		}
		
		public int getTag() {
			return tag;
		}
		
		public void setTag(int tag) {
			this.tag = tag;
		}
		
		public int getLoginFlag() {
			return loginFlag;
		}
		
		public void setLoginFlag(int loginFlag) {
			this.loginFlag = loginFlag;
		}
	}
}
