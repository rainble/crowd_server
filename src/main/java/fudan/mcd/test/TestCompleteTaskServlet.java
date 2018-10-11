package fudan.mcd.test;

import fudan.mcd.dao.abs.AbstractDAO;
import fudan.mcd.dao.impl.*;
import fudan.mcd.servlet.CompleteTaskServlet.*;
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

public class TestCompleteTaskServlet {
	private static final String SERVLET_NAME = "CompleteTaskServlet";
	private int userId;
	private UserDAO userDAO;
	private int templateId;
	private TemplateDAO templateDAO;
	private int taskId;
	private TaskDAO taskDAO;
	private int stageId;
	private StageDAO stageDAO;
	private int locationSrcId;
	private int locationDstId;
	private LocationDAO locationDAO;
	private int actionId;
	private ActionDAO actionDAO;
	private int inputId;
	private InputDAO inputDAO;
	private int outputId;
	private OutputDAO outputDAO;
	private int undertakeId;
	private UndertakeDAO undertakeDAO;

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
		taskId = taskDAO.insert(taskVO);
		stageDAO = new StageDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		StageVO stageVO = JUnitTestUtils.generateStageVO(taskId);
		stageId = stageDAO.insert(stageVO);
		locationDAO = new LocationDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		LocationVO locationSrcVO = JUnitTestUtils.generateLocationVO(stageId);
		locationSrcVO.setType(LocationVO.TYPE_SRC);
		locationSrcId = locationDAO.insert(locationSrcVO);
		LocationVO locationDstVO = JUnitTestUtils.generateLocationVO(stageId);
		locationDstVO.setType(LocationVO.TYPE_DEST);
		locationDstId = locationDAO.insert(locationDstVO);
		actionDAO = new ActionDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		ActionVO actionVO = JUnitTestUtils.generateActionVO(locationSrcId);
		actionId = actionDAO.insert(actionVO);
		inputDAO = new InputDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		InputVO inputVO = JUnitTestUtils.generateInputVO(actionId);
		inputId = inputDAO.insert(inputVO);
		outputDAO = new OutputDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		OutputVO outputVO = JUnitTestUtils.generateEnumOutputVO(actionId, userId);
		outputId = outputDAO.insert(outputVO);
		undertakeDAO = new UndertakeDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		UndertakeVO undertakeVO = JUnitTestUtils.generateUndertakeVO(userId, stageId);
		undertakeVO.setStatus(UndertakeVO.STATUS_ONGOING);
		undertakeId = undertakeDAO.insert(undertakeVO);
	}

	@Test
	public void testPostCorrectData() {
		RequestBO requestBO = new RequestBO();
		requestBO.setUserId(userId);
		requestBO.setStageId(stageId);
		requestBO.setFinishTime(new Timestamp(System.currentTimeMillis()));
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", JSONUtils.toJSONString(requestBO));

		String response = HttpPostProxy.doPost(SERVLET_NAME, map);
		ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertEquals(1, responseData.getResult());
		ResponseBO responseBO = JSONUtils.toBean(responseData.getData(), ResponseBO.class);

		int resultUndertakeId = responseBO.getUndertakeId();
		assertTrue(resultUndertakeId > 0);
		assertEquals(undertakeId, resultUndertakeId);
		UndertakeVO resultUndertakeVO = undertakeDAO.query(resultUndertakeId);
		assertEquals(UndertakeVO.STATUS_FINISHED, resultUndertakeVO.getStatus());
		JUnitTestUtils.assertTimestampEquals(requestBO.getFinishTime(), resultUndertakeVO.getEndTime());
	}

	@Test
	public void testPostInvalidAccount() {
		RequestBO requestBO = new RequestBO();
		requestBO.setUserId(JUnitTestUtils.INVALID_PK);
		requestBO.setStageId(stageId);
		requestBO.setFinishTime(new Timestamp(System.currentTimeMillis()));
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", JSONUtils.toJSONString(requestBO));

		String response = HttpPostProxy.doPost(SERVLET_NAME, map);
		ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertEquals(-1, responseData.getResult());
	}

	@Test
	public void testPostInvalidStage() {
		RequestBO requestBO = new RequestBO();
		requestBO.setUserId(userId);
		requestBO.setStageId(JUnitTestUtils.INVALID_PK);
		requestBO.setFinishTime(new Timestamp(System.currentTimeMillis()));
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", JSONUtils.toJSONString(requestBO));

		String response = HttpPostProxy.doPost(SERVLET_NAME, map);
		ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertEquals(-1, responseData.getResult());
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
		undertakeDAO.delete(undertakeId);
		inputDAO.delete(inputId);
		outputDAO.delete(outputId, EnumOutputVO.class);
		actionDAO.delete(actionId);
		locationDAO.delete(locationSrcId);
		locationDAO.delete(locationDstId);
		stageDAO.delete(stageId);
		taskDAO.delete(taskId);
		templateDAO.delete(templateId);
		userDAO.delete(userId);
	}

}
