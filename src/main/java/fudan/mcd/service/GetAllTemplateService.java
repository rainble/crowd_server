package fudan.mcd.service;

import fudan.mcd.dao.impl.CollectionDAO;
import fudan.mcd.dao.impl.TemplateDAO;
import fudan.mcd.dao.impl.UserDAO;
import fudan.mcd.servlet.ServletUtils;
import fudan.mcd.vo.TemplateVO;
import fudan.mcd.vo.UserVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;

public class GetAllTemplateService extends AbstractService {
	private static final int COLLECT_NOT_YET = 0;
	private static final Log LOG = LogFactory.getLog(GetAllTemplateService.class);

	public GetAllTemplateService(ServletContext context) {
		super(context);
	}

	public List<ResultVO> getAllTemplate(int userId) {
		LOG.info(String.format("User [ %d ] request the uncollected template list at [ %s ].", userId, ServletUtils.getTime()));
		List<ResultVO> result = new ArrayList<ResultVO>();

		// DAO of the TemplateVO and UserVO
		TemplateDAO tpDAO = new TemplateDAO(context);
		CollectionDAO cDAO = new CollectionDAO(context);
		UserDAO userDAO = new UserDAO(context);

		// Set the return info
		List<TemplateVO> tpvoList = tpDAO.queryAllTemplateList();	
		List<TemplateVO> collectedTemplate = cDAO.queryTemplateListByUser(userId);
		
		for (TemplateVO tpvo : tpvoList) {
			if (isCollected(tpvo.getId(), collectedTemplate) == COLLECT_NOT_YET) {
				ResultVO rvo = new ResultVO();
				UserVO uvo = userDAO.query(tpvo.getUserId());
				rvo.setTpvo(tpvo);
				rvo.setUservo(uvo);

				result.add(rvo);
			}
		}
		LOG.info(String.format("Find [ %d ] record(s) of template uncollected by the user [ %d ]", result.size(),userId));
		return result;
	}
	
	public int isCollected(int templateId, List<TemplateVO> collectedTemplate) {
		int result = 0;
		for (TemplateVO tpvo : collectedTemplate) {
			if (tpvo.getId() == templateId) {
				result = 1;
				break;
			}
		}
		return result;
	}

	public static class ResultVO {
		private TemplateVO tpvo;
		private UserVO uservo;

		public TemplateVO getTpvo() {
			return tpvo;
		}

		public void setTpvo(TemplateVO tpvo) {
			this.tpvo = tpvo;
		}

		public UserVO getUservo() {
			return uservo;
		}

		public void setUservo(UserVO uservo) {
			this.uservo = uservo;
		}

	}

}
