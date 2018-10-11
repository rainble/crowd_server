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

public class TestNumericalOutputDAO {
	private int userId;
	private int templateId;
	private int taskId;
	private int stageId;
	private int locationId;
	private int actionId;
	private OutputDAO outputDAO;
	private NumericalOutputVO numericalOutputVO;

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
		numericalOutputVO = JUnitTestUtils.generateNumericalOutputVO(actionId, userId);
		int actionPK = outputDAO.insert(numericalOutputVO);
		assertTrue(actionPK > 0);

		OutputVO resultOutputVO = outputDAO.delete(actionPK, NumericalOutputVO.class);
		assertNotNull(resultOutputVO);
		assertEquals(actionPK, resultOutputVO.getId());
		assertEquals(numericalOutputVO.getActionId(), resultOutputVO.getActionId());
		assertEquals(numericalOutputVO.getValue(), resultOutputVO.getValue());
		assertEquals(numericalOutputVO.getDesc(), resultOutputVO.getDesc());
		assertEquals(numericalOutputVO.getWorkerId(), resultOutputVO.getWorkerId());
		assertEquals(numericalOutputVO.getIndicator(), resultOutputVO.getIndicator());
		assertTrue(resultOutputVO instanceof NumericalOutputVO);
		assertEquals(numericalOutputVO.getInterval(), ((NumericalOutputVO) resultOutputVO).getInterval());
		assertEquals(numericalOutputVO.getLowerBound(), ((NumericalOutputVO) resultOutputVO).getLowerBound(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(numericalOutputVO.getUpperBound(), ((NumericalOutputVO) resultOutputVO).getUpperBound(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(numericalOutputVO.getAggregationMethod(), ((NumericalOutputVO) resultOutputVO).getAggregationMethod());
	}

	@Test
	public void testInsertDataWithPK() {
		numericalOutputVO = JUnitTestUtils.generateNumericalOutputVO(actionId, userId);
		int outputPK1 = -1;
		outputPK1 = outputDAO.insert(numericalOutputVO);
		assertTrue(outputPK1 > 0);
		numericalOutputVO.setId(outputPK1);
		int outputPK2 = outputDAO.insert(numericalOutputVO);
		assertTrue(outputPK2 > 0);
		assertNotEquals(outputPK1, outputPK2);
		outputDAO.delete(outputPK1, NumericalOutputVO.class);
		outputDAO.delete(outputPK2, NumericalOutputVO.class);
	}

	@Test
	public void testInvalidDelete() {
		int outputPK = JUnitTestUtils.INVALID_PK;
		assertNull(outputDAO.query(outputPK, NumericalOutputVO.class));
		OutputVO outputVO = outputDAO.delete(outputPK, NumericalOutputVO.class);
		assertNull(outputVO);
	}

	@Test
	public void testUpdate() {
		numericalOutputVO = JUnitTestUtils.generateNumericalOutputVO(actionId, userId);
		int outputPK = outputDAO.insert(numericalOutputVO);
		assertTrue(outputPK > 0);

		NumericalOutputVO newOutputVO = JUnitTestUtils.generateNumericalOutputVO(actionId, userId);
		newOutputVO.setId(outputPK);
		int result = outputDAO.update(newOutputVO);
		assertTrue(result > 0);
		OutputVO resultOutputVO = outputDAO.query(outputPK, NumericalOutputVO.class);
		assertNotNull(resultOutputVO);
		assertEquals(newOutputVO.getActionId(), resultOutputVO.getActionId());
		assertEquals(newOutputVO.getValue(), resultOutputVO.getValue());
		assertEquals(newOutputVO.getDesc(), resultOutputVO.getDesc());
		assertEquals(newOutputVO.getWorkerId(), resultOutputVO.getWorkerId());
		assertEquals(newOutputVO.getIndicator(), resultOutputVO.getIndicator());
		assertTrue(resultOutputVO instanceof NumericalOutputVO);
		assertEquals(newOutputVO.getInterval(), ((NumericalOutputVO) resultOutputVO).getInterval());
		assertEquals(newOutputVO.getLowerBound(), ((NumericalOutputVO) resultOutputVO).getLowerBound(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(newOutputVO.getUpperBound(), ((NumericalOutputVO) resultOutputVO).getUpperBound(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(newOutputVO.getAggregationMethod(), ((NumericalOutputVO) resultOutputVO).getAggregationMethod());
		outputDAO.delete(outputPK, NumericalOutputVO.class);
	}

	@Test
	public void testInvalidUpdate() {
		numericalOutputVO = JUnitTestUtils.generateNumericalOutputVO(actionId, userId);
		assertNull(outputDAO.query(JUnitTestUtils.INVALID_PK, NumericalOutputVO.class));

		int result = outputDAO.update(numericalOutputVO);
		assertTrue(result < 0);
		assertNull(outputDAO.query(JUnitTestUtils.INVALID_PK, NumericalOutputVO.class));
	}

	@Test
	public void testQuery() {
		numericalOutputVO = JUnitTestUtils.generateNumericalOutputVO(actionId, userId);
		int outputPK = outputDAO.insert(numericalOutputVO);
		assertTrue(outputPK > 0);

		OutputVO resultOutputVO = outputDAO.query(outputPK, NumericalOutputVO.class);
		assertNotNull(resultOutputVO);
		assertEquals(numericalOutputVO.getActionId(), resultOutputVO.getActionId());
		assertEquals(numericalOutputVO.getValue(), resultOutputVO.getValue());
		assertEquals(numericalOutputVO.getDesc(), resultOutputVO.getDesc());
		assertEquals(numericalOutputVO.getWorkerId(), resultOutputVO.getWorkerId());
		assertEquals(numericalOutputVO.getIndicator(), resultOutputVO.getIndicator());
		assertTrue(resultOutputVO instanceof NumericalOutputVO);
		assertEquals(numericalOutputVO.getInterval(), ((NumericalOutputVO) resultOutputVO).getInterval());
		assertEquals(numericalOutputVO.getLowerBound(), ((NumericalOutputVO) resultOutputVO).getLowerBound(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(numericalOutputVO.getUpperBound(), ((NumericalOutputVO) resultOutputVO).getUpperBound(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(numericalOutputVO.getAggregationMethod(), ((NumericalOutputVO) resultOutputVO).getAggregationMethod());
		outputDAO.delete(outputPK, NumericalOutputVO.class);
	}

	@Test
	public void testInvalidQuery() {
		int outputPK = JUnitTestUtils.INVALID_PK;
		OutputVO outputVO = outputDAO.query(outputPK, NumericalOutputVO.class);
		assertNull(outputVO);
	}

	@Test
	public void testQueryByAction() {
		numericalOutputVO = JUnitTestUtils.generateNumericalOutputVO(actionId, userId);
		int outputPK1 = outputDAO.insert(numericalOutputVO);
		assertTrue(outputPK1 > 0);
		int outputPK2 = outputDAO.insert(numericalOutputVO);
		assertTrue(outputPK2 > 0);

		List<OutputVO> resultOutputVOList = outputDAO.queryOutputListByAction(actionId);
		assertEquals(2, resultOutputVOList.size());
		List<Integer> resultOutputPKList = new ArrayList<Integer>();
		for (OutputVO vo : resultOutputVOList)
			resultOutputPKList.add(vo.getId());
		assertTrue(resultOutputPKList.contains(outputPK1));
		assertTrue(resultOutputPKList.contains(outputPK2));
		outputDAO.delete(outputPK1, NumericalOutputVO.class);
		outputDAO.delete(outputPK2, NumericalOutputVO.class);
	}

	@Test
	public void testInvalidQueryByLocation() {
		numericalOutputVO = JUnitTestUtils.generateNumericalOutputVO(actionId, userId);
		int outputPK1 = outputDAO.insert(numericalOutputVO);
		assertTrue(outputPK1 > 0);
		int outputPK2 = outputDAO.insert(numericalOutputVO);
		assertTrue(outputPK2 > 0);

		List<OutputVO> resultOutputVOList = outputDAO.queryOutputListByAction(JUnitTestUtils.INVALID_PK);
		assertEquals(0, resultOutputVOList.size());
		outputDAO.delete(outputPK1, NumericalOutputVO.class);
		outputDAO.delete(outputPK2, NumericalOutputVO.class);
	}
	
	@Test
	public void testQueryByActionAndIndicator() {
		NumericalOutputVO numericalOutputVO1 = JUnitTestUtils.generateNumericalOutputVO(actionId, userId);
		numericalOutputVO1.setIndicator(1);
		int outputPK1 = outputDAO.insert(numericalOutputVO1);
		assertTrue(outputPK1 > 0);
		NumericalOutputVO numericalOutputVO2 = JUnitTestUtils.generateNumericalOutputVO(actionId, userId);
		numericalOutputVO2.setIndicator(2);
		int outputPK2 = outputDAO.insert(numericalOutputVO2);
		assertTrue(outputPK2 > 0);

		List<OutputVO> resultOutputVOList = outputDAO.queryOutputListByActionAndIndicator(actionId, numericalOutputVO1.getIndicator());
		assertEquals(1, resultOutputVOList.size());
		outputDAO.delete(outputPK1, NumericalOutputVO.class);
		outputDAO.delete(outputPK2, NumericalOutputVO.class);
	}

	@Test
	public void testInvalidQueryByActionAndIndicator() {
		NumericalOutputVO numericalOutputVO1 = JUnitTestUtils.generateNumericalOutputVO(actionId, userId);
		numericalOutputVO1.setIndicator(1);
		int outputPK1 = outputDAO.insert(numericalOutputVO1);
		assertTrue(outputPK1 > 0);
		NumericalOutputVO numericalOutputVO2 = JUnitTestUtils.generateNumericalOutputVO(actionId, userId);
		numericalOutputVO2.setIndicator(2);
		int outputPK2 = outputDAO.insert(numericalOutputVO2);
		assertTrue(outputPK2 > 0);

		List<OutputVO> resultOutputVOList = outputDAO.queryOutputListByActionAndIndicator(JUnitTestUtils.INVALID_PK, numericalOutputVO1.getIndicator());
		assertEquals(0, resultOutputVOList.size());
		resultOutputVOList = outputDAO.queryOutputListByActionAndIndicator(actionId, JUnitTestUtils.INVALID_PK);
		assertEquals(0, resultOutputVOList.size());
		outputDAO.delete(outputPK1, NumericalOutputVO.class);
		outputDAO.delete(outputPK2, NumericalOutputVO.class);
	}

	@Test
	public void testQueryByActionAndUser() {
		NumericalOutputVO numericalOutputVO1 = JUnitTestUtils.generateNumericalOutputVO(actionId, userId);
		numericalOutputVO1.setIndicator(1);
		int outputPK1 = outputDAO.insert(numericalOutputVO1);
		assertTrue(outputPK1 > 0);
		NumericalOutputVO numericalOutputVO2 = JUnitTestUtils.generateNumericalOutputVO(actionId, userId);
		numericalOutputVO2.setIndicator(2);
		int outputPK2 = outputDAO.insert(numericalOutputVO2);
		assertTrue(outputPK2 > 0);

		List<OutputVO> resultOutputVOList = outputDAO.queryOutputListByActionAndUser(actionId, userId, numericalOutputVO1.getIndicator());
		assertEquals(1, resultOutputVOList.size());
		outputDAO.delete(outputPK1, NumericalOutputVO.class);
		outputDAO.delete(outputPK2, NumericalOutputVO.class);
	}

	@Test
	public void testInvalidQueryByActionAndUser() {
		NumericalOutputVO numericalOutputVO1 = JUnitTestUtils.generateNumericalOutputVO(actionId, userId);
		numericalOutputVO1.setIndicator(1);
		int outputPK1 = outputDAO.insert(numericalOutputVO1);
		assertTrue(outputPK1 > 0);
		NumericalOutputVO numericalOutputVO2 = JUnitTestUtils.generateNumericalOutputVO(actionId, userId);
		numericalOutputVO2.setIndicator(2);
		int outputPK2 = outputDAO.insert(numericalOutputVO2);
		assertTrue(outputPK2 > 0);

		List<OutputVO> resultOutputVOList = outputDAO.queryOutputListByActionAndUser(JUnitTestUtils.INVALID_PK, userId, numericalOutputVO1.getIndicator());
		assertEquals(0, resultOutputVOList.size());
		resultOutputVOList = outputDAO.queryOutputListByActionAndUser(actionId, JUnitTestUtils.INVALID_PK, numericalOutputVO1.getIndicator());
		assertEquals(0, resultOutputVOList.size());
		resultOutputVOList = outputDAO.queryOutputListByActionAndUser(actionId, userId, JUnitTestUtils.INVALID_PK);
		assertEquals(0, resultOutputVOList.size());
		outputDAO.delete(outputPK1, NumericalOutputVO.class);
		outputDAO.delete(outputPK2, NumericalOutputVO.class);
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
