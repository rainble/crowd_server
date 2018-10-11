package fudan.mcd.test;

import fudan.mcd.dao.abs.AbstractDAO;
import fudan.mcd.dao.impl.CollectionDAO;
import fudan.mcd.dao.impl.TemplateDAO;
import fudan.mcd.dao.impl.UserDAO;
import fudan.mcd.service.CollectTemplateService;
import fudan.mcd.servlet.CollectTemplateServlet.*;
import fudan.mcd.servlet.ServletResponseData;
import fudan.mcd.utils.JSONUtils;
import fudan.mcd.vo.CollectionVO;
import fudan.mcd.vo.TemplateVO;
import fudan.mcd.vo.UserVO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TestCollectTemplateServlet {
	private static final String SERVLET_NAME = "CollectTemplateServlet";
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
	public void testPostInsertAndDeleteData() {
		// Delete
		RequestBO requestBO = new RequestBO();
		requestBO.setUserId(userId);
		requestBO.setTemplateId(templateId);
		requestBO.setIndicator(CollectTemplateService.UNCOLLECT);
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", JSONUtils.toJSONString(requestBO));

		String response = HttpPostProxy.doPost(SERVLET_NAME, map);
		ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertEquals(1, responseData.getResult());
		ResponseBO responseBO = JSONUtils.toBean(responseData.getData(), ResponseBO.class);
		int newCollectionId = responseBO.getCollectionId();
		assertTrue(newCollectionId > 0);
		assertEquals(collectionId, newCollectionId);
		assertNull(collectionDAO.query(newCollectionId));

		// Insert
		requestBO.setUserId(userId);
		requestBO.setTemplateId(templateId);
		requestBO.setIndicator(CollectTemplateService.COLLECT);
		map.put("data", JSONUtils.toJSONString(requestBO));

		response = HttpPostProxy.doPost(SERVLET_NAME, map);
		responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertEquals(1, responseData.getResult());
		responseBO = JSONUtils.toBean(responseData.getData(), ResponseBO.class);
		newCollectionId = responseBO.getCollectionId();
		assertTrue(newCollectionId > 0);
		CollectionVO newCollectionVO = collectionDAO.query(newCollectionId);
		assertNotNull(newCollectionVO);
		collectionDAO.delete(newCollectionId);
	}

	@Test
	public void testPostInvalidAccount() {
		RequestBO requestBO = new RequestBO();
		requestBO.setUserId(JUnitTestUtils.INVALID_PK);
		requestBO.setTemplateId(templateId);
		requestBO.setIndicator(CollectTemplateService.UNCOLLECT);
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", JSONUtils.toJSONString(requestBO));

		String response = HttpPostProxy.doPost(SERVLET_NAME, map);
		ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertTrue(responseData.getResult() < 0);
	}

	@Test
	public void testPostInvalidTemplate() {
		RequestBO requestBO = new RequestBO();
		requestBO.setUserId(userId);
		requestBO.setTemplateId(JUnitTestUtils.INVALID_PK);
		requestBO.setIndicator(CollectTemplateService.UNCOLLECT);
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", JSONUtils.toJSONString(requestBO));

		String response = HttpPostProxy.doPost(SERVLET_NAME, map);
		ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertTrue(responseData.getResult() < 0);
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
