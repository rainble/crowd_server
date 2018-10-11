package fudan.mcd.service;

import fudan.mcd.dao.impl.TemplateDAO;
import fudan.mcd.dao.impl.UserDAO;
import fudan.mcd.servlet.ServletUtils;
import fudan.mcd.vo.TemplateVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;

public class SaveTemplateService extends AbstractService {
	private static final Log LOG = LogFactory.getLog(SaveTemplateService.class);
	public static final int SAVE_SUCCESS = 1, SAVE_FAIL = -1;
	private int pk;

	public SaveTemplateService(ServletContext context) {
		super(context);
		pk = -1;
	}

	public int saveTemplate(TemplateVO tpvo) {
		// The DAO of Template
		TemplateDAO tpDAO = new TemplateDAO(context);
		// Check user id
		UserDAO userDAO = new UserDAO(context);
		if (userDAO.query(tpvo.getUserId()) == null) {
			return SAVE_FAIL;
		}
		// Insert or update the template according to the id
		if (tpvo.getId() == -1) {
			pk = tpDAO.insert(tpvo);
			if (pk > 0)
				LOG.info(String.format("User [ %d ] create the template [ %d ] at [ %s ] successfully.", tpvo.getUserId(), pk, ServletUtils.getTime()));
		}
		else {
			// Check template id
			if (tpDAO.query(tpvo.getId()) == null)
				return SAVE_FAIL;
			int result = tpDAO.update(tpvo);
			if (result > 0) {
				pk = tpvo.getId();
				LOG.info(String.format("User [ %d ] update the template [ %d ] at [ %s ] successfully.", tpvo.getUserId(), pk, ServletUtils.getTime()));
			}
		}
		if (pk > 0) {
			return SAVE_SUCCESS;
		}
		else {
			LOG.info(String.format("User [ %d ] fail to create/update the template [ %d ] at [ %s ]: database error.", tpvo.getUserId(), pk, ServletUtils.getTime()));
			return SAVE_FAIL;
		}
	}

	public int getPrimaryKey() {
		return pk;
	}

}
