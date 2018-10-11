package fudan.mcd.service;

import fudan.mcd.dao.impl.StageDAO;
import fudan.mcd.dao.impl.TaskDAO;
import fudan.mcd.dao.impl.UndertakeDAO;
import fudan.mcd.dao.impl.UserDAO;
import fudan.mcd.servlet.ServletUtils;
import fudan.mcd.vo.StageVO;
import fudan.mcd.vo.TaskVO;
import fudan.mcd.vo.UndertakeVO;
import fudan.mcd.vo.UserVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import java.util.List;

public class GiveCreditService extends AbstractService {
	
	private static final Log LOG = LogFactory.getLog(GiveCreditService.class);
	public static final int UNDERTAKE_ONGOING = 0, UNDERTAKE_FINISHED = 1, UNDERTAKE_EXPIRED = -1;

	
	public GiveCreditService(ServletContext context) {
		super(context);
	}
	
	//1:success; -1:failed
	public int giveCredit(int userId, int taskId){
		TaskDAO tDAO = new TaskDAO(context);
		StageDAO sDAO = new StageDAO(context);
		UndertakeDAO underDAO = new UndertakeDAO(context);
		UserDAO uDAO = new UserDAO(context);	
		
		TaskVO task = tDAO.query(taskId);
		if(task.getUserId() == userId){
			List<StageVO> stages = sDAO.queryStageListByTask(taskId);
			for(StageVO stage:stages){
				List<UndertakeVO> us = underDAO.queryByStage(stage.getId());
				for(UndertakeVO under:us){
					UserVO userVO = uDAO.query(under.getUserId());
					double reward = stage.getReward();
					userVO.setWithdrawCredit(userVO.getWithdrawCredit() + reward);
					uDAO.update(userVO);
					under.setStatus(UndertakeVO.STATUS_CREDIT_GIVEN);
					underDAO.update(under);
					System.out.println("worker=========");
					LOG.info(String.format("The worker [ %d ] get the credit [ %f ] from the requester [ %d ] at [ %s ]!",userVO.getId(), reward, userId,ServletUtils.getTime()));
				}
			}
			task.setStatus(TaskVO.STATUS_CREDIT_GIVEN);
			tDAO.update(task);
			System.out.println("task update========");
			return 1;
		} else {
			LOG.info(String.format("The task [ %d ]'s requester is not the user [ %d ] at [ %s ]!",taskId, userId,ServletUtils.getTime()));
			return -1;
		}
		
	}
	
	
}
