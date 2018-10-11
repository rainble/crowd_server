package fudan.mcd.test;

import fudan.mcd.dao.abs.AbstractDAO;
import fudan.mcd.dao.impl.TemplateDAO;
import fudan.mcd.dao.impl.UserDAO;
import fudan.mcd.servlet.SaveTemplateServlet.*;
import fudan.mcd.servlet.ServletResponseData;
import fudan.mcd.utils.JSONUtils;
import fudan.mcd.vo.TemplateVO;
import fudan.mcd.vo.UserVO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TestSaveTemplateServlet {
	private static final String SERVLET_NAME = "SaveTemplateServlet";
	private UserDAO userDAO;
	private int userId;
	private TemplateDAO templateDAO;
	private int templateId;

	@Before
	public void init() {
		userDAO = new UserDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		UserVO userVO = JUnitTestUtils.generateUserVO();
		userId = userDAO.insert(userVO);
		templateDAO = new TemplateDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		TemplateVO templateVO = JUnitTestUtils.generateTemplateVO(userId);
		templateId = templateDAO.insert(templateVO);
	}

	@Test
	public void testInsertTemplateData() {
		TemplateVO templateVO = templateDAO.query(templateId);

		RequestBO requestBO = new RequestBO();
		requestBO.setTemplateId(-1);
		requestBO.setUserId(userId);
		requestBO.setName(templateVO.getName());
		requestBO.setDescription(templateVO.getDescription());
		requestBO.setHeat(templateVO.getHeat());
		requestBO.setUri(templateVO.getUri());
		requestBO.setCreateTime(templateVO.getCreateTime());
		requestBO.setTotalStageNum(templateVO.getTotalStageNum());
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", JSONUtils.toJSONString(requestBO));

		String response = HttpPostProxy.doPost(SERVLET_NAME, map);
		ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertEquals(1, responseData.getResult());
		ResponseBO responseBO = JSONUtils.toBean(responseData.getData(), ResponseBO.class);
		int newTemplatePK = responseBO.getTemplateID();
		assertTrue(newTemplatePK > 0);
		TemplateVO newTemplateVO = templateDAO.query(newTemplatePK);
		assertNotNull(newTemplateVO);

		assertEquals(templateVO.getUserId(), newTemplateVO.getUserId());
		assertEquals(templateVO.getName(), newTemplateVO.getName());
		assertEquals(templateVO.getDescription(), newTemplateVO.getDescription());
		assertEquals(templateVO.getHeat(), newTemplateVO.getHeat());
		assertEquals(templateVO.getUri(), newTemplateVO.getUri());
		JUnitTestUtils.assertTimestampEquals(templateVO.getCreateTime(), newTemplateVO.getCreateTime());
		assertEquals(templateVO.getTotalStageNum(), newTemplateVO.getTotalStageNum());

		templateDAO.delete(newTemplatePK);
	}

	@Test
	public void testUpdateTemplateData() {
		TemplateVO templateVO2 = JUnitTestUtils.generateTemplateVO(userId);
		int templatePK2 = templateDAO.insert(templateVO2);

		RequestBO requestBO = new RequestBO();
		requestBO.setTemplateId(templatePK2);
		requestBO.setUserId(templateVO2.getUserId());
		requestBO.setName(templateVO2.getName());
		requestBO.setDescription(templateVO2.getDescription());
		requestBO.setHeat(templateVO2.getHeat());
		requestBO.setUri(templateVO2.getUri());
		requestBO.setCreateTime(templateVO2.getCreateTime());
		requestBO.setTotalStageNum(templateVO2.getTotalStageNum());
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", JSONUtils.toJSONString(requestBO));

		String response = HttpPostProxy.doPost(SERVLET_NAME, map);
		ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertEquals(1, responseData.getResult());
		ResponseBO responseBO = JSONUtils.toBean(responseData.getData(), ResponseBO.class);
		int newTemplatePK = responseBO.getTemplateID();
		assertEquals(templatePK2, newTemplatePK);
		TemplateVO newTemplateVO = templateDAO.query(newTemplatePK);
		assertNotNull(newTemplateVO);

		assertEquals(templateVO2.getUserId(), newTemplateVO.getUserId());
		assertEquals(templateVO2.getName(), newTemplateVO.getName());
		assertEquals(templateVO2.getDescription(), newTemplateVO.getDescription());
		assertEquals(templateVO2.getHeat(), newTemplateVO.getHeat());
		assertEquals(templateVO2.getUri(), newTemplateVO.getUri());
		JUnitTestUtils.assertTimestampEquals(templateVO2.getCreateTime(), newTemplateVO.getCreateTime());
		assertEquals(templateVO2.getTotalStageNum(), newTemplateVO.getTotalStageNum());

		templateDAO.delete(templatePK2);
	}

	@Test
	public void testPostInvalidAccountData() {
		TemplateVO templateVO = templateDAO.query(templateId);

		RequestBO requestBO = new RequestBO();
		requestBO.setTemplateId(-1);
		requestBO.setUserId(JUnitTestUtils.INVALID_PK);
		requestBO.setName(templateVO.getName());
		requestBO.setDescription(templateVO.getDescription());
		requestBO.setHeat(templateVO.getHeat());
		requestBO.setUri(templateVO.getUri());
		requestBO.setCreateTime(templateVO.getCreateTime());
		requestBO.setTotalStageNum(templateVO.getTotalStageNum());
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", JSONUtils.toJSONString(requestBO));

		String response = HttpPostProxy.doPost(SERVLET_NAME, map);
		ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertTrue(responseData.getResult() < 0);
	}

	@Test
	public void testPostInvalidTemplateData() {
		TemplateVO templateVO = templateDAO.query(templateId);

		RequestBO requestBO = new RequestBO();
		requestBO.setTemplateId(templateVO.getId());
		requestBO.setUserId(JUnitTestUtils.INVALID_PK);
		requestBO.setName(templateVO.getName());
		requestBO.setDescription(templateVO.getDescription());
		requestBO.setHeat(templateVO.getHeat());
		requestBO.setUri(templateVO.getUri());
		requestBO.setCreateTime(templateVO.getCreateTime());
		requestBO.setTotalStageNum(templateVO.getTotalStageNum());
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
		templateDAO.delete(templateId);
		userDAO.delete(userId);
	}
}
