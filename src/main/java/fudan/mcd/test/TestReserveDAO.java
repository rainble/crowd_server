package fudan.mcd.test;

import fudan.mcd.dao.abs.AbstractDAO;
import fudan.mcd.dao.impl.*;
import fudan.mcd.vo.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestReserveDAO {
	private int userId;
	private int previousUserId;
	private int templateId;
	private int taskId;
	private int stageId;
	private int previousStageId;
	private ReserveDAO reserveDAO;
	private ReserveVO reserveVO;

	@Before
	public void init() {
		UserDAO userDAO = new UserDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		UserVO userVO = JUnitTestUtils.generateUserVO();
		userId = userDAO.insert(userVO);
		UserVO previousUserVO = JUnitTestUtils.generateUserVO();
		previousUserId = userDAO.insert(previousUserVO);
		TemplateDAO templateDAO = new TemplateDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		TemplateVO templateVO = JUnitTestUtils.generateTemplateVO(userId);
		templateId = templateDAO.insert(templateVO);
		TaskDAO taskDAO = new TaskDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		TaskVO taskVO = JUnitTestUtils.generateTaskVO(templateId, userId);
		taskId = taskDAO.insert(taskVO);
		StageDAO stageDAO = new StageDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		StageVO stageVO = JUnitTestUtils.generateStageVO(taskId);
		stageId = stageDAO.insert(stageVO);
		StageVO previousStageVO = JUnitTestUtils.generateStageVO(taskId);
		previousStageId = stageDAO.insert(previousStageVO);
		reserveDAO = new ReserveDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
	}

	@Test
	public void testInsertAndDelete() {
		reserveVO = JUnitTestUtils.generateReserveVO(userId, previousUserId, stageId, previousStageId);
		int reservePK = reserveDAO.insert(reserveVO);
		assertTrue(reservePK > 0);

		ReserveVO resultReserveVO = reserveDAO.delete(reservePK);
		assertNotNull(resultReserveVO);
		assertEquals(reservePK, resultReserveVO.getId());
		assertEquals(reserveVO.getUserId(), resultReserveVO.getUserId());
		assertEquals(reserveVO.getStageId(), resultReserveVO.getStageId());
		JUnitTestUtils.assertTimestampEquals(reserveVO.getReserveTime(), resultReserveVO.getReserveTime());
		assertEquals(reserveVO.getPreviousUserId(), resultReserveVO.getPreviousUserId());
		assertEquals(reserveVO.getPreviousStageId(), resultReserveVO.getPreviousStageId());
		assertEquals(reserveVO.getStatus(), resultReserveVO.getStatus());
		JUnitTestUtils.assertTimestampEquals(reserveVO.getContract(), resultReserveVO.getContract());
		
	}

	@Test
	public void testInsertDataWithPK() {
		reserveVO = JUnitTestUtils.generateReserveVO(userId, previousUserId, stageId, previousStageId);
		int reservePK1 = -1;
		reservePK1 = reserveDAO.insert(reserveVO);
		assertTrue(reservePK1 > 0);
		reserveVO.setId(reservePK1);
		int reservePK2 = reserveDAO.insert(reserveVO);
		assertTrue(reservePK2 > 0);
		assertNotEquals(reservePK1, reservePK2);
		reserveDAO.delete(reservePK1);
		reserveDAO.delete(reservePK2);
	}

	@Test
	public void testInvalidDelete() {
		int reservePK = JUnitTestUtils.INVALID_PK;
		assertNull(reserveDAO.query(reservePK));
		reserveVO = reserveDAO.delete(reservePK);
		assertNull(reserveVO);
	}

	@Test
	public void testUpdate() {
		reserveVO = JUnitTestUtils.generateReserveVO(userId, previousUserId, stageId, previousStageId);
		int reservePK = reserveDAO.insert(reserveVO);
		assertTrue(reservePK > 0);

		ReserveVO newReserveVO = JUnitTestUtils.generateReserveVO(userId, previousUserId, stageId, previousStageId);
		newReserveVO.setId(reservePK);
		int result = reserveDAO.update(newReserveVO);
		assertTrue(result > 0);
		ReserveVO resultReserveVO = reserveDAO.query(reservePK);
		assertNotNull(resultReserveVO);
		assertEquals(newReserveVO.getUserId(), resultReserveVO.getUserId());
		assertEquals(newReserveVO.getStageId(), resultReserveVO.getStageId());
		JUnitTestUtils.assertTimestampEquals(newReserveVO.getReserveTime(), resultReserveVO.getReserveTime());
		assertEquals(newReserveVO.getPreviousUserId(), resultReserveVO.getPreviousUserId());
		assertEquals(newReserveVO.getPreviousStageId(), resultReserveVO.getPreviousStageId());
		assertEquals(newReserveVO.getStatus(), resultReserveVO.getStatus());
		JUnitTestUtils.assertTimestampEquals(newReserveVO.getContract(), resultReserveVO.getContract());
		reserveDAO.delete(reservePK);
	}

	@Test
	public void testInvalidUpdate() {
		reserveVO = JUnitTestUtils.generateReserveVO(userId, previousUserId, stageId, previousStageId);
		assertNull(reserveDAO.query(JUnitTestUtils.INVALID_PK));

		int result = reserveDAO.update(reserveVO);
		assertTrue(result < 0);
		assertNull(reserveDAO.query(JUnitTestUtils.INVALID_PK));
	}

	@Test
	public void testQuery() {
		reserveVO = JUnitTestUtils.generateReserveVO(userId, previousUserId, stageId, previousStageId);
		int reservePK = reserveDAO.insert(reserveVO);
		assertTrue(reservePK > 0);

		ReserveVO resultReserveVO = reserveDAO.query(reservePK);
		assertNotNull(resultReserveVO);
		assertEquals(reserveVO.getUserId(), resultReserveVO.getUserId());
		assertEquals(reserveVO.getStageId(), resultReserveVO.getStageId());
		JUnitTestUtils.assertTimestampEquals(reserveVO.getReserveTime(), resultReserveVO.getReserveTime());
		assertEquals(reserveVO.getPreviousUserId(), resultReserveVO.getPreviousUserId());
		assertEquals(reserveVO.getPreviousStageId(), resultReserveVO.getPreviousStageId());
		assertEquals(reserveVO.getStatus(), resultReserveVO.getStatus());
		JUnitTestUtils.assertTimestampEquals(reserveVO.getContract(), resultReserveVO.getContract());
		reserveDAO.delete(reservePK);
	}

	@Test
	public void testInvalidQuery() {
		int reservePK = JUnitTestUtils.INVALID_PK;
		reserveVO = reserveDAO.query(reservePK);
		assertNull(reserveVO);
	}

	@After
	public void recycle() {
		StageDAO stageDAO = new StageDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		stageDAO.delete(stageId);
		stageDAO.delete(previousStageId);
		TaskDAO taskDAO = new TaskDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		taskDAO.delete(taskId);
		TemplateDAO templateDAO = new TemplateDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		templateDAO.delete(templateId);
		UserDAO userDAO = new UserDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		userDAO.delete(userId);
		userDAO.delete(previousUserId);
	}
}
