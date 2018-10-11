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

public class SearchTemplateService extends AbstractService {

	private static final int COLLECT_NOT_YET = 0;
	private static final Log LOG = LogFactory.getLog(SearchTemplateService.class);

	public SearchTemplateService(ServletContext context) {
		super(context);
	}

	public List<ResultVO> searchTemplate(int userId, String templateName) {
		LOG.info(String.format("User [ %d ] search the template by [ keyword:%s ] at [ %s ].", userId, templateName, ServletUtils.getTime()));
		List<ResultVO> rvoList = new ArrayList<ResultVO>();

		// DAO of Template, Undertake and UserDAO
		TemplateDAO tpDAO = new TemplateDAO(context);
		CollectionDAO uDAO = new CollectionDAO(context);
		UserDAO userDAO = new UserDAO(context);

		// Get the needed template info
		List<TemplateVO> allRelatedTemplate = tpDAO.queryTemplateListContainingName(templateName);

		LOG.info(String.format("The size of matched template is [ %d ]", allRelatedTemplate.size()));

		List<TemplateVO> collectedTemplate = uDAO.queryTemplateListByUser(userId);

		for (TemplateVO tpvo : allRelatedTemplate) {
			if (isCollected(tpvo.getId(), collectedTemplate) == COLLECT_NOT_YET) {
				ResultVO rvo = new ResultVO();
				rvo.setTpvo(tpvo);
				rvo.setUservo(userDAO.query(tpvo.getUserId()));
				rvoList.add(rvo);
			}
		}
		return rvoList;
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
