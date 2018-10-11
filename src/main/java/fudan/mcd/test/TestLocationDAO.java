package fudan.mcd.test;

import fudan.mcd.dao.abs.AbstractDAO;
import fudan.mcd.dao.impl.*;
import fudan.mcd.vo.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestLocationDAO {
	private int userId;
	private int templateId;
	private int taskId;
	private int stageId;
	private LocationDAO locationDAO;
	private LocationVO locationVO;

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
		locationDAO = new LocationDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
	}

	@Test
	public void testInsertAndDelete() {
		locationVO = JUnitTestUtils.generateLocationVO(stageId);
		int locationPK = locationDAO.insert(locationVO);
		assertTrue(locationPK > 0);

		LocationVO resultLocationVO = locationDAO.delete(locationPK);
		assertNotNull(resultLocationVO);
		assertEquals(locationPK, resultLocationVO.getId());
		assertEquals(locationVO.getStageId(), resultLocationVO.getStageId());
		assertEquals(locationVO.getAddress(), resultLocationVO.getAddress());
		assertEquals(locationVO.getLatitude(), resultLocationVO.getLatitude(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(locationVO.getLongitude(), resultLocationVO.getLongitude(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(locationVO.getType(), resultLocationVO.getType());
	}

	@Test
	public void testInsertDataWithPK() {
		locationVO = JUnitTestUtils.generateLocationVO(stageId);
		int locationPK1 = -1;
		locationPK1 = locationDAO.insert(locationVO);
		assertTrue(locationPK1 > 0);
		locationVO.setId(locationPK1);
		int locationPK2 = locationDAO.insert(locationVO);
		assertTrue(locationPK2 > 0);
		assertNotEquals(locationPK1, locationPK2);
		locationDAO.delete(locationPK1);
		locationDAO.delete(locationPK2);
	}

	@Test
	public void testInvalidDelete() {
		int locationPK = JUnitTestUtils.INVALID_PK;
		assertNull(locationDAO.query(locationPK));
		locationVO = locationDAO.delete(locationPK);
		assertNull(locationVO);
	}

	@Test
	public void testUpdate() {
		locationVO = JUnitTestUtils.generateLocationVO(stageId);
		int stagePK = locationDAO.insert(locationVO);
		assertTrue(stagePK > 0);

		LocationVO newLocationVO = JUnitTestUtils.generateLocationVO(stageId);
		newLocationVO.setId(stagePK);
		int result = locationDAO.update(newLocationVO);
		assertTrue(result > 0);
		LocationVO resultLocationVO = locationDAO.query(stagePK);
		assertNotNull(resultLocationVO);
		assertEquals(newLocationVO.getStageId(), resultLocationVO.getStageId());
		assertEquals(newLocationVO.getAddress(), resultLocationVO.getAddress());
		assertEquals(newLocationVO.getLatitude(), resultLocationVO.getLatitude(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(newLocationVO.getLongitude(), resultLocationVO.getLongitude(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(newLocationVO.getType(), resultLocationVO.getType());
		locationDAO.delete(stagePK);
	}

	@Test
	public void testInvalidUpdate() {
		locationVO = JUnitTestUtils.generateLocationVO(stageId);
		assertNull(locationDAO.query(JUnitTestUtils.INVALID_PK));

		int result = locationDAO.update(locationVO);
		assertTrue(result < 0);
		assertNull(locationDAO.query(JUnitTestUtils.INVALID_PK));
	}

	@Test
	public void testQuery() {
		locationVO = JUnitTestUtils.generateLocationVO(stageId);
		int locationPK = locationDAO.insert(locationVO);
		assertTrue(locationPK > 0);

		LocationVO resultLocationVO = locationDAO.query(locationPK);
		assertNotNull(resultLocationVO);
		assertEquals(locationVO.getStageId(), resultLocationVO.getStageId());
		assertEquals(locationVO.getAddress(), resultLocationVO.getAddress());
		assertEquals(locationVO.getLatitude(), resultLocationVO.getLatitude(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(locationVO.getLongitude(), resultLocationVO.getLongitude(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(locationVO.getType(), resultLocationVO.getType());
		locationDAO.delete(locationPK);
	}

	@Test
	public void testInvalidQuery() {
		int locationPK = JUnitTestUtils.INVALID_PK;
		locationVO = locationDAO.query(locationPK);
		assertNull(locationVO);
	}

	@Test
	public void queryByStageAndType() {
		locationVO = JUnitTestUtils.generateLocationVO(stageId);
		int locationPK = locationDAO.insert(locationVO);
		assertTrue(locationPK > 0);

		LocationVO resultLocationVO = locationDAO.queryByStageAndType(locationVO.getStageId(), locationVO.getType());
		assertNotNull(resultLocationVO);
		assertEquals(locationVO.getStageId(), resultLocationVO.getStageId());
		assertEquals(locationVO.getAddress(), resultLocationVO.getAddress());
		assertEquals(locationVO.getLatitude(), resultLocationVO.getLatitude(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(locationVO.getLongitude(), resultLocationVO.getLongitude(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(locationVO.getType(), resultLocationVO.getType());
		locationDAO.delete(locationPK);
	}

	@Test
	public void TestInvalidQueryByStageAndType() {
		locationVO = JUnitTestUtils.generateLocationVO(stageId);
		int locationPK = locationDAO.insert(locationVO);
		assertTrue(locationPK > 0);

		assertNull(locationDAO.queryByStageAndType(JUnitTestUtils.INVALID_PK, locationVO.getType()));
		assertNull(locationDAO.queryByStageAndType(locationVO.getStageId(), JUnitTestUtils.INVALID_PK));
		assertNull(locationDAO.queryByStageAndType(JUnitTestUtils.INVALID_PK, JUnitTestUtils.INVALID_PK));
		locationDAO.delete(locationPK);
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
