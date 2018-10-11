package fudan.mcd.test;

import fudan.mcd.dao.abs.AbstractDAO;
import fudan.mcd.dao.impl.TaskDAO;
import fudan.mcd.dao.impl.TemplateDAO;
import fudan.mcd.dao.impl.UserDAO;
import fudan.mcd.servlet.GetPublishedTaskServlet.*;
import fudan.mcd.servlet.GetPublishedTaskServlet.ResponseBO.*;
import fudan.mcd.servlet.ServletResponseData;
import fudan.mcd.utils.JSONUtils;
import fudan.mcd.vo.TaskVO;
import fudan.mcd.vo.TemplateVO;
import fudan.mcd.vo.UserVO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class TestGetPublishedTaskServlet {
	private static final String SERVLET_NAME = "GetPublishedTaskServlet";
	private int userId;
	private UserDAO userDAO;
	private int templateId;
	private TemplateDAO templateDAO;
	private int taskId;
	private TaskDAO taskDAO;

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
		taskVO.setStatus(TaskVO.STATUS_EXPIRED);
		taskId = taskDAO.insert(taskVO);
	}

	@Test
	public void testPostCorrectAccount() {
		TaskVO taskVO2 = JUnitTestUtils.generateTaskVO(templateId, userId);
		taskVO2.setStatus(TaskVO.STATUS_FINISHED);
		int taskPK2 = taskDAO.insert(taskVO2);

		userDAO = new UserDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		UserVO userVO2 = JUnitTestUtils.generateUserVO();
		int userPK2 = userDAO.insert(userVO2);
		TaskVO taskVO3 = JUnitTestUtils.generateTaskVO(templateId, userPK2);
		taskVO3.setStatus(TaskVO.STATUS_ONGOING);
		int taskPK3 = taskDAO.insert(taskVO3);

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
			for (TaskBO taskBO : taskBOList) {
				if (taskBO.getId() == taskId) {
					assertEquals(taskVO.getId(), taskBO.getId());
					assertEquals(taskVO.getTemplateId(), taskBO.getTemplateId());
					assertEquals(taskVO.getTitle(), taskBO.getTitle());
					assertEquals(taskVO.getDescription(), taskBO.getDescription());
					assertEquals(taskVO.getStatus(), taskBO.getStatus());
					assertEquals(String.format("%d/%d", taskVO.getCurrentStage(), templateDAO.query(templateId).getTotalStageNum()),
							taskBO.getProgress());
					assertEquals(taskVO.getCurrentStage(), taskBO.getCurrentStage());
					assertEquals(taskVO.getBonusReward(), taskBO.getBonusReward(), JUnitTestUtils.FLOAT_ERROR);
					JUnitTestUtils.assertTimestampEquals(taskVO.getPublishTime(), taskBO.getPublishTime());
					JUnitTestUtils.assertTimestampEquals(taskVO.getDeadline(), taskBO.getDeadline());
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			taskDAO.delete(taskPK2);
			taskDAO.delete(taskPK3);
			userDAO.delete(userPK2);
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
		taskDAO.delete(taskId);
		templateDAO.delete(templateId);
		userDAO.delete(userId);
	}

}
