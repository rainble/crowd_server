package fudan.mcd.service;

import fudan.mcd.dao.impl.TemplateDAO;
import fudan.mcd.servlet.ServletUtils;
import fudan.mcd.vo.TemplateVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import java.util.List;

public class GetDesignedTemplateService extends AbstractService {

	private static final Log LOG = LogFactory.getLog(GetPublishedTaskService.class);

	public GetDesignedTemplateService(ServletContext context) {
		super(context);
	}

	public List<TemplateVO> getDesignedTemplateService(int userId) {
		LOG.info(String.format("User [ %d ] request the designed template list at [ %s ].", userId, ServletUtils.getTime()));
		List<TemplateVO> result;
		TemplateDAO tpDAO = new TemplateDAO(context);
		result = tpDAO.queryTemplateListByUser(userId);
		LOG.info(String.format("The number of user [ %d ] designed template at [ %s ] is [ %d ].", userId, ServletUtils.getTime(),result.size()));
		return result;
	}

}
