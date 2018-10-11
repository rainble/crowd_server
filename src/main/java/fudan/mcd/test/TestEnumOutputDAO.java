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

public class TestEnumOutputDAO {
	private int userId;
	private int templateId;
	private int taskId;
	private int stageId;
	private int locationId;
	private int actionId;
	private OutputDAO outputDAO;
	private EnumOutputVO enumOutputVO;

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
		outputDAO = new OutputDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
	}

	@Test
	public void testInsertAndDelete() {
		enumOutputVO = JUnitTestUtils.generateEnumOutputVO(actionId, userId);
		int actionPK = outputDAO.insert(enumOutputVO);
		assertTrue(actionPK > 0);

		OutputVO resultOutputVO = outputDAO.delete(actionPK, EnumOutputVO.class);
		assertNotNull(resultOutputVO);
		assertEquals(actionPK, resultOutputVO.getId());
		assertEquals(enumOutputVO.getActionId(), resultOutputVO.getActionId());
		assertEquals(enumOutputVO.getValue(), resultOutputVO.getValue());
		assertEquals(enumOutputVO.getDesc(), resultOutputVO.getDesc());
		assertEquals(enumOutputVO.getWorkerId(), resultOutputVO.getWorkerId());
		assertEquals(enumOutputVO.getIndicator(), resultOutputVO.getIndicator());
		assertTrue(resultOutputVO instanceof EnumOutputVO);
		assertEquals(enumOutputVO.getEntries(), ((EnumOutputVO) resultOutputVO).getEntries());
		assertEquals(enumOutputVO.getAggregationMethod(), ((EnumOutputVO) resultOutputVO).getAggregationMethod());
	}

	@Test
	public void testInsertDataWithPK() {
		enumOutputVO = JUnitTestUtils.generateEnumOutputVO(actionId, userId);
		int outputPK1 = -1;
		outputPK1 = outputDAO.insert(enumOutputVO);
		assertTrue(outputPK1 > 0);
		enumOutputVO.setId(outputPK1);
		int outputPK2 = outputDAO.insert(enumOutputVO);
		assertTrue(outputPK2 > 0);
		assertNotEquals(outputPK1, outputPK2);
		outputDAO.delete(outputPK1, EnumOutputVO.class);
		outputDAO.delete(outputPK2, EnumOutputVO.class);
	}

	@Test
	public void testInvalidDelete() {
		int outputPK = JUnitTestUtils.INVALID_PK;
		assertNull(outputDAO.query(outputPK, EnumOutputVO.class));
		OutputVO outputVO = outputDAO.delete(outputPK, EnumOutputVO.class);
		assertNull(outputVO);
	}

	@Test
	public void testUpdate() {
		enumOutputVO = JUnitTestUtils.generateEnumOutputVO(actionId, userId);
		int outputPK = outputDAO.insert(enumOutputVO);
		assertTrue(outputPK > 0);

		EnumOutputVO newOutputVO = JUnitTestUtils.generateEnumOutputVO(actionId, userId);
		newOutputVO.setId(outputPK);
		int result = outputDAO.update(newOutputVO);
		assertTrue(result > 0);
		OutputVO resultOutputVO = outputDAO.query(outputPK, EnumOutputVO.class);
		assertNotNull(resultOutputVO);
		assertEquals(newOutputVO.getActionId(), resultOutputVO.getActionId());
		assertEquals(newOutputVO.getValue(), resultOutputVO.getValue());
		assertEquals(newOutputVO.getDesc(), resultOutputVO.getDesc());
		assertEquals(newOutputVO.getWorkerId(), resultOutputVO.getWorkerId());
		assertEquals(newOutputVO.getIndicator(), resultOutputVO.getIndicator());
		assertTrue(resultOutputVO instanceof EnumOutputVO);
		assertEquals(newOutputVO.getEntries(), ((EnumOutputVO) resultOutputVO).getEntries());
		assertEquals(newOutputVO.getAggregationMethod(), ((EnumOutputVO) resultOutputVO).getAggregationMethod());
		outputDAO.delete(outputPK, EnumOutputVO.class);
	}

	@Test
	public void testInvalidUpdate() {
		enumOutputVO = JUnitTestUtils.generateEnumOutputVO(actionId, userId);
		assertNull(outputDAO.query(JUnitTestUtils.INVALID_PK, EnumOutputVO.class));

		int result = outputDAO.update(enumOutputVO);
		assertTrue(result < 0);
		assertNull(outputDAO.query(JUnitTestUtils.INVALID_PK, EnumOutputVO.class));
	}

	@Test
	public void testQuery() {
		enumOutputVO = JUnitTestUtils.generateEnumOutputVO(actionId, userId);
		int outputPK = outputDAO.insert(enumOutputVO);
		assertTrue(outputPK > 0);

		OutputVO resultOutputVO = outputDAO.query(outputPK, EnumOutputVO.class);
		assertNotNull(resultOutputVO);
		assertEquals(enumOutputVO.getActionId(), resultOutputVO.getActionId());
		assertEquals(enumOutputVO.getValue(), resultOutputVO.getValue());
		assertEquals(enumOutputVO.getDesc(), resultOutputVO.getDesc());
		assertEquals(enumOutputVO.getWorkerId(), resultOutputVO.getWorkerId());
		assertEquals(enumOutputVO.getIndicator(), resultOutputVO.getIndicator());
		assertTrue(resultOutputVO instanceof EnumOutputVO);
		assertEquals(enumOutputVO.getEntries(), ((EnumOutputVO) resultOutputVO).getEntries());
		assertEquals(enumOutputVO.getAggregationMethod(), ((EnumOutputVO) resultOutputVO).getAggregationMethod());
		outputDAO.delete(outputPK, EnumOutputVO.class);
	}

	@Test
	public void testInvalidQuery() {
		int outputPK = JUnitTestUtils.INVALID_PK;
		OutputVO outputVO = outputDAO.query(outputPK, EnumOutputVO.class);
		assertNull(outputVO);
	}

	@Test
	public void testQueryByAction() {
		enumOutputVO = JUnitTestUtils.generateEnumOutputVO(actionId, userId);
		int outputPK1 = outputDAO.insert(enumOutputVO);
		assertTrue(outputPK1 > 0);
		int outputPK2 = outputDAO.insert(enumOutputVO);
		assertTrue(outputPK2 > 0);

		List<OutputVO> resultOutputVOList = outputDAO.queryOutputListByAction(actionId);
		assertEquals(2, resultOutputVOList.size());
		List<Integer> resultOutputPKList = new ArrayList<Integer>();
		for (OutputVO vo : resultOutputVOList)
			resultOutputPKList.add(vo.getId());
		assertTrue(resultOutputPKList.contains(outputPK1));
		assertTrue(resultOutputPKList.contains(outputPK2));
		outputDAO.delete(outputPK1, EnumOutputVO.class);
		outputDAO.delete(outputPK2, EnumOutputVO.class);
	}

	@Test
	public void testInvalidQueryByLocation() {
		enumOutputVO = JUnitTestUtils.generateEnumOutputVO(actionId, userId);
		int outputPK1 = outputDAO.insert(enumOutputVO);
		assertTrue(outputPK1 > 0);
		int outputPK2 = outputDAO.insert(enumOutputVO);
		assertTrue(outputPK2 > 0);

		List<OutputVO> resultOutputVOList = outputDAO.queryOutputListByAction(JUnitTestUtils.INVALID_PK);
		assertEquals(0, resultOutputVOList.size());
		outputDAO.delete(outputPK1, EnumOutputVO.class);
		outputDAO.delete(outputPK2, EnumOutputVO.class);
	}

	@Test
	public void testQueryByActionAndIndicator() {
		EnumOutputVO enumOutputVO1 = JUnitTestUtils.generateEnumOutputVO(actionId, userId);
		enumOutputVO1.setIndicator(1);
		int outputPK1 = outputDAO.insert(enumOutputVO1);
		assertTrue(outputPK1 > 0);
		EnumOutputVO enumOutputVO2 = JUnitTestUtils.generateEnumOutputVO(actionId, userId);
		enumOutputVO2.setIndicator(2);
		int outputPK2 = outputDAO.insert(enumOutputVO2);
		assertTrue(outputPK2 > 0);

		List<OutputVO> resultOutputVOList = outputDAO.queryOutputListByActionAndIndicator(actionId, enumOutputVO1.getIndicator());
		assertEquals(1, resultOutputVOList.size());
		outputDAO.delete(outputPK1, EnumOutputVO.class);
		outputDAO.delete(outputPK2, EnumOutputVO.class);
	}

	@Test
	public void testInvalidQueryByActionAndIndicator() {
		EnumOutputVO enumOutputVO1 = JUnitTestUtils.generateEnumOutputVO(actionId, userId);
		enumOutputVO1.setIndicator(1);
		int outputPK1 = outputDAO.insert(enumOutputVO1);
		assertTrue(outputPK1 > 0);
		EnumOutputVO enumOutputVO2 = JUnitTestUtils.generateEnumOutputVO(actionId, userId);
		enumOutputVO2.setIndicator(2);
		int outputPK2 = outputDAO.insert(enumOutputVO2);
		assertTrue(outputPK2 > 0);

		List<OutputVO> resultOutputVOList = outputDAO.queryOutputListByActionAndIndicator(JUnitTestUtils.INVALID_PK, enumOutputVO1.getIndicator());
		assertEquals(0, resultOutputVOList.size());
		resultOutputVOList = outputDAO.queryOutputListByActionAndIndicator(actionId, JUnitTestUtils.INVALID_PK);
		assertEquals(0, resultOutputVOList.size());
		outputDAO.delete(outputPK1, EnumOutputVO.class);
		outputDAO.delete(outputPK2, EnumOutputVO.class);
	}

	@Test
	public void testQueryByActionAndUser() {
		EnumOutputVO enumOutputVO1 = JUnitTestUtils.generateEnumOutputVO(actionId, userId);
		enumOutputVO1.setIndicator(1);
		int outputPK1 = outputDAO.insert(enumOutputVO1);
		assertTrue(outputPK1 > 0);
		EnumOutputVO enumOutputVO2 = JUnitTestUtils.generateEnumOutputVO(actionId, userId);
		enumOutputVO2.setIndicator(2);
		int outputPK2 = outputDAO.insert(enumOutputVO2);
		assertTrue(outputPK2 > 0);

		List<OutputVO> resultOutputVOList = outputDAO.queryOutputListByActionAndUser(actionId, userId, enumOutputVO1.getIndicator());
		assertEquals(1, resultOutputVOList.size());
		outputDAO.delete(outputPK1, EnumOutputVO.class);
		outputDAO.delete(outputPK2, EnumOutputVO.class);
	}

	@Test
	public void testInvalidQueryByActionAndUser() {
		EnumOutputVO enumOutputVO1 = JUnitTestUtils.generateEnumOutputVO(actionId, userId);
		enumOutputVO1.setIndicator(1);
		int outputPK1 = outputDAO.insert(enumOutputVO1);
		assertTrue(outputPK1 > 0);
		EnumOutputVO enumOutputVO2 = JUnitTestUtils.generateEnumOutputVO(actionId, userId);
		enumOutputVO2.setIndicator(2);
		int outputPK2 = outputDAO.insert(enumOutputVO2);
		assertTrue(outputPK2 > 0);

		List<OutputVO> resultOutputVOList = outputDAO.queryOutputListByActionAndUser(JUnitTestUtils.INVALID_PK, userId, enumOutputVO1.getIndicator());
		assertEquals(0, resultOutputVOList.size());
		resultOutputVOList = outputDAO.queryOutputListByActionAndUser(actionId, JUnitTestUtils.INVALID_PK, enumOutputVO1.getIndicator());
		assertEquals(0, resultOutputVOList.size());
		resultOutputVOList = outputDAO.queryOutputListByActionAndUser(actionId, userId, JUnitTestUtils.INVALID_PK);
		assertEquals(0, resultOutputVOList.size());
		outputDAO.delete(outputPK1, EnumOutputVO.class);
		outputDAO.delete(outputPK2, EnumOutputVO.class);
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
