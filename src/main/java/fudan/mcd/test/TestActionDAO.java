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

public class TestActionDAO {
	private int userId;
	private int templateId;
	private int taskId;
	private int stageId;
	private int locationId;
	private ActionDAO actionDAO;
	private ActionVO actionVO;

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
		LocationDAO locationDAO = new LocationDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		LocationVO locationVO = JUnitTestUtils.generateLocationVO(stageId);
		locationId = locationDAO.insert(locationVO);
		actionDAO = new ActionDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
	}

	@Test
	public void testInsertAndDelete() {
		actionVO = JUnitTestUtils.generateActionVO(locationId);
		int actionPK = actionDAO.insert(actionVO);
		assertTrue(actionPK > 0);

		ActionVO resultActionVO = actionDAO.delete(actionPK);
		assertNotNull(resultActionVO);
		assertEquals(actionPK, resultActionVO.getId());
		assertEquals(actionVO.getLocationId(), resultActionVO.getLocationId());
		assertEquals(actionVO.getDuration(), resultActionVO.getDuration(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(actionVO.getType(), resultActionVO.getType());
	}

	@Test
	public void testInsertDataWithPK() {
		actionVO = JUnitTestUtils.generateActionVO(locationId);
		int actionPK1 = -1;
		actionPK1 = actionDAO.insert(actionVO);
		assertTrue(actionPK1 > 0);
		actionVO.setId(actionPK1);
		int actionPK2 = actionDAO.insert(actionVO);
		assertTrue(actionPK2 > 0);
		assertNotEquals(actionPK1, actionPK2);
		actionDAO.delete(actionPK1);
		actionDAO.delete(actionPK2);
	}

	@Test
	public void testInvalidDelete() {
		int actionPK = JUnitTestUtils.INVALID_PK;
		assertNull(actionDAO.query(actionPK));
		actionVO = actionDAO.delete(actionPK);
		assertNull(actionVO);
	}

	@Test
	public void testUpdate() {
		actionVO = JUnitTestUtils.generateActionVO(locationId);
		int actionPK = actionDAO.insert(actionVO);
		assertTrue(actionPK > 0);

		ActionVO newActionVO = JUnitTestUtils.generateActionVO(locationId);
		newActionVO.setId(actionPK);
		int result = actionDAO.update(newActionVO);
		assertTrue(result > 0);
		ActionVO resultActionVO = actionDAO.query(actionPK);
		assertNotNull(resultActionVO);
		assertEquals(newActionVO.getLocationId(), resultActionVO.getLocationId());
		assertEquals(newActionVO.getDuration(), resultActionVO.getDuration(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(newActionVO.getType(), resultActionVO.getType());
		actionDAO.delete(actionPK);
	}

	@Test
	public void testInvalidUpdate() {
		actionVO = JUnitTestUtils.generateActionVO(locationId);
		assertNull(actionDAO.query(JUnitTestUtils.INVALID_PK));

		int result = actionDAO.update(actionVO);
		assertTrue(result < 0);
		assertNull(actionDAO.query(JUnitTestUtils.INVALID_PK));
	}

	@Test
	public void testQuery() {
		actionVO = JUnitTestUtils.generateActionVO(locationId);
		int actionPK = actionDAO.insert(actionVO);
		assertTrue(actionPK > 0);

		ActionVO resultActionVO = actionDAO.query(actionPK);
		assertNotNull(resultActionVO);
		assertEquals(actionVO.getLocationId(), resultActionVO.getLocationId());
		assertEquals(actionVO.getDuration(), resultActionVO.getDuration(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(actionVO.getType(), resultActionVO.getType());
		actionDAO.delete(actionPK);
	}

	@Test
	public void testInvalidQuery() {
		int actionPK = JUnitTestUtils.INVALID_PK;
		actionVO = actionDAO.query(actionPK);
		assertNull(actionVO);
	}

	@Test
	public void testQueryByLocation() {
		actionVO = JUnitTestUtils.generateActionVO(locationId);
		int actionPK1 = actionDAO.insert(actionVO);
		assertTrue(actionPK1 > 0);
		int actionPK2 = actionDAO.insert(actionVO);
		assertTrue(actionPK2 > 0);

		List<ActionVO> resultActionVOList = actionDAO.queryActionListByLocation(locationId);
		assertEquals(2, resultActionVOList.size());
		List<Integer> resultActionPKList = new ArrayList<Integer>();
		for (ActionVO vo : resultActionVOList)
			resultActionPKList.add(vo.getId());
		assertTrue(resultActionPKList.contains(actionPK1));
		assertTrue(resultActionPKList.contains(actionPK2));
		actionDAO.delete(actionPK1);
		actionDAO.delete(actionPK2);
	}

	@Test
	public void testInvalidQueryByLocation() {
		actionVO = JUnitTestUtils.generateActionVO(locationId);
		int actionPK1 = actionDAO.insert(actionVO);
		assertTrue(actionPK1 > 0);
		int actionPK2 = actionDAO.insert(actionVO);
		assertTrue(actionPK2 > 0);

		List<ActionVO> resultActionVOList = actionDAO.queryActionListByLocation(JUnitTestUtils.INVALID_PK);
		assertEquals(0, resultActionVOList.size());
		actionDAO.delete(actionPK1);
		actionDAO.delete(actionPK2);
	}

	@After
	public void recycle() {
		LocationDAO locationDAO = new LocationDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		locationDAO.delete(locationId);
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
