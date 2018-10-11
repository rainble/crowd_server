package fudan.mcd.service;

import fudan.mcd.dao.impl.StageDAO;
import fudan.mcd.dao.impl.TaskDAO;
import fudan.mcd.dao.impl.TemplateDAO;
import fudan.mcd.dao.impl.UndertakeDAO;
import fudan.mcd.servlet.ServletUtils;
import fudan.mcd.vo.StageVO;
import fudan.mcd.vo.TaskVO;
import fudan.mcd.vo.TemplateVO;
import fudan.mcd.vo.UndertakeVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class QueryTaskInfoService extends AbstractService{

	public static final int LOCATION_SRC = 0, LOCATION_DEST = 1;
	private static final Log LOG = LogFactory.getLog(QueryTaskInfoService.class);
	
	public QueryTaskInfoService(ServletContext context) {
		super(context);
	}


	public ResultVO queryTaskInfo(int taskId, int userId) {
		ResultVO rvo = new ResultVO();
		// The DAO of Task, Template, Stage and Location
		TaskDAO tDAO = new TaskDAO(context);
		TemplateDAO tpDAO = new TemplateDAO(context);
		StageDAO sDAO = new StageDAO(context);

		// Get the object using the corresponding DAO
		TaskVO tvo = tDAO.query(taskId);
		if (tvo == null) {
			return null;
		}

		boolean isRequester = false;
		if(tvo.getUserId() == userId)
			isRequester = true;

		TemplateVO tpvo = tpDAO.query(tvo.getTemplateId());
		List<StageVO> svoList = sDAO.queryStageListByTask(taskId);

		// Generate the stage with location of src and dest
		List<GetStageInfoService.ResultVO> stageInfoList = new ArrayList<GetStageInfoService.ResultVO>();
		GetStageInfoService service = new GetStageInfoService(context);
		for (StageVO svo : svoList) {
			//根据userId是否是stage worker，决定相应的模式查看stage信息
			GetStageInfoService.ResultVO stageInfo;
			//以requester身份，可以查看所有output；如果目前没有任何output，那么返回该stage的输出类型
			if(isRequester){
				LOG.info(String.format("User [ %d ] query the task [ %d ] info at [ %s ] as [ requester ].", userId, taskId, ServletUtils.getTime()));
				stageInfo = service.getStageInfo(svo.getId(),userId,2);
				if(stageInfo.getDestLinvo().getOvoList().size() == 0)
					stageInfo = service.getStageInfo(svo.getId(),userId,0);
			}
			//以一个task里某一个或几个stage worker的身份查看stage信息
			else{
				LOG.info(String.format("User [ %d ] query the task [ %d ] info at [ %s ] as [ worker ].", userId, taskId, ServletUtils.getTime()));
				//如果是该stage的worker并且已经完成stage，取出相应的output，并设置stage状态为已完成
				if(isWorkerAndFinished(svo.getId(),userId)){
					stageInfo = service.getStageInfo(svo.getId(),userId,1);
					stageInfo.setStageStatus(StageVO.STAGE_FINISHED);
				}
				//否则只返回该stage的输出类型
				else{
					stageInfo = service.getStageInfo(svo.getId(),userId,0);
					
					StageVO stage = stageInfo.getSvo();
					//查询并返回contract
					UndertakeDAO uDAO = new UndertakeDAO(context);
					UndertakeVO uvo = uDAO.queryByUserAndStage(userId, stage.getId());
					if(uvo != null)
						stage.setContract(uvo.getContractTime());
					else{
						String defaultTimeString = "2000-01-01 00:00:00";
						Timestamp defaultTime = Timestamp.valueOf(defaultTimeString);
						stage.setContract(defaultTime);
					}
					
				}
			}
			stageInfoList.add(stageInfo);
		}
		rvo.setTvo(tvo);
		rvo.setTpvo(tpvo);
		rvo.setStageInfoList(stageInfoList);
		return rvo;
	}
	
	private boolean isWorkerAndFinished(int stageId, int userId) {
		UndertakeDAO uDAO = new UndertakeDAO(context);
		UndertakeVO uvo = uDAO.queryByUserAndStage(userId, stageId);
		if(uvo == null)
			return false;
		else{
			if(uvo.getStatus() == UndertakeVO.STATUS_FINISHED)
				return true;
			else
				return false;
		}
	}

	public static class ResultVO {
		private TaskVO tvo;
		private TemplateVO tpvo;
		private List<GetStageInfoService.ResultVO> stageInfoList;

		public TaskVO getTvo() {
			return tvo;
		}

		public void setTvo(TaskVO tvo) {
			this.tvo = tvo;
		}

		public TemplateVO getTpvo() {
			return tpvo;
		}

		public void setTpvo(TemplateVO tpvo) {
			this.tpvo = tpvo;
		}

		public List<GetStageInfoService.ResultVO> getStageInfoList() {
			return stageInfoList;
		}

		public void setStageInfoList(List<GetStageInfoService.ResultVO> stageInfoList) {
			this.stageInfoList = stageInfoList;
		}
	}
}
