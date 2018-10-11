package fudan.mcd.service;

import fudan.mcd.dao.impl.UserDAO;
import fudan.mcd.servlet.ServletUtils;
import fudan.mcd.vo.UserVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;


public class GetBonusPublishCreditService extends AbstractService {
	public static final int LOGIN_NOT_FIRST = 1;
	private static final Log LOG = LogFactory.getLog(GetBonusPublishCreditService.class);

	public GetBonusPublishCreditService(ServletContext context) {
		super(context);
	}

	public UserVO getBonusReward(int userId) {
		UserDAO uDAO = new UserDAO(context);
		UserVO uvo = uDAO.query(userId);
		if(uvo == null)
			return null;
		double bonusCreditPublish = 2;
		uvo.setPublishCredit(uvo.getPublishCredit() + bonusCreditPublish);
		int result = uDAO.update(uvo);
		if(result > 0){
			LOG.info(String.format("User [ %d ] get the bonus credit [ %f ] at [ %s ] successfully!", userId, bonusCreditPublish, ServletUtils.getTime()));
			uvo.setLoginFlag(LOGIN_NOT_FIRST);
			uDAO.update(uvo);
		}	
		else
			LOG.info(String.format("User [ %d ] fail to get the bonus credit at [ %s ]: database error!", userId, ServletUtils.getTime()));
		return uvo;
	}

}
