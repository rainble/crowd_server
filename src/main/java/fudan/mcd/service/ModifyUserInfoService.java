package fudan.mcd.service;

import fudan.mcd.dao.impl.UserDAO;
import fudan.mcd.servlet.ServletUtils;
import fudan.mcd.vo.UserVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;

public class ModifyUserInfoService extends AbstractService {
	private static final Log LOG = LogFactory.getLog(ModifyUserInfoService.class);
	private UserDAO userDAO = new UserDAO(context);
	
	public ModifyUserInfoService(ServletContext context) {
		super(context);
	}

	public int modifyUserInfo(UserVO user) {
		LOG.info(String.format("User [ %d ] request to update his own info at [ %s ]!", user.getId(),ServletUtils.getTime()));
		//先查询，获得相关信息
		UserVO originalUser = userDAO.query(user.getId());
		if(originalUser != null){
			user.setAccount(originalUser.getAccount());
			user.setPublishCredit(originalUser.getPublishCredit());
			user.setWithdrawCredit(originalUser.getWithdrawCredit());
			user.setLoginFlag(originalUser.getLoginFlag());
			if(user.getPassword().equals(""))
				user.setPassword(originalUser.getPassword());
			if(user.getTag() == -1)
				user.setTag(originalUser.getTag());
			int result = userDAO.update(user);
			if(result > 0)
				LOG.info(String.format("User [ %d ] has updated his own info at [ %s ] successfully!", user.getId(),ServletUtils.getTime()));
			else
				LOG.info(String.format("User [ %d ] fail to update his own info at [ %s ]: database error!", user.getId(),ServletUtils.getTime()));
			return result;
		}
		else{
			LOG.info(String.format("User [ %d ] fail to update his own info at [ %s ]: database error!", user.getId(),ServletUtils.getTime()));
			return -1;
		}
			
	}

	public UserVO queryUserInfo(int id) {
		UserVO user = userDAO.query(id);
//		if(user != null)
//			LOG.info(String.format("The info of user [%s] has been accessed successfully!", user.getAccount()));
//		else
//			LOG.info(String.format("The id of user [%d] doesn't exist!", id));
		return user;
	}

}
