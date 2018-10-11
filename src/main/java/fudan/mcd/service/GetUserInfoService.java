package fudan.mcd.service;

import fudan.mcd.dao.impl.UserDAO;
import fudan.mcd.servlet.ServletUtils;
import fudan.mcd.vo.UserVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;

public class GetUserInfoService extends AbstractService {
	private static final Log LOG = LogFactory.getLog(GetUserInfoService.class);
	
	public GetUserInfoService(ServletContext context) {
		super(context);
	}
	
	public UserVO getUserInfo(int userId){
		LOG.info(String.format("User [ %d ] request his user info at [ %s ].", userId, ServletUtils.getTime()));
		UserVO uvo;
		UserDAO uDAO = new UserDAO(context);
		if(uDAO.query(userId) == null)
			return null;
		uvo = uDAO.query(userId);
		return uvo;
	}
}
