package fudan.mcd.service;

import fudan.mcd.dao.impl.*;
import fudan.mcd.servlet.AcceptTaskServlet.RequestBO;
import fudan.mcd.servlet.ServletUtils;
import fudan.mcd.vo.ApplicationVO;
import fudan.mcd.vo.StageVO;
import fudan.mcd.vo.TaskVO;
import fudan.mcd.vo.UndertakeVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import java.util.List;

public class AcceptTaskService extends AbstractService {

	private static final Log LOG = LogFactory.getLog(AcceptTaskService.class);
	private static final int UNDERTAKE_SUCCESS = 1, UNDERTAKE_FAIL = -1, UNDERTAKE_ALREADY_FULL = -2;
	private static final int STAGE_ONGOING = 0;
	private StageDAO stageDAO = new StageDAO(context);
	
	public AcceptTaskService(ServletContext context) {
		super(context);
	}
	
	public int getCurrentStageId(int taskId, int currentStage){
		StageDAO stageDAO = new StageDAO(context);
		StageVO stageVO = stageDAO.queryByTaskAndIndex(taskId, currentStage);
		if(stageVO == null) {
			return -1;
		}
		return stageVO.getId();
	}

	public int acceptStage(int stageId, List<RequestBO> targets) {
		//LOG.info(String.format(" The stageId of stage to be undertaken is [ %d ]", stageId));
		// DAO of the Stage, Undertake and User
		UndertakeDAO undertakeDAO = new UndertakeDAO(context);
		UserDAO userDAO = new UserDAO(context);

		//接任务
		int result = -1;
		for(RequestBO target : targets){
			// Check whether the account is valid
			if (userDAO.query(target.getUserId()) == null)
				return UNDERTAKE_FAIL;
			
			//判断工人数量是否还有剩余名额
			StageVO stageVO = stageDAO.query(stageId);
			int workerNum = stageVO.getWorkerNum();
			List<UndertakeVO> unders = undertakeDAO.queryByStage(stageId);
			int currentNum = 0;
			for(UndertakeVO under:unders){
				if(under.getStatus() != -1){
					currentNum++;
				}
			}
			if(currentNum >= workerNum)
				return UNDERTAKE_ALREADY_FULL;
			
			UndertakeVO undertakeVO = new UndertakeVO();
			undertakeVO.setUserId(target.getUserId());
			undertakeVO.setStageId(stageId);
			undertakeVO.setStartTime(target.getStartTime());
			undertakeVO.setContractTime(target.getContractTime());
			undertakeVO.setStatus(STAGE_ONGOING);
			result = undertakeDAO.insert(undertakeVO);
		}
		if (result > 0)
			return UNDERTAKE_SUCCESS;
		else
			return UNDERTAKE_FAIL;
	}

	public StageVO getStageInfo(int stageId) {
		StageVO stage = stageDAO.query(stageId);
		return stage;
	}

	public String getTaskTitle(int stageId) {
		String taskTitle = "This is default string value";
		StageVO stage = stageDAO.query(stageId);
		TaskDAO tDAO = new TaskDAO(context);
		if(stage != null){
			TaskVO task = tDAO.query(stage.getTaskId());
			if(task != null)
				taskTitle = task.getTitle();
		}
		return taskTitle;
	}

	public boolean isNeedMoreWorker(int stageId) {
		boolean result = false;
		UndertakeDAO undertakeDAO = new UndertakeDAO(context);
		//判断工人数量是否还有剩余名额
		StageVO stageVO = stageDAO.query(stageId);
		if(stageVO != null){
			int workerNum = stageVO.getWorkerNum();
			//只有非过期状态的undertake才算一个人数
			List<UndertakeVO> unders = undertakeDAO.queryByStage(stageId);
			int currentNum = 0;
			for(UndertakeVO under:unders){
				if(under.getStatus() != -1){
					currentNum++;
				}
			}
			if(currentNum < workerNum)
				result = true;
		}
		return result;
	}
	
	public int getLeftWorkerNumber(int stageId){
		int result = 0;
		UndertakeDAO undertakeDAO = new UndertakeDAO(context);
		//判断工人数量是否还有剩余名额
		StageVO stageVO = stageDAO.query(stageId);
		if(stageVO != null){
			int workerNum = stageVO.getWorkerNum();
			List<UndertakeVO> unders = undertakeDAO.queryByStage(stageId);
			int currentNum = 0;
			for(UndertakeVO under:unders){
				if(under.getStatus() != -1){
					currentNum++;
				}
			}
			result = workerNum - currentNum;
		}
		return result;
	}

	public void insertApplication(int stageId, int userId) {
		ApplicationDAO aDAO = new ApplicationDAO(context);
		ApplicationVO aVO = new ApplicationVO();
		aVO.setStageId(stageId);
		aVO.setUserId(userId);
		int result = aDAO.insert(aVO);
		if(result > 0)
			LOG.info(String.format("User [ %d ] apply for the stage [ %d ] at [ %s ] successfully!", userId, stageId, ServletUtils.getTime()));
		else
			LOG.info(String.format("User [ %d ] apply for the stage [ %d ] at [ %s ] fail!", userId, stageId, ServletUtils.getTime()));
			
	}

	public void deleteApplication(int userId, int stageId) {
		ApplicationDAO aDAO = new ApplicationDAO(context);
		ApplicationVO aVO = aDAO.deleteByUserAndStage(userId, stageId);
		if(aVO != null)
			LOG.info(String.format("Delete the record of user [ %d ] apply for the stage [ %d ] at [ %s ] successfully!", userId, stageId, ServletUtils.getTime()));
		else
			LOG.info(String.format("Fail to delete the record of user [ %d ] apply for the stage [ %d ] at [ %s ]!", userId, stageId, ServletUtils.getTime()));
	}

}
