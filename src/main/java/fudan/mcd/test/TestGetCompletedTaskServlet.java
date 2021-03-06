package fudan.mcd.test;

import fudan.mcd.dao.abs.AbstractDAO;
import fudan.mcd.dao.impl.*;
import fudan.mcd.servlet.GetCompletedTaskServlet.*;
import fudan.mcd.servlet.GetCompletedTaskServlet.ResponseBO.*;
import fudan.mcd.servlet.ServletResponseData;
import fudan.mcd.utils.JSONUtils;
import fudan.mcd.vo.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class TestGetCompletedTaskServlet {
	private static final String SERVLET_NAME = "GetCompletedTaskServlet";
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
		undertakeVO.setStatus(UndertakeVO.STATUS_FINISHED);
		undertakeId = undertakeDAO.insert(undertakeVO);
	}

	@Test
	public void testPostCorrectAccount() {
		TaskVO taskVO2 = JUnitTestUtils.generateTaskVO(templateId, userId);
		int taskPK2 = taskDAO.insert(taskVO2);
		StageVO stageVO2 = JUnitTestUtils.generateStageVO(taskPK2);
		int stagePK2 = stageDAO.insert(stageVO2);
		UndertakeVO undertakeVO2 = JUnitTestUtils.generateUndertakeVO(userId, stagePK2);
		undertakeVO2.setStatus(UndertakeVO.STATUS_EXPIRED);
		int undertakePK2 = undertakeDAO.insert(undertakeVO2);

		TaskVO taskVO3 = JUnitTestUtils.generateTaskVO(templateId, userId);
		int taskPK3 = taskDAO.insert(taskVO3);
		StageVO stageVO3 = JUnitTestUtils.generateStageVO(taskPK3);
		int stagePK3 = stageDAO.insert(stageVO3);
		UndertakeVO undertakeVO3 = JUnitTestUtils.generateUndertakeVO(userId, stagePK3);
		undertakeVO3.setStatus(UndertakeVO.STATUS_ONGOING);
		int undertakePK3 = undertakeDAO.insert(undertakeVO3);

		try {
			RequestBO requestBO = new RequestBO();
			requestBO.setUserId(userId);
			Map<String, String> map = new HashMap<String, String>();
			map.put("data", JSONUtils.toJSONString(requestBO));

			String response = HttpPostProxy.doPost(SERVLET_NAME, map);
			ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
			assertEquals(1, responseData.getResult());
			ResponseBO responseBO = JSONUtils.toBean(responseData.getData(), ResponseBO.class);
			List<TaskBO> taskBOList = responseBO.getTasks();
			assertEquals(2, taskBOList.size());

			TaskVO taskVO = taskDAO.query(taskId);
			StageVO stageVO = stageDAO.query(stageId);
			UndertakeVO undertakeVO = undertakeDAO.query(undertakeId);
			for (TaskBO taskBO : taskBOList) {
				if (taskBO.getTaskId() == taskId) {
					assertEquals(taskVO.getTitle(), taskBO.getTaskTitle());
					assertEquals(taskVO.getDescription(), taskBO.getTaskDesc());
					assertEquals(String.format("%d/%d", taskVO.getCurrentStage(), templateDAO.query(templateId).getTotalStageNum()),
							taskBO.getTaskProgress());
					assertEquals(taskVO.getCurrentStage(), taskBO.getCurrentStage());
					assertEquals(taskVO.getBonusReward(), taskBO.getBonusReward(), JUnitTestUtils.FLOAT_ERROR);
					JUnitTestUtils.assertTimestampEquals(taskVO.getDeadline(), taskBO.getTaskDeadline());
					assertEquals(stageVO.getName(), taskBO.getStageName());
					assertEquals(stageVO.getDescription(), taskBO.getStageDesc());
					assertEquals(stageVO.getReward(), taskBO.getReward(), JUnitTestUtils.FLOAT_ERROR);
					assertEquals(undertakeVO.getStatus(), taskBO.getStageStatus());
					JUnitTestUtils.assertTimestampEquals(stageVO.getDeadline(), taskBO.getFinishTime());
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			undertakeDAO.delete(undertakePK2);
			undertakeDAO.delete(undertakePK3);
			stageDAO.delete(stagePK2);
			stageDAO.delete(stagePK3);
			taskDAO.delete(taskPK2);
			taskDAO.delete(taskPK3);
		}
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
		List<TaskBO> taskBOList = responseBO.getTasks();
		assertEquals(0, taskBOList.size());
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
