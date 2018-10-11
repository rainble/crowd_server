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

public class TestInputDAO {
	private int userId;
	private int templateId;
	private int taskId;
	private int stageId;
	private int locationId;
	private int actionId;
	private InputDAO inputDAO;
	private InputVO inputVO;

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
		ActionDAO actionDAO = new ActionDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		ActionVO actionVO = JUnitTestUtils.generateActionVO(locationId);
		actionId = actionDAO.insert(actionVO);
		inputDAO = new InputDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
	}

	@Test
	public void testInsertAndDelete() {
		inputVO = JUnitTestUtils.generateInputVO(actionId);
		int actionPK = inputDAO.insert(inputVO);
		assertTrue(actionPK > 0);

		InputVO resultInputVO = inputDAO.delete(actionPK);
		assertNotNull(resultInputVO);
		assertEquals(actionPK, resultInputVO.getId());
		assertEquals(inputVO.getActionId(), resultInputVO.getActionId());
		assertEquals(inputVO.getType(), resultInputVO.getType());
		assertEquals(inputVO.getValue(), resultInputVO.getValue());
		assertEquals(inputVO.getDesc(), resultInputVO.getDesc());
	}

	@Test
	public void testInsertDataWithPK() {
		inputVO = JUnitTestUtils.generateInputVO(actionId);
		int inputPK1 = -1;
		inputPK1 = inputDAO.insert(inputVO);
		assertTrue(inputPK1 > 0);
		inputVO.setId(inputPK1);
		int inputPK2 = inputDAO.insert(inputVO);
		assertTrue(inputPK2 > 0);
		assertNotEquals(inputPK1, inputPK2);
		inputDAO.delete(inputPK1);
		inputDAO.delete(inputPK2);
	}

	@Test
	public void testInvalidDelete() {
		int inputPK = JUnitTestUtils.INVALID_PK;
		assertNull(inputDAO.query(inputPK));
		inputVO = inputDAO.delete(inputPK);
		assertNull(inputVO);
	}

	@Test
	public void testUpdate() {
		inputVO = JUnitTestUtils.generateInputVO(actionId);
		int inputPK = inputDAO.insert(inputVO);
		assertTrue(inputPK > 0);

		InputVO newInputVO = JUnitTestUtils.generateInputVO(actionId);
		newInputVO.setId(inputPK);
		int result = inputDAO.update(newInputVO);
		assertTrue(result > 0);
		InputVO resultInputVO = inputDAO.query(inputPK);
		assertNotNull(resultInputVO);
		assertEquals(newInputVO.getActionId(), resultInputVO.getActionId());
		assertEquals(newInputVO.getType(), resultInputVO.getType());
		assertEquals(newInputVO.getValue(), resultInputVO.getValue());
		assertEquals(newInputVO.getDesc(), resultInputVO.getDesc());
		inputDAO.delete(inputPK);
	}

	@Test
	public void testInvalidUpdate() {
		inputVO = JUnitTestUtils.generateInputVO(actionId);
		assertNull(inputDAO.query(JUnitTestUtils.INVALID_PK));

		int result = inputDAO.update(inputVO);
		assertTrue(result < 0);
		assertNull(inputDAO.query(JUnitTestUtils.INVALID_PK));
	}

	@Test
	public void testQuery() {
		inputVO = JUnitTestUtils.generateInputVO(actionId);
		int inputPK = inputDAO.insert(inputVO);
		assertTrue(inputPK > 0);

		InputVO resultInputVO = inputDAO.query(inputPK);
		assertNotNull(resultInputVO);
		assertEquals(inputVO.getActionId(), resultInputVO.getActionId());
		assertEquals(inputVO.getType(), resultInputVO.getType());
		assertEquals(inputVO.getValue(), resultInputVO.getValue());
		assertEquals(inputVO.getDesc(), resultInputVO.getDesc());
		inputDAO.delete(inputPK);
	}

	@Test
	public void testInvalidQuery() {
		int inputPK = JUnitTestUtils.INVALID_PK;
		inputVO = inputDAO.query(inputPK);
		assertNull(inputVO);
	}

	@Test
	public void testQueryByAction() {
		inputVO = JUnitTestUtils.generateInputVO(actionId);
		int inputPK1 = inputDAO.insert(inputVO);
		assertTrue(inputPK1 > 0);
		int inputPK2 = inputDAO.insert(inputVO);
		assertTrue(inputPK2 > 0);

		List<InputVO> resultInputVOList = inputDAO.queryInputListByAction(actionId);
		assertEquals(2, resultInputVOList.size());
		List<Integer> resultInputPKList = new ArrayList<Integer>();
		for (InputVO vo : resultInputVOList)
			resultInputPKList.add(vo.getId());
		assertTrue(resultInputPKList.contains(inputPK1));
		assertTrue(resultInputPKList.contains(inputPK2));
		inputDAO.delete(inputPK1);
		inputDAO.delete(inputPK2);
	}

	@Test
	public void testInvalidQueryByLocation() {
		inputVO = JUnitTestUtils.generateInputVO(actionId);
		int inputPK1 = inputDAO.insert(inputVO);
		assertTrue(inputPK1 > 0);
		int inputPK2 = inputDAO.insert(inputVO);
		assertTrue(inputPK2 > 0);

		List<InputVO> resultInputVOList = inputDAO.queryInputListByAction(JUnitTestUtils.INVALID_PK);
		assertEquals(0, resultInputVOList.size());
		inputDAO.delete(inputPK1);
		inputDAO.delete(inputPK2);
	}

	@After
	public void recycle() {
		ActionDAO actionDAO = new ActionDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		actionDAO.delete(actionId);
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
