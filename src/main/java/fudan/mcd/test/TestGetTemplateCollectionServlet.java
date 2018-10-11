package fudan.mcd.test;

import fudan.mcd.dao.abs.AbstractDAO;
import fudan.mcd.dao.impl.CollectionDAO;
import fudan.mcd.dao.impl.TemplateDAO;
import fudan.mcd.dao.impl.UserDAO;
import fudan.mcd.servlet.GetTemplateCollectionServlet.*;
import fudan.mcd.servlet.GetTemplateCollectionServlet.ResponseBO.*;
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

public class TestGetTemplateCollectionServlet {
	private static final String SERVLET_NAME = "GetTemplateCollectionServlet";
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
	public void testPostCorrectAccount() {
		TemplateVO templateVO2 = JUnitTestUtils.generateTemplateVO(userId);
		int templatePK2 = templateDAO.insert(templateVO2);
		CollectionVO collectionVO2 = JUnitTestUtils.generateCollectionVO(userId, templatePK2);
		int collectionPK2 = collectionDAO.insert(collectionVO2);

		RequestBO requestBO = new RequestBO();
		requestBO.setUserId(userId);
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", JSONUtils.toJSONString(requestBO));

		String response = HttpPostProxy.doPost(SERVLET_NAME, map);
		ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertEquals(1, responseData.getResult());
		ResponseBO responseBO = JSONUtils.toBean(responseData.getData(), ResponseBO.class);
		List<TemplateBO> templateBOList = responseBO.getTemplates();
		assertEquals(2, templateBOList.size());

		for (TemplateBO templateBO : templateBOList) {
			if (templateBO.getTemplateId() == templatePK2) {
				assertEquals(templatePK2, templateBO.getTemplateId());
				assertEquals(templateVO2.getName(), templateBO.getName());
				assertEquals(templateVO2.getUri(), templateBO.getUri());
				JUnitTestUtils.assertTimestampEquals(templateVO2.getCreateTime(), templateBO.getCreateTime());
				assertEquals(templateVO2.getTotalStageNum(), templateBO.getTotalStageNum());
				assertEquals(templateVO2.getHeat(), templateBO.getHeat());
				assertEquals(templateVO2.getDescription(), templateBO.getDescription());
				UserVO creator = userDAO.query(templateVO2.getUserId());
				assertNotNull(creator);
				assertEquals(creator.getAccount(), templateBO.getCreater());
			}
		}

		collectionDAO.delete(collectionPK2);
		templateDAO.delete(templatePK2);
	}

	@Test
	public void testPostInvalidAccount() {
		RequestBO requestBO = new RequestBO();
		requestBO.setUserId(JUnitTestUtils.INVALID_PK);
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", JSONUtils.toJSONString(requestBO));

		String response = HttpPostProxy.doPost(SERVLET_NAME, map);
		ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertEquals(1, responseData.getResult());
		ResponseBO responseBO = JSONUtils.toBean(responseData.getData(), ResponseBO.class);
		List<TemplateBO> templateBOList = responseBO.getTemplates();
		assertEquals(0, templateBOList.size());
	}

	@Test
	public void testPostEmptyData() {
		Map<String, String> map = new HashMap<String, String>();
		String response = HttpPostProxy.doPost(SERVLET_NAME, map);
		ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertEquals(ServletResponseData.RESULT_PARSE_FAILED, responseData.getResult());
	}

	@After
	public void recycle() {
		collectionDAO.delete(collectionId);
		templateDAO.delete(templateId);
		userDAO.delete(userId);
	}
}
