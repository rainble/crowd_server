package fudan.mcd.test;

import fudan.mcd.dao.abs.AbstractDAO;
import fudan.mcd.dao.impl.CollectionDAO;
import fudan.mcd.dao.impl.TemplateDAO;
import fudan.mcd.dao.impl.UserDAO;
import fudan.mcd.servlet.SearchTemplateServlet.*;
import fudan.mcd.servlet.SearchTemplateServlet.ResponseBO.*;
import fudan.mcd.servlet.ServletResponseData;
import fudan.mcd.utils.JSONUtils;
import fudan.mcd.vo.CollectionVO;
import fudan.mcd.vo.TemplateVO;
import fudan.mcd.vo.UserVO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TestSearchTemplateServlet {
	private static final String SERVLET_NAME = "SearchTemplateServlet";
	private UserDAO userDAO;
	private int userId;
	private TemplateDAO templateDAO;
	private int templateId;
	private CollectionDAO collectionDAO;
	private int collectionId;

	@Before
	public void init() {
		userDAO = new UserDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		UserVO userVO = JUnitTestUtils.generateUserVO();
		userId = userDAO.insert(userVO);
		templateDAO = new TemplateDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		TemplateVO templateVO = JUnitTestUtils.generateTemplateVO(userId);
		templateId = templateDAO.insert(templateVO);
		collectionDAO = new CollectionDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		CollectionVO collectionVO = JUnitTestUtils.generateCollectionVO(userId, templateId);
		collectionId = collectionDAO.insert(collectionVO);
	}

	@Test
	public void testPostData() {
		TemplateVO templateVO2 = JUnitTestUtils.generateTemplateVO(userId);
		templateVO2.setName(JUnitTestUtils.generateTestString("search_name"));
		int templatePK2 = templateDAO.insert(templateVO2);

		try {
			RequestBO requestBO = new RequestBO();
			requestBO.setUserId(userId);
			requestBO.setTemplateName("search");
			Map<String, String> map = new HashMap<String, String>();
			map.put("data", JSONUtils.toJSONString(requestBO));

			String response = HttpPostProxy.doPost(SERVLET_NAME, map);
			ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
			assertEquals(1, responseData.getResult());
			ResponseBO responseBO = JSONUtils.toBean(responseData.getData(), ResponseBO.class);
			List<TemplateBO> templateBOList = responseBO.getTemplates();
			assertTrue(templateBOList.size() >= 1);

			for (TemplateBO templateBO : templateBOList) {
				if (templateBO.getTemplateId() == templatePK2) {
					assertEquals(templatePK2, templateBO.getTemplateId());
					assertEquals(templateVO2.getName(), templateBO.getName());
					assertEquals(templateVO2.getUri(), templateBO.getUri());
					JUnitTestUtils.assertTimestampEquals(templateVO2.getCreateTime(), templateBO.getCreateTime());
					assertEquals(templateVO2.getTotalStageNum(), templateBO.getTotalStageNum());
					assertEquals(templateVO2.getHeat(), templateBO.getHeat());
					assertEquals(templateVO2.getDescription(), templateBO.getDescription());
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			templateDAO.delete(templatePK2);
		}
	}

	@After
	public void recycle() {
		collectionDAO.delete(collectionId);
		templateDAO.delete(templateId);
		userDAO.delete(userId);
	}
}
