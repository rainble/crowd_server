package fudan.mcd.service;

import fudan.mcd.dao.impl.UndertakeDAO;
import fudan.mcd.dao.impl.UserDAO;
import fudan.mcd.servlet.ServletUtils;
import fudan.mcd.vo.UndertakeVO;
import fudan.mcd.vo.UserVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;

public class AbortTaskService extends AbstractService {

	private static final Log LOG = LogFactory.getLog(AbortTaskService.class);
	public AbortTaskService(ServletContext context) {
		super(context);
	}

	public int abortTask(int userId, int stageId,double creditPunish) {
		LOG.info(String.format("User [ userId = %d ] confirm to abort the stage [ stageId = %d ] at [ %s ].", userId, stageId, ServletUtils.getTime()));
		// The dao of user, stage and undertake
		UserDAO uDAO = new UserDAO(context);
		UndertakeDAO underDAO = new UndertakeDAO(context);
		
		UndertakeVO uvo = underDAO.queryByUserAndStage(userId, stageId);
		if(uvo != null){
			//删除对应的undertake记录
			underDAO.delete(uvo.getId());
			LOG.info(String.format("User [ userId = %d ] abort the stage [ stageId = %d ] at [ %s ] successfully!", userId, stageId, ServletUtils.getTime()));
			
			//扣除用户积分作为惩罚
			UserVO user = uDAO.query(userId);
			if(user != null){
				double creditPublish = user.getPublishCredit() - creditPunish;
				user.setPublishCredit(creditPublish);
				uDAO.update(user);
				LOG.info(String.format("User [ userId = %d ] lost [ %f ] credit because of the abortion of the stage [ stageId = %d ] at [ %s ]!", userId, creditPublish, stageId, ServletUtils.getTime()));
			}
			
			return 1;
		}
		else{
			LOG.info(String.format("User [ userId = %d ] fail to abort the stage [ stageId = %d ] at [ %s ]!", userId, stageId, ServletUtils.getTime()));
			return -1;
		}
		
	}

}
