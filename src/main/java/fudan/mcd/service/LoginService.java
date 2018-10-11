package fudan.mcd.service;

import fudan.mcd.dao.impl.UserDAO;
import fudan.mcd.servlet.ServletUtils;
import fudan.mcd.vo.UserVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;

public class LoginService extends AbstractService {
	public static final int RESULT_SUCCESS = 1, RESULT_ACCOUNT_NOT_EXIST = -1, RESULT_WRONG_PASSWORD = -2;
	private static final Log LOG = LogFactory.getLog(LoginService.class);
	private UserVO dbUserVO;

	public LoginService(ServletContext context) {
		super(context);
	}

	public int login(UserVO user) {
		UserDAO dao = new UserDAO(context);
		dbUserVO = dao.queryByAccount(user.getAccount());
		if (dbUserVO == null) {
			LOG.info(String.format("User [ account = %s ] login at [ %s ] fail: account does not exsits!.", user.getAccount(), ServletUtils.getTime()));
			return RESULT_ACCOUNT_NOT_EXIST;
		}
		else if (!dbUserVO.getPassword().equals(user.getPassword())) {
			LOG.info(String.format("User [ account = %s ] login at [ %s ] fail: password is wrong!.", user.getAccount(), ServletUtils.getTime()));
			return RESULT_WRONG_PASSWORD;
		}
		else {
			return RESULT_SUCCESS;
		}
	}

	public UserVO getUserData() {
		return dbUserVO;
	}
}
