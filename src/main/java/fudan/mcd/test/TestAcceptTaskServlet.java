package fudan.mcd.test;

import fudan.mcd.dao.abs.AbstractDAO;
import fudan.mcd.dao.impl.*;
import fudan.mcd.servlet.AcceptTaskServlet.*;
import fudan.mcd.servlet.ServletResponseData;
import fudan.mcd.utils.JSONUtils;
import fudan.mcd.vo.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TestAcceptTaskServlet {
	private static final String SERVLET_NAME = "AcceptTaskServlet";
	private int userId;
	private UserDAO userDAO;
	private int templateId;
	private TemplateDAO templateDAO;
	private int taskId;
	private TaskDAO taskDAO;
	private int stageId;
	private StageDAO stageDAO;

	@Before
	public void init() {
		userDAO = new UserDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		UserVO userVO = JUnitTestUtils.generateUserVO();
		userId = userDAO.insert(userVO);
		templateDAO = new TemplateDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		TemplateVO templateVO = JUnitTestUtils.generateTemplateVO(userId);
		templateId = templateDAO.insert(templateVO);
		taskDAO = new TaskDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		TaskVO taskVO = JUnitTestUtils.generateTaskVO(templateId, userId);
		taskVO.setStatus(TaskVO.STATUS_ONGOING);
		taskId = taskDAO.insert(taskVO);
		stageDAO = new StageDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		StageVO stageVO = JUnitTestUtils.generateStageVO(taskId);
		stageVO.setIndex(taskVO.getCurrentStage());
		stageId = stageDAO.insert(stageVO);
	}

	@Test
	public void testPostCorrectData() {
		TaskVO taskVO = taskDAO.query(taskId);

		RequestBO requestBO = new RequestBO();
		requestBO.setTaskId(taskId);
		requestBO.setUserId(userId);
		requestBO.setCurrentStage(taskVO.getCurrentStage());
		requestBO.setStartTime(new Timestamp(System.currentTimeMillis()));
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", JSONUtils.toJSONString(requestBO));
		String response = HttpPostProxy.doPost(SERVLET_NAME, map);
		ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertEquals(1, responseData.getResult());
		ResponseBO responseBO = JSONUtils.toBean(responseData.getData(), ResponseBO.class);
		assertNotNull(responseBO);

		UndertakeDAO undertakeDAO = new UndertakeDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		UndertakeVO undertakeVO = undertakeDAO.queryByUserAndStage(userId, stageId);
		assertNotNull(undertakeVO);
		JUnitTestUtils.assertTimestampEquals(requestBO.getStartTime(), undertakeVO.getStartTime());
		undertakeDAO.delete(undertakeVO.getId());
	}

	@Test
	public void testPostTaskData() {
		TaskVO taskVO = taskDAO.query(taskId);

		RequestBO requestBO = new RequestBO();
		requestBO.setTaskId(JUnitTestUtils.INVALID_PK);
		requestBO.setUserId(userId);
		requestBO.setCurrentStage(taskVO.getCurrentStage());
		requestBO.setStartTime(new Timestamp(System.currentTimeMillis()));
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", JSONUtils.toJSONString(requestBO));
		String response = HttpPostProxy.doPost(SERVLET_NAME, map);
		ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertEquals(-1, responseData.getResult());
		ResponseBO responseBO = JSONUtils.toBean(responseData.getData(), ResponseBO.class);
		assertNotNull(responseBO);
	}

	@Test
	public void testPostInvalidAccountData() {
		TaskVO taskVO = taskDAO.query(taskId);

		RequestBO requestBO = new RequestBO();
		requestBO.setTaskId(taskId);
		requestBO.setUserId(JUnitTestUtils.INVALID_PK);
		requestBO.setCurrentStage(taskVO.getCurrentStage());
		requestBO.setStartTime(new Timestamp(System.currentTimeMillis()));
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", JSONUtils.toJSONString(requestBO));
		String response = HttpPostProxy.doPost(SERVLET_NAME, map);
		ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertEquals(-1, responseData.getResult());
		ResponseBO responseBO = JSONUtils.toBean(responseData.getData(), ResponseBO.class);
		assertNotNull(responseBO);
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
		stageDAO.delete(stageId);
		taskDAO.delete(taskId);
		templateDAO.delete(templateId);
		userDAO.delete(userId);
	}

}
