package fudan.mcd.service;

import fudan.mcd.dao.impl.UserDAO;
import fudan.mcd.servlet.ServletUtils;
import fudan.mcd.vo.UserVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;

public class RegisterService extends AbstractService {
	public static final int RESULT_SUCCESS = 1, RESULT_ACCOUNT_EXISTED = -1, RESULT_DATABASE_ERROR = -2;
	private static final Log LOG = LogFactory.getLog(RegisterService.class);
	private int pk;

	public RegisterService(ServletContext context) {
		super(context);
		pk = -1;
	}

	public int register(UserVO user) {
		UserDAO dao = new UserDAO(context);
		if (dao.queryByAccount(user.getAccount()) == null) {
			//所有新注册用户都一样的值
			double initialPublishCredit = 30;
			double initialWithdrawCredit = 0;
			int initialLoginIndicator = 0;
			user.setPublishCredit(initialPublishCredit);
			user.setWithdrawCredit(initialWithdrawCredit);
			user.setLoginFlag(initialLoginIndicator);
			pk = dao.insert(user);
			if (pk > 0) {
				LOG.info(String.format("User [ account = %s ] register successfully at  [ %s ]!", user.getAccount(), ServletUtils.getTime()));
				return RESULT_SUCCESS;
			}
			else {
				LOG.info(String.format("User [ account = %s ] register fail at  [ %s ]: database insert error!", user.getAccount(), ServletUtils.getTime()));
				return RESULT_DATABASE_ERROR;
			}
		}
		else {
			LOG.info(String.format("User [ account = %s ] register fail at  [ %s ]: account already exsits!", user.getAccount(), ServletUtils.getTime()));
			return RESULT_ACCOUNT_EXISTED;
		}
	}

	public int getPrimaryKey() {
		return pk;
	}
}
