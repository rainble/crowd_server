package fudan.mcd.service;

import fudan.mcd.dao.impl.CollectionDAO;
import fudan.mcd.dao.impl.TemplateDAO;
import fudan.mcd.dao.impl.UserDAO;
import fudan.mcd.servlet.ServletUtils;
import fudan.mcd.vo.CollectionVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;

public class CollectTemplateService extends AbstractService {
	public static final int COLLECT = 1, UNCOLLECT = -1;
	public static final int COLLECT_SUCCESS = 1, COLLECT_FAIL = -1;
	private static final Log LOG = LogFactory.getLog(CollectTemplateService.class);
	private int pk;

	public CollectTemplateService(ServletContext context) {
		super(context);
		pk = -1;
	}

	public int collectTemplate(CollectionVO collectionVO, int indicator) {
		int result;
		// Check user id
		UserDAO userDAO = new UserDAO(context);
		if (userDAO.query(collectionVO.getUserId()) == null)
			return COLLECT_FAIL;
		// Check template id
		TemplateDAO templateDAO = new TemplateDAO(context);
		if (templateDAO.query(collectionVO.getTemplateId()) == null)
			return COLLECT_FAIL;
		// DAO of CollectionVO
		CollectionDAO collectionDAO = new CollectionDAO(context);
		if (indicator == COLLECT) {
			result = collectionDAO.insert(collectionVO);
			pk = result;
			if (result > 0){
				LOG.info(String.format("User [ %d ] collect the template [ %d ] successfully at [ %s ].", collectionVO.getUserId(),
						collectionVO.getTemplateId(), ServletUtils.getTime()));
				return COLLECT_SUCCESS;
			}
			else{
				LOG.info(String.format("User [ %d ] fail to collect the template [ %d ] at [ %s ].", collectionVO.getUserId(),
						collectionVO.getTemplateId(), ServletUtils.getTime()));
				return COLLECT_FAIL;
			}
		}
		else if (indicator == UNCOLLECT) {
			CollectionVO tempVO = collectionDAO.deleteByUserAndTemplate(collectionVO.getUserId(), collectionVO.getTemplateId());
			if(tempVO != null){
				LOG.info(String.format("User [ %d ] delete the collection of the template [ %d ] successfully at [ %s ].", collectionVO.getUserId(),
						collectionVO.getTemplateId(), ServletUtils.getTime()));
				pk = tempVO.getId();
				return COLLECT_SUCCESS;
			}
			else{
				LOG.info(String.format("User [ %d ] fail to delete the collection of the template [ %d ] at [ %s ].", collectionVO.getUserId(),
						collectionVO.getTemplateId(), ServletUtils.getTime()));
				return COLLECT_FAIL;
			}
		}
		else {
			return COLLECT_FAIL;
		}
	}

	public int getCollectionPrimaryKey() {
		return pk;
	}
}
