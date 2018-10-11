package fudan.mcd.service;

import fudan.mcd.dao.impl.*;
import fudan.mcd.servlet.ServletUtils;
import fudan.mcd.vo.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;

public class GetStageInfoService extends AbstractService {
	public static final int LOCATION_SRC = 0, LOCATION_DEST = 1;
	private static final Log LOG = LogFactory.getLog(GetStageInfoService.class);

	public GetStageInfoService(ServletContext context) {
		super(context);
	}

	public ResultVO getStageInfo(int stageId, int userId, int mode) {
		LOG.info(String.format("The detail of stage [ %d ] has been requested by user [ %d ] at [ %s ]", stageId, userId, ServletUtils.getTime()));
		ResultVO rvo = new ResultVO();

		// DAO of the Stage, Location, Undertake and User
		StageDAO sDAO = new StageDAO(context);
		LocationDAO lDAO = new LocationDAO(context);
		UndertakeDAO undertakeDAO = new UndertakeDAO(context);

		// Get the info about StageVO, LocationVO and InputVO
		StageVO svo = sDAO.query(stageId);

		// Check stage id
		if (svo == null)
			return null;

		// Get the worker list if any
		List<UndertakeVO> undertakes = undertakeDAO.queryByStage(svo.getId());
		int workerNum = svo.getWorkerNum();
		int currentNum = 0;
		for(UndertakeVO under : undertakes){
			if(under.getStatus() != -1){
				currentNum++;
			}
		}
		
		//Set the status of stage
		if(svo.getStatus() == StageVO.STAGE_ONGOING){
			if (currentNum < workerNum)
				rvo.setStageStatus(StageVO.STAGE_IDLE);
			else
				rvo.setStageStatus(StageVO.STAGE_ONGOING);
		}
		else
			rvo.setStageStatus(svo.getStatus());
		
		//Set the worker list
		List<String> workers = getWorkerList(undertakes);
		rvo.setWorkers(workers);
		
		// Get the info about start location and the input
		LocationVO src = lDAO.queryByStageAndType(svo.getId(), LOCATION_SRC);
		LocationVO dest = lDAO.queryByStageAndType(svo.getId(), LOCATION_DEST);
		LocInputVO srcLinvo = null;
		if(src != null){
			srcLinvo = getInputByLocation(src,userId,mode);
		}
		LocInputVO destLinvo = null;
		if(dest != null){
			destLinvo = getInputByLocation(dest,userId,mode);
		}

		// Set the info of ResultVO
		rvo.setSvo(svo);
		rvo.setSrcLinvo(srcLinvo);
		rvo.setDestLinvo(destLinvo);

		return rvo;
	}
	
	private List<String> getWorkerList(List<UndertakeVO> undertakes) {
		List<String> workers = new ArrayList<String>();
		UserDAO userDAO = new UserDAO(context);
		UserVO uservo;
		for (UndertakeVO uvo : undertakes) {
			//非过期状态的undertake才算有效人数
			if(uvo.getStatus() != -1){
				uservo = userDAO.query(uvo.getUserId());
				workers.add(uservo.getAccount());
			}
		}
		return workers;
	}

	public LocInputVO getInputByLocation(LocationVO lvo, int userId, int mode) {
		LocInputVO linvo = new LocInputVO();
		// DAO of the Action and Input
		ActionDAO aDAO = new ActionDAO(context);
		InputDAO iDAO = new InputDAO(context);
		OutputDAO oDAO = new OutputDAO(context);

		// Get the input list
		//LOG.info(String.format("The corresponding locationId is %d ", lvo.getId()));
		ActionVO avo = aDAO.queryActionListByLocation(lvo.getId()).get(0);
		List<InputVO> ivoList = iDAO.queryInputListByAction(avo.getId());
		List<OutputVO> ovoList;
		//根据三种情况返回不同的output信息
		/*
		 * 0 – 只查看stage有什么输出，比如在任务浏览页面
	   	 * 1 – 作为worker查看自己的output记录
         * 2 – 作为requester查看stage所有的output
		 */
		if(mode == 0){
			ovoList = oDAO.queryOutputListByActionAndIndicator(avo.getId(),OutputVO.OUTPUT_DESC);
		}
		else if(mode == 1){
			ovoList = oDAO.queryOutputListByActionAndUser(avo.getId(),userId,OutputVO.OUTPUT_VALUE);
		}
		else{
			ovoList = oDAO.queryOutputListByActionAndIndicator(avo.getId(),OutputVO.OUTPUT_VALUE);
		}
	
		linvo.setLvo(lvo);
		linvo.setIvoList(ivoList);
		linvo.setOvoList(ovoList);

		return linvo;
	}

	public static class ResultVO {
		private StageVO svo;
		private List<String> workers;
		private int stageStatus;
		private LocInputVO srcLinvo;
		private LocInputVO destLinvo;

		public StageVO getSvo() {
			return svo;
		}

		public void setSvo(StageVO svo) {
			this.svo = svo;
		}

		public List<String> getWorkers() {
			return workers;
		}

		public void setWorkers(List<String> workers) {
			this.workers = workers;
		}

		public int getStageStatus() {
			return stageStatus;
		}

		public void setStageStatus(int stageStatus) {
			this.stageStatus = stageStatus;
		}

		public LocInputVO getSrcLinvo() {
			return srcLinvo;
		}

		public void setSrcLinvo(LocInputVO srcLinvo) {
			this.srcLinvo = srcLinvo;
		}

		public LocInputVO getDestLinvo() {
			return destLinvo;
		}

		public void setDestLinvo(LocInputVO destLinvo) {
			this.destLinvo = destLinvo;
		}

	}

	public static class LocInputVO {
		private LocationVO lvo;
		private List<InputVO> ivoList;
		private List<OutputVO> ovoList;

		public LocationVO getLvo() {
			return lvo;
		}

		public void setLvo(LocationVO lvo) {
			this.lvo = lvo;
		}

		public List<InputVO> getIvoList() {
			return ivoList;
		}

		public void setIvoList(List<InputVO> ivoList) {
			this.ivoList = ivoList;
		}

		public List<OutputVO> getOvoList() {
			return ovoList;
		}

		public void setOvoList(List<OutputVO> ovoList) {
			this.ovoList = ovoList;
		}

	}
}
