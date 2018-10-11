package fudan.mcd.service;

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
import java.util.ArrayList;
import java.util.List;

public class GetCompletedTaskService extends AbstractService {
	private static final Log LOG = LogFactory.getLog(GetCompletedTaskService.class);
	private static final int STAGE_EXPIRED = UndertakeVO.STATUS_EXPIRED, STAGE_FINISHED = UndertakeVO.STATUS_FINISHED,STAGE_CREDIT_GIVEN = UndertakeVO.STATUS_CREDIT_GIVEN;

	public GetCompletedTaskService(ServletContext context) {
		super(context);
	}

	public List<ResultVO> getCompletedTask(int userId) {
		LOG.info(String.format("User [ %d ] request the completed/expired task list at [ %s ].", userId, ServletUtils.getTime()));
		List<ResultVO> rvoList = new ArrayList<ResultVO>();

		// DAO of the Task, Template and Undertake
		UndertakeDAO uDAO = new UndertakeDAO(context);
		TaskDAO tDAO = new TaskDAO(context);
		TemplateDAO tpDAO = new TemplateDAO(context);

		// Get the StageVO list and then get the need info
		List<StageVO> svoList = new ArrayList<StageVO>();
		List<StageVO> svoExpiredList = uDAO.queryStageListByUserAndStatus(userId, STAGE_EXPIRED);
		List<StageVO> svoFinishedList = uDAO.queryStageListByUserAndStatus(userId, STAGE_FINISHED);
		List<StageVO> svoCreditGivenList = uDAO.queryStageListByUserAndStatus(userId, STAGE_CREDIT_GIVEN);
		svoList.addAll(svoExpiredList);
		svoList.addAll(svoFinishedList);
		svoList.addAll(svoCreditGivenList);
		LOG.info(String.format("The number of user [ %d ] completed task at [ %s ] is [ %d ].", userId, ServletUtils.getTime(),svoFinishedList.size()));
		LOG.info(String.format("The number of user [ %d ] confirmed task at [ %s ] is [ %d ].", userId, ServletUtils.getTime(),svoCreditGivenList.size()));
		LOG.info(String.format("The number of user [ %d ] expired task at [ %s ] is [ %d ].", userId, ServletUtils.getTime(),svoExpiredList.size()));
		LOG.info(String.format("The total number of user [ %d ] completed/expired task at [ %s ] is [ %d ].", userId, ServletUtils.getTime(),svoList.size()));

		for (StageVO svo : svoList) {
			ResultVO rvo = new ResultVO();

			TaskVO tvo = tDAO.query(svo.getTaskId());
			TemplateVO tpvo = tpDAO.query(tvo.getTemplateId());
			UndertakeVO uvo = uDAO.queryByUserAndStage(userId, svo.getId());

			rvo.setTvo(tvo);
			rvo.setTpvo(tpvo);
			rvo.setSvo(svo);
			rvo.setUvo(uvo);

			rvoList.add(rvo);
		}
		return rvoList;
	}

	public static class ResultVO {
		private TaskVO tvo;
		private TemplateVO tpvo;
		private StageVO svo;
		private UndertakeVO uvo;

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

		public UndertakeVO getUvo() {
			return uvo;
		}

		public void setUvo(UndertakeVO uvo) {
			this.uvo = uvo;
		}

	}

}
