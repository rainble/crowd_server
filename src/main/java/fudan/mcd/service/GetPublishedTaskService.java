package fudan.mcd.service;

import fudan.mcd.dao.impl.TaskDAO;
import fudan.mcd.dao.impl.TemplateDAO;
import fudan.mcd.servlet.ServletUtils;
import fudan.mcd.vo.TaskVO;
import fudan.mcd.vo.TemplateVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;

public class GetPublishedTaskService extends AbstractService {

	private static final Log LOG = LogFactory.getLog(GetPublishedTaskService.class);

	public GetPublishedTaskService(ServletContext context) {
		super(context);
	}

	public List<ResultVO> getPublishedTask(int userId) {
		LOG.info(String.format("User [ %d ] request the published task list at [ %s ].", userId, ServletUtils.getTime()));
		List<ResultVO> result = new ArrayList<ResultVO>();
		// The DAO of Task and Template
		TaskDAO dao = new TaskDAO(context);
		TemplateDAO tpDao = new TemplateDAO(context);
		List<TaskVO> tasks = dao.queryTaskListByUser(userId);
		LOG.info(String.format("The number of user [ %d ] published task at [ %s ] is [ %d ].", userId, ServletUtils.getTime(),tasks.size()));
		for (TaskVO vo : tasks) {
			ResultVO rvo = new ResultVO();
			rvo.setTvo(vo);
			rvo.setTpvo(tpDao.query(vo.getTemplateId()));
			result.add(rvo);
		}
		return result;
	}

	public static class ResultVO {
		private TaskVO tvo;
		private TemplateVO tpvo;

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
	}

}
