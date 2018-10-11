package fudan.mcd.service;

import fudan.mcd.dao.impl.*;
import fudan.mcd.servlet.ServletUtils;
import fudan.mcd.vo.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class GetAcceptedTaskService extends AbstractService {
	private static final Log LOG = LogFactory.getLog(GetAcceptedTaskService.class);
	private static final int STAGE_ONGOING = UndertakeVO.STATUS_ONGOING;
	private static final int LOCATION_SRC = LocationVO.TYPE_SRC, LOCATION_DEST = LocationVO.TYPE_DEST;

	public GetAcceptedTaskService(ServletContext context) {
		super(context);
	}

	public List<ResultVO> getAcceptedTask(int userId) {
		LOG.info(String.format("User [ %d ] request the accepted task list at [ %s ].", userId, ServletUtils.getTime()));
		List<ResultVO> rvoList = new ArrayList<ResultVO>();

		// DAO of the Undertake, Stage, Task, Template
		UndertakeDAO uDAO = new UndertakeDAO(context);
		TaskDAO tDAO = new TaskDAO(context);
		TemplateDAO tpDAO = new TemplateDAO(context);
		LocationDAO lDAO = new LocationDAO(context);

		// Get the StageVO list and then get the need info
		List<StageVO> svoList = uDAO.queryStageListByUserAndStatus(userId, STAGE_ONGOING);
		LOG.info(String.format("Find [ %d ] record(s) of stage accepted by the user [ %d ]", svoList.size(),userId));

		for (StageVO svo : svoList) {
			ResultVO rvo = new ResultVO();

			TaskVO tvo = tDAO.query(svo.getTaskId());
			TemplateVO tpvo = tpDAO.query(tvo.getTemplateId());
			// Get the info about start location and the input
			LocationVO src = lDAO.queryByStageAndType(svo.getId(), LOCATION_SRC);
			LocationVO dest = lDAO.queryByStageAndType(svo.getId(), LOCATION_DEST);
			LocInputVO srcLinvo = null;
			LocInputVO destLinvo = null;
			if (src != null)
				srcLinvo = getInputByLocation(src);
			if (dest != null)
				destLinvo = getInputByLocation(dest);

			//查询并返回contract
			UndertakeVO uvo = uDAO.queryByUserAndStage(userId, svo.getId());
			if(uvo != null)
				svo.setContract(uvo.getContractTime());
			else{
				String defaultTimeString = "0000-00-00 00:00:00";
				Timestamp defaultTime = Timestamp.valueOf(defaultTimeString);
				svo.setContract(defaultTime);
			}
			
			// Set the info of ResultVO
			rvo.setTvo(tvo);
			rvo.setTpvo(tpvo);
			rvo.setSvo(svo);
			rvo.setSrcLinvo(srcLinvo);
			rvo.setDestLinvo(destLinvo);

			rvoList.add(rvo);
		}
		return rvoList;
	}

	public LocInputVO getInputByLocation(LocationVO lvo) {
		LocInputVO linvo = new LocInputVO();

		// DAO of the Action and Input
		ActionDAO aDAO = new ActionDAO(context);
		InputDAO iDAO = new InputDAO(context);
		OutputDAO oDAO = new OutputDAO(context);

		// Get the input list
		List<ActionVO> actionList = aDAO.queryActionListByLocation(lvo.getId());
		if (actionList.size() > 0) {
			ActionVO avo = aDAO.queryActionListByLocation(lvo.getId()).get(0);
			List<InputVO> ivoList = iDAO.queryInputListByAction(avo.getId());
			List<OutputVO> ovoList = oDAO.queryOutputListByActionAndIndicator(avo.getId(),OutputVO.OUTPUT_DESC);

			linvo.setLvo(lvo);
			linvo.setIvoList(ivoList);
			linvo.setOvoList(ovoList);
		}
		return linvo;
	}

	public static class ResultVO {
		private TaskVO tvo;
		private TemplateVO tpvo;
		private StageVO svo;
		private LocInputVO srcLinvo;
		private LocInputVO destLinvo;

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

		public StageVO getSvo() {
			return svo;
		}

		public void setSvo(StageVO svo) {
			this.svo = svo;
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
