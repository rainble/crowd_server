package fudan.mcd.test;

import fudan.mcd.dao.abs.AbstractDAO;
import fudan.mcd.dao.impl.StageDAO;
import fudan.mcd.dao.impl.TaskDAO;
import fudan.mcd.dao.impl.TemplateDAO;
import fudan.mcd.dao.impl.UserDAO;
import fudan.mcd.vo.StageVO;
import fudan.mcd.vo.TaskVO;
import fudan.mcd.vo.TemplateVO;
import fudan.mcd.vo.UserVO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestStageDAO {
	private int userId;
	private int templateId;
	private int taskId;
	private StageDAO stageDAO;
	private StageVO stageVO;

	@Before
	public void init() {
		UserDAO userDAO = new UserDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		UserVO userVO = JUnitTestUtils.generateUserVO();
		userId = userDAO.insert(userVO);
		TemplateDAO templateDAO = new TemplateDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		TemplateVO templateVO = JUnitTestUtils.generateTemplateVO(userId);
		templateId = templateDAO.insert(templateVO);
		TaskDAO taskDAO = new TaskDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		TaskVO taskVO = JUnitTestUtils.generateTaskVO(templateId, userId);
		taskId = taskDAO.insert(taskVO);
		stageDAO = new StageDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
	}

	@Test
	public void testInsertAndDelete() {
		stageVO = JUnitTestUtils.generateStageVO(taskId);
		int stagePK = stageDAO.insert(stageVO);
		assertTrue(stagePK > 0);

		StageVO resultStageVO = stageDAO.delete(stagePK);
		assertNotNull(resultStageVO);
		assertEquals(stagePK, resultStageVO.getId());
		assertEquals(stageVO.getTaskId(), resultStageVO.getTaskId());
		assertEquals(stageVO.getName(), resultStageVO.getName());
		assertEquals(stageVO.getDescription(), resultStageVO.getDescription());
		JUnitTestUtils.assertTimestampEquals(stageVO.getDeadline(), resultStageVO.getDeadline());
		assertEquals(stageVO.getReward(), resultStageVO.getReward(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(stageVO.getIndex(), resultStageVO.getIndex());
		assertEquals(stageVO.getWorkerNum(), resultStageVO.getWorkerNum());
		assertEquals(stageVO.getAggregateMethod(), resultStageVO.getAggregateMethod());
		assertEquals(stageVO.getAggregateResult(), resultStageVO.getAggregateResult());
		assertEquals(stageVO.getRestrictions(), resultStageVO.getRestrictions());
		assertEquals(stageVO.getStatus(), resultStageVO.getStatus());
	}

	@Test
	public void testInsertDataWithPK() {
		stageVO = JUnitTestUtils.generateStageVO(taskId);
		int stagePK1 = -1;
		stagePK1 = stageDAO.insert(stageVO);
		assertTrue(stagePK1 > 0);
		stageVO.setId(stagePK1);
		int stagePK2 = stageDAO.insert(stageVO);
		assertTrue(stagePK2 > 0);
		assertNotEquals(stagePK1, stagePK2);
		stageDAO.delete(stagePK1);
		stageDAO.delete(stagePK2);
	}

	@Test
	public void testInvalidDelete() {
		int stagePK = JUnitTestUtils.INVALID_PK;
		assertNull(stageDAO.query(stagePK));
		stageVO = stageDAO.delete(stagePK);
		assertNull(stageVO);
	}

	@Test
	public void testUpdate() {
		stageVO = JUnitTestUtils.generateStageVO(taskId);
		int stagePK = stageDAO.insert(stageVO);
		assertTrue(stagePK > 0);

		StageVO newStageVO = JUnitTestUtils.generateStageVO(taskId);
		newStageVO.setId(stagePK);
		int result = stageDAO.update(newStageVO);
		assertTrue(result > 0);
		StageVO resultStageVO = stageDAO.query(stagePK);
		assertNotNull(resultStageVO);
		assertEquals(newStageVO.getTaskId(), resultStageVO.getTaskId());
		assertEquals(newStageVO.getName(), resultStageVO.getName());
		assertEquals(newStageVO.getDescription(), resultStageVO.getDescription());
		JUnitTestUtils.assertTimestampEquals(newStageVO.getDeadline(), resultStageVO.getDeadline());
		assertEquals(newStageVO.getReward(), resultStageVO.getReward(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(newStageVO.getIndex(), resultStageVO.getIndex());
		assertEquals(newStageVO.getWorkerNum(), resultStageVO.getWorkerNum());
		assertEquals(newStageVO.getAggregateMethod(), resultStageVO.getAggregateMethod());
		assertEquals(newStageVO.getAggregateResult(), resultStageVO.getAggregateResult());
		assertEquals(newStageVO.getRestrictions(), resultStageVO.getRestrictions());
		assertEquals(newStageVO.getDuration(), resultStageVO.getDuration(), JUnitTestUtils.FLOAT_ERROR);
		JUnitTestUtils.assertTimestampEquals(newStageVO.getContract(), resultStageVO.getContract());
		assertEquals(newStageVO.getStatus(), resultStageVO.getStatus());
		stageDAO.delete(stagePK);
	}

	@Test
	public void testInvalidUpdate() {
		stageVO = JUnitTestUtils.generateStageVO(taskId);
		assertNull(stageDAO.query(JUnitTestUtils.INVALID_PK));

		int result = stageDAO.update(stageVO);
		assertTrue(result < 0);
		assertNull(stageDAO.query(JUnitTestUtils.INVALID_PK));
	}

	@Test
	public void testQuery() {
		stageVO = JUnitTestUtils.generateStageVO(taskId);
		int stagePK = stageDAO.insert(stageVO);
		assertTrue(stagePK > 0);

		StageVO resultStageVO = stageDAO.query(stagePK);
		assertNotNull(resultStageVO);
		assertEquals(stageVO.getTaskId(), resultStageVO.getTaskId());
		assertEquals(stageVO.getName(), resultStageVO.getName());
		assertEquals(stageVO.getDescription(), resultStageVO.getDescription());
		JUnitTestUtils.assertTimestampEquals(stageVO.getDeadline(), resultStageVO.getDeadline());
		assertEquals(stageVO.getReward(), resultStageVO.getReward(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(stageVO.getIndex(), resultStageVO.getIndex());
		assertEquals(stageVO.getWorkerNum(), resultStageVO.getWorkerNum());
		assertEquals(stageVO.getAggregateMethod(), resultStageVO.getAggregateMethod());
		assertEquals(stageVO.getAggregateResult(), resultStageVO.getAggregateResult());
		assertEquals(stageVO.getRestrictions(), resultStageVO.getRestrictions());
		assertEquals(stageVO.getDuration(), resultStageVO.getDuration(), JUnitTestUtils.FLOAT_ERROR);
		JUnitTestUtils.assertTimestampEquals(stageVO.getContract(), resultStageVO.getContract());
		assertEquals(stageVO.getStatus(), resultStageVO.getStatus());
		stageDAO.delete(stagePK);
	}

	@Test
	public void testInvalidQuery() {
		int stagePK = JUnitTestUtils.INVALID_PK;
		stageVO = stageDAO.query(stagePK);
		assertNull(stageVO);
	}

	@Test
	public void testQueryStageListByTask() {
		stageVO = JUnitTestUtils.generateStageVO(taskId);
		int stagePK1 = stageDAO.insert(stageVO);
		assertTrue(stagePK1 > 0);
		int stagePK2 = stageDAO.insert(stageVO);
		assertTrue(stagePK2 > 0);

		List<StageVO> resultStageVOList = stageDAO.queryStageListByTask(taskId);
		assertEquals(2, resultStageVOList.size());
		List<Integer> resultStagePKList = new ArrayList<Integer>();
		for (StageVO vo : resultStageVOList)
			resultStagePKList.add(vo.getId());
		assertTrue(resultStagePKList.contains(stagePK1));
		assertTrue(resultStagePKList.contains(stagePK2));
		stageDAO.delete(stagePK1);
		stageDAO.delete(stagePK2);
	}

	@Test
	public void testInvalidQueryStageListByTask() {
		stageVO = JUnitTestUtils.generateStageVO(taskId);
		int stagePK1 = stageDAO.insert(stageVO);
		assertTrue(stagePK1 > 0);
		int stagePK2 = stageDAO.insert(stageVO);
		assertTrue(stagePK2 > 0);

		List<StageVO> resultStageVOList = stageDAO.queryStageListByTask(JUnitTestUtils.INVALID_PK);
		assertEquals(0, resultStageVOList.size());
		stageDAO.delete(stagePK1);
		stageDAO.delete(stagePK2);
	}

	@Test
	public void testQueryByTaskAndIndex() {
		stageVO = JUnitTestUtils.generateStageVO(taskId);
		int stagePK = stageDAO.insert(stageVO);
		assertTrue(stagePK > 0);

		StageVO resultStageVO = stageDAO.queryByTaskAndIndex(stageVO.getTaskId(), stageVO.getIndex());
		assertNotNull(resultStageVO);
		assertEquals(stageVO.getTaskId(), resultStageVO.getTaskId());
		assertEquals(stageVO.getName(), resultStageVO.getName());
		assertEquals(stageVO.getDescription(), resultStageVO.getDescription());
		JUnitTestUtils.assertTimestampEquals(stageVO.getDeadline(), resultStageVO.getDeadline());
		assertEquals(stageVO.getReward(), resultStageVO.getReward(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(stageVO.getIndex(), resultStageVO.getIndex());
		assertEquals(stageVO.getWorkerNum(), resultStageVO.getWorkerNum());
		assertEquals(stageVO.getAggregateMethod(), resultStageVO.getAggregateMethod());
		assertEquals(stageVO.getAggregateResult(), resultStageVO.getAggregateResult());
		assertEquals(stageVO.getRestrictions(), resultStageVO.getRestrictions());
		assertEquals(stageVO.getDuration(), resultStageVO.getDuration(), JUnitTestUtils.FLOAT_ERROR);
		JUnitTestUtils.assertTimestampEquals(stageVO.getContract(), resultStageVO.getContract());
		assertEquals(stageVO.getStatus(), resultStageVO.getStatus());
		stageDAO.delete(stagePK);
	}

	@Test
	public void testInvalidQueryByTaskAndIndex() {
		stageVO = JUnitTestUtils.generateStageVO(taskId);
		int stagePK = stageDAO.insert(stageVO);
		assertTrue(stagePK > 0);

		assertNull(stageDAO.queryByTaskAndIndex(JUnitTestUtils.INVALID_PK, stageVO.getIndex()));
		assertNull(stageDAO.queryByTaskAndIndex(stageVO.getTaskId(), JUnitTestUtils.INVALID_PK));
		assertNull(stageDAO.queryByTaskAndIndex(JUnitTestUtils.INVALID_PK, JUnitTestUtils.INVALID_PK));
		stageDAO.delete(stagePK);
	}

	@After
	public void recycle() {
		TaskDAO taskDAO = new TaskDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		taskDAO.delete(taskId);
		TemplateDAO templateDAO = new TemplateDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		templateDAO.delete(templateId);
		UserDAO userDAO = new UserDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		userDAO.delete(userId);
	}
}
