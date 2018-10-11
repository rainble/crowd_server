package fudan.mcd.test;

import fudan.mcd.dao.abs.AbstractDAO;
import fudan.mcd.dao.impl.*;
import fudan.mcd.vo.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestApplicationDAO {
	private int userId;
	private int templateId;
	private int taskId;
	private int stageId;
	private ApplicationDAO applicationDAO;
	private ApplicationVO applicationVO;

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
		applicationDAO = new ApplicationDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
	}

	@Test
	public void testInsertAndDelete() {
		applicationVO = JUnitTestUtils.generateApplicationVO(userId, stageId);
		int applicationPK = applicationDAO.insert(applicationVO);
		assertTrue(applicationPK > 0);

		ApplicationVO resultapplicationVO = applicationDAO.delete(applicationPK);
		assertNotNull(resultapplicationVO);
		assertEquals(applicationPK, resultapplicationVO.getId());
		assertEquals(applicationVO.getUserId(), resultapplicationVO.getUserId());
		assertEquals(applicationVO.getStageId(), resultapplicationVO.getStageId());
	}

	@Test
	public void testInsertDataWithPK() {
		applicationVO = JUnitTestUtils.generateApplicationVO(userId, stageId);
		int applicationPK1 = -1;
		applicationPK1 = applicationDAO.insert(applicationVO);
		assertTrue(applicationPK1 > 0);
		applicationVO.setId(applicationPK1);
		int applicationPK2 = applicationDAO.insert(applicationVO);
		assertTrue(applicationPK2 > 0);
		assertNotEquals(applicationPK1, applicationPK2);
		applicationDAO.delete(applicationPK1);
		applicationDAO.delete(applicationPK2);
	}

	@Test
	public void testInvalidDelete() {
		int applicationPK = JUnitTestUtils.INVALID_PK;
		assertNull(applicationDAO.query(applicationPK));
		applicationVO = applicationDAO.delete(applicationPK);
		assertNull(applicationVO);
	}

	@Test
	public void testUpdate() {
		applicationVO = JUnitTestUtils.generateApplicationVO(userId, stageId);
		int applicationPK = applicationDAO.insert(applicationVO);
		assertTrue(applicationPK > 0);

		ApplicationVO newApplicationVO = JUnitTestUtils.generateApplicationVO(userId, stageId);
		newApplicationVO.setId(applicationPK);
		int result = applicationDAO.update(newApplicationVO);
		assertTrue(result > 0);
		ApplicationVO resultApplicationVO = applicationDAO.query(applicationPK);
		assertNotNull(resultApplicationVO);
		assertEquals(newApplicationVO.getUserId(), resultApplicationVO.getUserId());
		assertEquals(newApplicationVO.getStageId(), resultApplicationVO.getStageId());
		applicationDAO.delete(applicationPK);
	}

	@Test
	public void testInvalidUpdate() {
		applicationVO = JUnitTestUtils.generateApplicationVO(userId, stageId);
		assertNull(applicationDAO.query(JUnitTestUtils.INVALID_PK));

		int result = applicationDAO.update(applicationVO);
		assertTrue(result < 0);
		assertNull(applicationDAO.query(JUnitTestUtils.INVALID_PK));
	}

	@Test
	public void testQuery() {
		applicationVO = JUnitTestUtils.generateApplicationVO(userId, stageId);
		int applicationPK = applicationDAO.insert(applicationVO);
		assertTrue(applicationPK > 0);

		ApplicationVO resultApplicationVO = applicationDAO.query(applicationPK);
		assertNotNull(resultApplicationVO);
		assertEquals(applicationVO.getUserId(), resultApplicationVO.getUserId());
		assertEquals(applicationVO.getStageId(), resultApplicationVO.getStageId());
		applicationDAO.delete(applicationPK);
	}

	@Test
	public void testInvalidQuery() {
		int applicationPK = JUnitTestUtils.INVALID_PK;
		applicationVO = applicationDAO.query(applicationPK);
		assertNull(applicationVO);
	}
	
	@Test
	public void testQueryByUserAndStage() {
		applicationVO = JUnitTestUtils.generateApplicationVO(userId, stageId);
		int applicationPK = applicationDAO.insert(applicationVO);
		assertTrue(applicationPK > 0);

		ApplicationVO resultApplicationVO = applicationDAO.queryByUserAndStage(userId, stageId);
		assertNotNull(resultApplicationVO);
		assertEquals(applicationVO.getUserId(), resultApplicationVO.getUserId());
		assertEquals(applicationVO.getStageId(), resultApplicationVO.getStageId());
		applicationDAO.delete(applicationPK);
	}
	
	@Test
	public void testInvalidQueryByUserAndStage() {
		assertNull(applicationDAO.queryByUserAndStage(JUnitTestUtils.INVALID_PK, stageId));
		assertNull(applicationDAO.queryByUserAndStage(userId, JUnitTestUtils.INVALID_PK));
	}
	
	@Test
	public void testDeleteByUserAndStage() {
		applicationVO = JUnitTestUtils.generateApplicationVO(userId, stageId);
		int applicationPK = applicationDAO.insert(applicationVO);
		assertTrue(applicationPK > 0);

		ApplicationVO resultapplicationVO = applicationDAO.deleteByUserAndStage(userId, stageId);
		assertNotNull(resultapplicationVO);
		assertEquals(applicationPK, resultapplicationVO.getId());
		assertEquals(applicationVO.getUserId(), resultapplicationVO.getUserId());
		assertEquals(applicationVO.getStageId(), resultapplicationVO.getStageId());
	}
	
	@Test
	public void testInvalidDeleteByUserAndStage() {
		assertNull(applicationDAO.deleteByUserAndStage(JUnitTestUtils.INVALID_PK, stageId));
		assertNull(applicationDAO.deleteByUserAndStage(userId, JUnitTestUtils.INVALID_PK));
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
