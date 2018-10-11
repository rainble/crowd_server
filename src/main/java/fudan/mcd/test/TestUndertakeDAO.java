package fudan.mcd.test;

import fudan.mcd.dao.abs.AbstractDAO;
import fudan.mcd.dao.impl.*;
import fudan.mcd.vo.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestUndertakeDAO {
	private int userId;
	private int templateId;
	private int taskId;
	private int stageId;
	private UndertakeDAO undertakeDAO;
	private UndertakeVO undertakeVO;

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
		StageDAO stageDAO = new StageDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		StageVO stageVO = JUnitTestUtils.generateStageVO(taskId);
		stageId = stageDAO.insert(stageVO);
		undertakeDAO = new UndertakeDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
	}

	@Test
	public void testInsertAndDelete() {
		undertakeVO = JUnitTestUtils.generateUndertakeVO(userId, stageId);
		int undertakePK = undertakeDAO.insert(undertakeVO);
		assertTrue(undertakePK > 0);

		UndertakeVO resultUndertakeVO = undertakeDAO.delete(undertakePK);
		assertNotNull(resultUndertakeVO);
		assertEquals(undertakePK, resultUndertakeVO.getId());
		assertEquals(undertakeVO.getUserId(), resultUndertakeVO.getUserId());
		assertEquals(undertakeVO.getStageId(), resultUndertakeVO.getStageId());
		JUnitTestUtils.assertTimestampEquals(undertakeVO.getStartTime(), resultUndertakeVO.getStartTime());
		JUnitTestUtils.assertTimestampEquals(undertakeVO.getEndTime(), resultUndertakeVO.getEndTime());
		JUnitTestUtils.assertTimestampEquals(undertakeVO.getContractTime(), resultUndertakeVO.getContractTime());
		assertEquals(undertakeVO.getStatus(), resultUndertakeVO.getStatus());
	}

	@Test
	public void testInsertDataWithPK() {
		undertakeVO = JUnitTestUtils.generateUndertakeVO(userId, stageId);
		int undertakePK1 = -1;
		undertakePK1 = undertakeDAO.insert(undertakeVO);
		assertTrue(undertakePK1 > 0);
		undertakeVO.setId(undertakePK1);
		int undertakePK2 = undertakeDAO.insert(undertakeVO);
		assertTrue(undertakePK2 > 0);
		assertNotEquals(undertakePK1, undertakePK2);
		undertakeDAO.delete(undertakePK1);
		undertakeDAO.delete(undertakePK2);
	}

	@Test
	public void testInvalidDelete() {
		int undertakePK = JUnitTestUtils.INVALID_PK;
		assertNull(undertakeDAO.query(undertakePK));
		undertakeVO = undertakeDAO.delete(undertakePK);
		assertNull(undertakeVO);
	}

	@Test
	public void testUpdate() {
		undertakeVO = JUnitTestUtils.generateUndertakeVO(userId, stageId);
		int undertakePK = undertakeDAO.insert(undertakeVO);
		assertTrue(undertakePK > 0);

		UndertakeVO newUndertakeVO = JUnitTestUtils.generateUndertakeVO(userId, stageId);
		newUndertakeVO.setId(undertakePK);
		int result = undertakeDAO.update(newUndertakeVO);
		assertTrue(result > 0);
		UndertakeVO resultUndertakeVO = undertakeDAO.query(undertakePK);
		assertNotNull(resultUndertakeVO);
		assertEquals(newUndertakeVO.getUserId(), resultUndertakeVO.getUserId());
		assertEquals(newUndertakeVO.getStageId(), resultUndertakeVO.getStageId());
		JUnitTestUtils.assertTimestampEquals(newUndertakeVO.getStartTime(), resultUndertakeVO.getStartTime());
		JUnitTestUtils.assertTimestampEquals(newUndertakeVO.getEndTime(), resultUndertakeVO.getEndTime());
		JUnitTestUtils.assertTimestampEquals(newUndertakeVO.getContractTime(), resultUndertakeVO.getContractTime());
		assertEquals(newUndertakeVO.getStatus(), resultUndertakeVO.getStatus());
		undertakeDAO.delete(undertakePK);
	}

	@Test
	public void testInvalidUpdate() {
		undertakeVO = JUnitTestUtils.generateUndertakeVO(userId, stageId);
		assertNull(undertakeDAO.query(JUnitTestUtils.INVALID_PK));

		int result = undertakeDAO.update(undertakeVO);
		assertTrue(result < 0);
		assertNull(undertakeDAO.query(JUnitTestUtils.INVALID_PK));
	}

	@Test
	public void testQuery() {
		undertakeVO = JUnitTestUtils.generateUndertakeVO(userId, stageId);
		int undertakePK = undertakeDAO.insert(undertakeVO);
		assertTrue(undertakePK > 0);

		UndertakeVO resultUndertakeVO = undertakeDAO.query(undertakePK);
		assertNotNull(resultUndertakeVO);
		assertEquals(undertakeVO.getUserId(), resultUndertakeVO.getUserId());
		assertEquals(undertakeVO.getStageId(), resultUndertakeVO.getStageId());
		JUnitTestUtils.assertTimestampEquals(undertakeVO.getStartTime(), resultUndertakeVO.getStartTime());
		JUnitTestUtils.assertTimestampEquals(undertakeVO.getEndTime(), resultUndertakeVO.getEndTime());
		JUnitTestUtils.assertTimestampEquals(undertakeVO.getContractTime(), resultUndertakeVO.getContractTime());
		assertEquals(undertakeVO.getStatus(), resultUndertakeVO.getStatus());
		undertakeDAO.delete(undertakePK);
	}

	@Test
	public void testInvalidQuery() {
		int undertakePK = JUnitTestUtils.INVALID_PK;
		undertakeVO = undertakeDAO.query(undertakePK);
		assertNull(undertakeVO);
	}

	@Test
	public void testQueryStageListByUser() {
		UserDAO userDAO = new UserDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		UserVO userVO = JUnitTestUtils.generateUserVO();
		int userPK2 = userDAO.insert(userVO);
		assertTrue(userPK2 > 0);
		StageDAO stageDAO = new StageDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		StageVO stageVO = JUnitTestUtils.generateStageVO(taskId);
		int stagePK2 = stageDAO.insert(stageVO);
		assertTrue(stagePK2 > 0);
		UndertakeVO undertakeVO2 = JUnitTestUtils.generateUndertakeVO(userId, stageId);
		int undertakePK2 = undertakeDAO.insert(undertakeVO2);
		UndertakeVO undertakeVO3 = JUnitTestUtils.generateUndertakeVO(userId, stagePK2);
		undertakeVO3.setStatus(undertakeVO2.getStatus());
		int undertakePK3 = undertakeDAO.insert(undertakeVO3);
		UndertakeVO undertakeVO4 = JUnitTestUtils.generateUndertakeVO(userPK2, stageId);
		int undertakePK4 = undertakeDAO.insert(undertakeVO4);

		List<StageVO> stageVOList = undertakeDAO.queryStageListByUserAndStatus(userId, undertakeVO2.getStatus());
		assertEquals(2, stageVOList.size());
		List<Integer> stagePKList = new ArrayList<Integer>();
		for (StageVO vo : stageVOList)
			stagePKList.add(vo.getId());
		assertTrue(stagePKList.contains(stageId));
		assertTrue(stagePKList.contains(stagePK2));

		for (StageVO resultStageVO : stageVOList) {
			if (resultStageVO.getId() == stagePK2) {
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
			}
		}

		undertakeDAO.delete(undertakePK2);
		undertakeDAO.delete(undertakePK3);
		undertakeDAO.delete(undertakePK4);
		stageDAO.delete(stagePK2);
		userDAO.delete(userPK2);
	}

	@Test
	public void testInvalidQueryStageListByUser() {
		UserDAO userDAO = new UserDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		UserVO userVO = JUnitTestUtils.generateUserVO();
		int userPK2 = userDAO.insert(userVO);
		assertTrue(userPK2 > 0);
		StageDAO stageDAO = new StageDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		StageVO stageVO = JUnitTestUtils.generateStageVO(taskId);
		int stagePK2 = stageDAO.insert(stageVO);
		assertTrue(stagePK2 > 0);
		int undertakePK2 = undertakeDAO.insert(JUnitTestUtils.generateUndertakeVO(userId, stageId));
		int undertakePK3 = undertakeDAO.insert(JUnitTestUtils.generateUndertakeVO(userId, stagePK2));
		int undertakePK4 = undertakeDAO.insert(JUnitTestUtils.generateUndertakeVO(userPK2, stageId));

		List<StageVO> stageVOList = undertakeDAO.queryStageListByUserAndStatus(JUnitTestUtils.INVALID_PK, JUnitTestUtils.INVALID_PK);
		assertEquals(0, stageVOList.size());

		undertakeDAO.delete(undertakePK2);
		undertakeDAO.delete(undertakePK3);
		undertakeDAO.delete(undertakePK4);
		stageDAO.delete(stagePK2);
		userDAO.delete(userPK2);
	}

	@Test
	public void testQueryByUserAndStage() {
		undertakeVO = JUnitTestUtils.generateUndertakeVO(userId, stageId);
		int undertakePK = undertakeDAO.insert(undertakeVO);
		assertTrue(undertakePK > 0);

		UndertakeVO resultUndertakeVO = undertakeDAO.queryByUserAndStage(userId, stageId);
		assertNotNull(resultUndertakeVO);
		assertEquals(undertakePK, resultUndertakeVO.getId());
		assertEquals(undertakeVO.getUserId(), resultUndertakeVO.getUserId());
		assertEquals(undertakeVO.getStageId(), resultUndertakeVO.getStageId());
		JUnitTestUtils.assertTimestampEquals(undertakeVO.getStartTime(), resultUndertakeVO.getStartTime());
		JUnitTestUtils.assertTimestampEquals(undertakeVO.getEndTime(), resultUndertakeVO.getEndTime());
		JUnitTestUtils.assertTimestampEquals(undertakeVO.getContractTime(), resultUndertakeVO.getContractTime());
		assertEquals(undertakeVO.getStatus(), resultUndertakeVO.getStatus());
		undertakeDAO.delete(undertakePK);
	}

	@Test
	public void testQueryByStage() {
		undertakeVO = JUnitTestUtils.generateUndertakeVO(userId, stageId);
		int undertakePK = undertakeDAO.insert(undertakeVO);
		assertTrue(undertakePK > 0);

		List<UndertakeVO> list = undertakeDAO.queryByStage(stageId);
		assertEquals(1, list.size());
		UndertakeVO resultUndertakeVO = list.get(0);
		assertEquals(undertakePK, resultUndertakeVO.getId());
		assertEquals(undertakeVO.getUserId(), resultUndertakeVO.getUserId());
		assertEquals(undertakeVO.getStageId(), resultUndertakeVO.getStageId());
		JUnitTestUtils.assertTimestampEquals(undertakeVO.getStartTime(), resultUndertakeVO.getStartTime());
		JUnitTestUtils.assertTimestampEquals(undertakeVO.getEndTime(), resultUndertakeVO.getEndTime());
		JUnitTestUtils.assertTimestampEquals(undertakeVO.getContractTime(), resultUndertakeVO.getContractTime());
		assertEquals(undertakeVO.getStatus(), resultUndertakeVO.getStatus());
		undertakeDAO.delete(undertakePK);
	}

	@After
	public void recycle() {
		StageDAO stageDAO = new StageDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		stageDAO.delete(stageId);
		TaskDAO taskDAO = new TaskDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		taskDAO.delete(taskId);
		TemplateDAO templateDAO = new TemplateDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		templateDAO.delete(templateId);
		UserDAO userDAO = new UserDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		userDAO.delete(userId);
	}
}
