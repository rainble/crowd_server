package fudan.mcd.test;

import fudan.mcd.dao.abs.AbstractDAO;
import fudan.mcd.dao.impl.TaskDAO;
import fudan.mcd.dao.impl.TemplateDAO;
import fudan.mcd.dao.impl.UserDAO;
import fudan.mcd.vo.TaskVO;
import fudan.mcd.vo.TemplateVO;
import fudan.mcd.vo.UserVO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestTaskDAO {
	private int userId;
	private int templateId;
	private TaskDAO taskDAO;
	private TaskVO taskVO;

	@Before
	public void init() {
		UserDAO userDAO = new UserDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		UserVO userVO = JUnitTestUtils.generateUserVO();
		userId = userDAO.insert(userVO);
		TemplateDAO templateDAO = new TemplateDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		TemplateVO templateVO = JUnitTestUtils.generateTemplateVO(userId);
		templateId = templateDAO.insert(templateVO);
		taskDAO = new TaskDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
	}

	@Test
	public void testInsertAndDelete() {
		taskVO = JUnitTestUtils.generateTaskVO(templateId, userId);
		int taskPK = taskDAO.insert(taskVO);
		assertTrue(taskPK > 0);

		TaskVO resultTaskVO = taskDAO.delete(taskPK);
		assertNotNull(resultTaskVO);
		assertEquals(taskPK, resultTaskVO.getId());
		assertEquals(taskVO.getTemplateId(), resultTaskVO.getTemplateId());
		assertEquals(taskVO.getUserId(), resultTaskVO.getUserId());
		assertEquals(taskVO.getTitle(), resultTaskVO.getTitle());
		assertEquals(taskVO.getDescription(), resultTaskVO.getDescription());
		assertEquals(taskVO.getStatus(), resultTaskVO.getStatus());
		assertEquals(taskVO.getCurrentStage(), resultTaskVO.getCurrentStage());
		assertEquals(taskVO.getBonusReward(), resultTaskVO.getBonusReward(), JUnitTestUtils.FLOAT_ERROR);
		JUnitTestUtils.assertTimestampEquals(taskVO.getPublishTime(), resultTaskVO.getPublishTime());
		JUnitTestUtils.assertTimestampEquals(taskVO.getDeadline(), resultTaskVO.getDeadline());
		assertEquals(taskVO.getUserType(), resultTaskVO.getUserType());
	}

	@Test
	public void testInsertDataWithPK() {
		taskVO = JUnitTestUtils.generateTaskVO(templateId, userId);
		int taskPK1 = -1;
		taskPK1 = taskDAO.insert(taskVO);
		assertTrue(taskPK1 > 0);
		taskVO.setId(taskPK1);
		int taskPK2 = taskDAO.insert(taskVO);
		assertTrue(taskPK2 > 0);
		assertNotEquals(taskPK1, taskPK2);
		taskDAO.delete(taskPK1);
		taskDAO.delete(taskPK2);
	}

	@Test
	public void testInvalidDelete() {
		int taskPK = JUnitTestUtils.INVALID_PK;
		assertNull(taskDAO.query(taskPK));
		taskVO = taskDAO.delete(taskPK);
		assertNull(taskVO);
	}

	@Test
	public void testUpdate() {
		taskVO = JUnitTestUtils.generateTaskVO(templateId, userId);
		int taskPK = taskDAO.insert(taskVO);
		assertTrue(taskPK > 0);

		TaskVO newTaskVO = JUnitTestUtils.generateTaskVO(templateId, userId);
		newTaskVO.setId(taskPK);
		int result = taskDAO.update(newTaskVO);
		assertTrue(result > 0);
		TaskVO resultTaskVO = taskDAO.query(taskPK);
		assertNotNull(resultTaskVO);
		assertEquals(newTaskVO.getTemplateId(), resultTaskVO.getTemplateId());
		assertEquals(newTaskVO.getUserId(), resultTaskVO.getUserId());
		assertEquals(newTaskVO.getTitle(), resultTaskVO.getTitle());
		assertEquals(newTaskVO.getDescription(), resultTaskVO.getDescription());
		assertEquals(newTaskVO.getStatus(), resultTaskVO.getStatus());
		assertEquals(newTaskVO.getCurrentStage(), resultTaskVO.getCurrentStage());
		assertEquals(newTaskVO.getBonusReward(), resultTaskVO.getBonusReward(), JUnitTestUtils.FLOAT_ERROR);
		JUnitTestUtils.assertTimestampEquals(newTaskVO.getPublishTime(), resultTaskVO.getPublishTime());
		JUnitTestUtils.assertTimestampEquals(newTaskVO.getDeadline(), resultTaskVO.getDeadline());
		assertEquals(newTaskVO.getUserType(), resultTaskVO.getUserType());
		taskDAO.delete(taskPK);
	}

	@Test
	public void testInvalidUpdate() {
		taskVO = JUnitTestUtils.generateTaskVO(templateId, userId);
		assertNull(taskDAO.query(JUnitTestUtils.INVALID_PK));

		int result = taskDAO.update(taskVO);
		assertTrue(result < 0);
		assertNull(taskDAO.query(JUnitTestUtils.INVALID_PK));
	}

	@Test
	public void testQuery() {
		taskVO = JUnitTestUtils.generateTaskVO(templateId, userId);
		int taskPK = taskDAO.insert(taskVO);
		assertTrue(taskPK > 0);

		TaskVO resultTaskVO = taskDAO.query(taskPK);
		assertNotNull(resultTaskVO);
		assertEquals(taskVO.getTemplateId(), resultTaskVO.getTemplateId());
		assertEquals(taskVO.getUserId(), resultTaskVO.getUserId());
		assertEquals(taskVO.getTitle(), resultTaskVO.getTitle());
		assertEquals(taskVO.getDescription(), resultTaskVO.getDescription());
		assertEquals(taskVO.getStatus(), resultTaskVO.getStatus());
		assertEquals(taskVO.getCurrentStage(), resultTaskVO.getCurrentStage());
		assertEquals(taskVO.getBonusReward(), resultTaskVO.getBonusReward(), JUnitTestUtils.FLOAT_ERROR);
		JUnitTestUtils.assertTimestampEquals(taskVO.getPublishTime(), resultTaskVO.getPublishTime());
		JUnitTestUtils.assertTimestampEquals(taskVO.getDeadline(), resultTaskVO.getDeadline());
		assertEquals(taskVO.getUserType(), resultTaskVO.getUserType());
		taskDAO.delete(taskPK);
	}

	@Test
	public void testInvalidQuery() {
		int taskPK = JUnitTestUtils.INVALID_PK;
		taskVO = taskDAO.query(taskPK);
		assertNull(taskVO);
	}

	@Test
	public void testQueryByUser() {
		taskVO = JUnitTestUtils.generateTaskVO(templateId, userId);
		int taskPK1 = taskDAO.insert(taskVO);
		assertTrue(taskPK1 > 0);
		int taskPK2 = taskDAO.insert(taskVO);
		assertTrue(taskPK2 > 0);

		List<TaskVO> resultTaskVOList = taskDAO.queryTaskListByUser(userId);
		assertEquals(2, resultTaskVOList.size());
		List<Integer> resultTaskPKList = new ArrayList<Integer>();
		for (TaskVO vo : resultTaskVOList)
			resultTaskPKList.add(vo.getId());
		assertTrue(resultTaskPKList.contains(taskPK1));
		assertTrue(resultTaskPKList.contains(taskPK2));
		taskDAO.delete(taskPK1);
		taskDAO.delete(taskPK2);
	}

	@Test
	public void testInvalidQueryByUser() {
		taskVO = JUnitTestUtils.generateTaskVO(templateId, userId);
		int taskPK1 = taskDAO.insert(taskVO);
		assertTrue(taskPK1 > 0);
		int taskPK2 = taskDAO.insert(taskVO);
		assertTrue(taskPK2 > 0);

		List<TaskVO> resultTaskVOList = taskDAO.queryTaskListByUser(JUnitTestUtils.INVALID_PK);
		assertEquals(0, resultTaskVOList.size());
		taskDAO.delete(taskPK1);
		taskDAO.delete(taskPK2);
	}

	@Test
	public void testQueryAll() {
		taskVO = JUnitTestUtils.generateTaskVO(templateId, userId);
		int taskPK1 = taskDAO.insert(taskVO);
		assertTrue(taskPK1 > 0);
		int taskPK2 = taskDAO.insert(taskVO);
		assertTrue(taskPK2 > 0);

		List<TaskVO> resultTaskVOList = taskDAO.queryAllTaskList();
		assertTrue(resultTaskVOList.size() >= 2);
		List<Integer> resultTaskPKList = new ArrayList<Integer>();
		for (TaskVO vo : resultTaskVOList)
			resultTaskPKList.add(vo.getId());
		assertTrue(resultTaskPKList.contains(taskPK1));
		assertTrue(resultTaskPKList.contains(taskPK2));
		taskDAO.delete(taskPK1);
		taskDAO.delete(taskPK2);
	}

	@After
	public void recycle() {
		TemplateDAO templateDAO = new TemplateDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		templateDAO.delete(templateId);
		UserDAO userDAO = new UserDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		userDAO.delete(userId);
	}
}
