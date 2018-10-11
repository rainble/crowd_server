package fudan.mcd.test;

import fudan.mcd.dao.abs.AbstractDAO;
import fudan.mcd.dao.impl.UserDAO;
import fudan.mcd.service.LoginService;
import fudan.mcd.servlet.LoginServlet.*;
import fudan.mcd.servlet.ServletResponseData;
import fudan.mcd.utils.JSONUtils;
import fudan.mcd.vo.UserVO;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TestLoginServlet {
	private static final String SERVLET_NAME = "LoginServlet";
	private UserDAO dao;

	@Before
	public void init() {
		dao = new UserDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
	}

	@Test
	public void testPostCorrectData() {
		UserVO userVO = JUnitTestUtils.generateUserVO();
		int pk = dao.insert(userVO);
		assertTrue(pk > 0);

		RequestBO requestBO = new RequestBO();
		requestBO.setAccount(userVO.getAccount());
		requestBO.setPassword(userVO.getPassword());
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", JSONUtils.toJSONString(requestBO));

		String response = HttpPostProxy.doPost(SERVLET_NAME, map);
		ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertEquals(LoginService.RESULT_SUCCESS, responseData.getResult());
		ResponseBO responseBO = JSONUtils.toBean(responseData.getData(), ResponseBO.class);
		assertEquals(pk, responseBO.getUserId());
		assertEquals(userVO.getPublishCredit(), responseBO.getCreditPublish(), JUnitTestUtils.FLOAT_ERROR);

		dao.delete(pk);
	}

	@Test
	public void testPostWrongPassword() {
		UserVO userVO = JUnitTestUtils.generateUserVO();
		int pk = dao.insert(userVO);
		assertTrue(pk > 0);

		RequestBO requestBO = new RequestBO();
		requestBO.setAccount(userVO.getAccount());
		requestBO.setPassword(JUnitTestUtils.generateTestString("wrong_password"));
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", JSONUtils.toJSONString(requestBO));

		String response = HttpPostProxy.doPost(SERVLET_NAME, map);
		ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertEquals(LoginService.RESULT_WRONG_PASSWORD, responseData.getResult());

		dao.delete(pk);
	}

	@Test
	public void testPostInvalidAccount() {
		RequestBO requestBO = new RequestBO();
		requestBO.setAccount(JUnitTestUtils.generateTestString("account"));
		requestBO.setPassword(JUnitTestUtils.generateTestString("password"));
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", JSONUtils.toJSONString(requestBO));

		String response = HttpPostProxy.doPost(SERVLET_NAME, map);
		ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertEquals(LoginService.RESULT_ACCOUNT_NOT_EXIST, responseData.getResult());
	}

	@Test
	public void testPostEmptyData() {
		Map<String, String> map = new HashMap<String, String>();
		String response = HttpPostProxy.doPost(SERVLET_NAME, map);
		ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertEquals(ServletResponseData.RESULT_PARSE_FAILED, responseData.getResult());
	}
}
