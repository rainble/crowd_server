package fudan.mcd.service;

import fudan.mcd.dao.impl.*;
import fudan.mcd.servlet.ServletUtils;
import fudan.mcd.vo.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import java.util.List;

public class CancelTaskService  extends AbstractService {

	private static final Log LOG = LogFactory.getLog(AbortTaskService.class);
	public CancelTaskService(ServletContext context) {
		super(context);
	}

	//1:can cancel; -1:cannot cancel
	public int canCancel(int taskId, int userId) {
		LOG.info(String.format("Requester [ userId = %d ] confirm to cancel the task [ taskId = %d ] at [ %s ].", userId, taskId, ServletUtils.getTime()));
		TaskDAO tDAO = new TaskDAO(context);
		TaskVO taskVO = tDAO.query(taskId);
		if(null != taskVO && taskVO.getUserId() == userId &&
				taskVO.getCurrentStage() == 1 && taskVO.getStatus() == 0){
			StageDAO sDAO = new StageDAO(context);
			UndertakeDAO underDAO = new UndertakeDAO(context);
			//find the first stage of this task
			StageVO stage = sDAO.queryByTaskAndIndex(taskId, 1);
			List<UndertakeVO> undertakes =  underDAO.queryByStage(stage.getId());
			if(null == undertakes || undertakes.size() == 0){
				LOG.info(String.format("Requester [ userId = %d ] can cancel the task [ taskId = %d ] at [ %s ]!", userId, taskId, ServletUtils.getTime()));
				return 1;
			} else {
				LOG.info(String.format("Requester [ userId = %d ] cannot cancel the task [ taskId = %d ] at [ %s ] because someone has undertaken it!", userId, taskId, ServletUtils.getTime()));
				return -1;
			}
		} else {
			LOG.info(String.format("Requester [ userId = %d ] cannot cancel the task [ taskId = %d ] at [ %s ] because task has arrived at the second or more stage!", userId, taskId, ServletUtils.getTime()));
			return -2;
		}
	}
	
	//1：cancel successfully;
	public int cancelTask(int taskId, int userId){
		StageDAO sDAO = new StageDAO(context);
		LocationDAO locDAO = new LocationDAO(context);
		ActionDAO aDAO = new ActionDAO(context);
		InputDAO inputDAO = new InputDAO(context);
		OutputDAO outputDAO = new OutputDAO(context);
		
		List<StageVO> stages = sDAO.queryStageListByTask(taskId);
		for(StageVO stage:stages){
			LocationVO loc = locDAO.queryByStageAndType(stage.getId(), 1);
			if(null != loc){
				List<ActionVO> actions = aDAO.queryActionListByLocation(loc.getId());
				for(ActionVO action:actions){
					List<InputVO> inputs = inputDAO.queryInputListByAction(action.getId());
					if(null != inputs && inputs.size() > 0){
						for(InputVO input:inputs){
							inputDAO.delete(input.getId());
						}
					}
					List<OutputVO> outputs = outputDAO.queryOutputListByAction(action.getId());
					if(null != outputs && outputs.size() > 0){
						for(OutputVO output:outputs){
							outputDAO.delete(output.getId(), output.getClass());
						}
					}
					aDAO.delete(action.getId());
				}
				locDAO.delete(loc.getId());
			}
			sDAO.delete(stage.getId());
		}
		
		TaskDAO tDAO = new TaskDAO(context);
		tDAO.delete(taskId);
		return 1;
	}

}
