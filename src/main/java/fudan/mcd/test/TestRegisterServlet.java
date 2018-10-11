package fudan.mcd.test;

import fudan.mcd.dao.abs.AbstractDAO;
import fudan.mcd.dao.impl.UserDAO;
import fudan.mcd.service.RegisterService;
import fudan.mcd.servlet.RegisterServlet.*;
import fudan.mcd.servlet.ServletResponseData;
import fudan.mcd.utils.JSONUtils;
import fudan.mcd.vo.UserVO;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TestRegisterServlet {
	private static final String SERVLET_NAME = "RegisterServlet";
	private UserDAO dao;

	@Before
	public void init() {
		dao = new UserDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
	}

	@Test
	public void testPostCorrectData() {
		UserVO userVO = JUnitTestUtils.generateUserVO();

		RequestBO requestBO = new RequestBO();
		requestBO.setAccount(userVO.getAccount());
		requestBO.setPassword(userVO.getPassword());
		requestBO.setUserTag(userVO.getTag());
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", JSONUtils.toJSONString(requestBO));

		String response = HttpPostProxy.doPost(SERVLET_NAME, map);
		ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertEquals(RegisterService.RESULT_SUCCESS, responseData.getResult());
		ResponseBO responseBO = JSONUtils.toBean(responseData.getData(), ResponseBO.class);
		int pk = responseBO.getUserId();
		assertTrue(pk > 0);
		assertNotNull(dao.query(pk));

		dao.delete(pk);
	}

	@Test
	public void testPostExistedAccount() {
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
		assertEquals(RegisterService.RESULT_ACCOUNT_EXISTED, responseData.getResult());

		dao.delete(pk);
	}

	@Test
	public void testPostEmptyData() {
		Map<String, String> map = new HashMap<String, String>();
		String response = HttpPostProxy.doPost(SERVLET_NAME, map);
		ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertEquals(ServletResponseData.RESULT_PARSE_FAILED, responseData.getResult());
	}

}
