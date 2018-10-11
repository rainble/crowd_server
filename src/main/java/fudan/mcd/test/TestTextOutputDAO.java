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

public class TestTextOutputDAO {
	private int userId;
	private int templateId;
	private int taskId;
	private int stageId;
	private int locationId;
	private int actionId;
	private OutputDAO outputDAO;
	private TextOutputVO textOutputVO;

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
		textOutputVO = JUnitTestUtils.generateTextOutputVO(actionId, userId);
		int actionPK = outputDAO.insert(textOutputVO);
		assertTrue(actionPK > 0);

		OutputVO resultOutputVO = outputDAO.delete(actionPK, TextOutputVO.class);
		assertNotNull(resultOutputVO);
		assertEquals(actionPK, resultOutputVO.getId());
		assertEquals(textOutputVO.getActionId(), resultOutputVO.getActionId());
		assertEquals(textOutputVO.getValue(), resultOutputVO.getValue());
		assertEquals(textOutputVO.getDesc(), resultOutputVO.getDesc());
		assertEquals(textOutputVO.getWorkerId(), resultOutputVO.getWorkerId());
		assertEquals(textOutputVO.getIndicator(), resultOutputVO.getIndicator());
		assertTrue(resultOutputVO instanceof TextOutputVO);
	}

	@Test
	public void testInsertDataWithPK() {
		textOutputVO = JUnitTestUtils.generateTextOutputVO(actionId, userId);
		int outputPK1 = -1;
		outputPK1 = outputDAO.insert(textOutputVO);
		assertTrue(outputPK1 > 0);
		textOutputVO.setId(outputPK1);
		int outputPK2 = outputDAO.insert(textOutputVO);
		assertTrue(outputPK2 > 0);
		assertNotEquals(outputPK1, outputPK2);
		outputDAO.delete(outputPK1, TextOutputVO.class);
		outputDAO.delete(outputPK2, TextOutputVO.class);
	}

	@Test
	public void testInvalidDelete() {
		int outputPK = JUnitTestUtils.INVALID_PK;
		assertNull(outputDAO.query(outputPK, TextOutputVO.class));
		OutputVO outputVO = outputDAO.delete(outputPK, TextOutputVO.class);
		assertNull(outputVO);
	}

	@Test
	public void testUpdate() {
		textOutputVO = JUnitTestUtils.generateTextOutputVO(actionId, userId);
		int outputPK = outputDAO.insert(textOutputVO);
		assertTrue(outputPK > 0);

		TextOutputVO newOutputVO = JUnitTestUtils.generateTextOutputVO(actionId, userId);
		newOutputVO.setId(outputPK);
		int result = outputDAO.update(newOutputVO);
		assertTrue(result > 0);
		OutputVO resultOutputVO = outputDAO.query(outputPK, TextOutputVO.class);
		assertNotNull(resultOutputVO);
		assertEquals(newOutputVO.getActionId(), resultOutputVO.getActionId());
		assertEquals(newOutputVO.getValue(), resultOutputVO.getValue());
		assertEquals(newOutputVO.getDesc(), resultOutputVO.getDesc());
		assertEquals(newOutputVO.getWorkerId(), resultOutputVO.getWorkerId());
		assertEquals(newOutputVO.getIndicator(), resultOutputVO.getIndicator());
		assertTrue(resultOutputVO instanceof TextOutputVO);
		outputDAO.delete(outputPK, TextOutputVO.class);
	}

	@Test
	public void testInvalidUpdate() {
		textOutputVO = JUnitTestUtils.generateTextOutputVO(actionId, userId);
		assertNull(outputDAO.query(JUnitTestUtils.INVALID_PK, TextOutputVO.class));

		int result = outputDAO.update(textOutputVO);
		assertTrue(result < 0);
		assertNull(outputDAO.query(JUnitTestUtils.INVALID_PK, TextOutputVO.class));
	}

	@Test
	public void testQuery() {
		textOutputVO = JUnitTestUtils.generateTextOutputVO(actionId, userId);
		int outputPK = outputDAO.insert(textOutputVO);
		assertTrue(outputPK > 0);

		OutputVO resultOutputVO = outputDAO.query(outputPK, TextOutputVO.class);
		assertNotNull(resultOutputVO);
		assertEquals(textOutputVO.getActionId(), resultOutputVO.getActionId());
		assertEquals(textOutputVO.getValue(), resultOutputVO.getValue());
		assertEquals(textOutputVO.getDesc(), resultOutputVO.getDesc());
		assertEquals(textOutputVO.getWorkerId(), resultOutputVO.getWorkerId());
		assertEquals(textOutputVO.getIndicator(), resultOutputVO.getIndicator());
		assertTrue(resultOutputVO instanceof TextOutputVO);
		outputDAO.delete(outputPK, TextOutputVO.class);
	}

	@Test
	public void testInvalidQuery() {
		int outputPK = JUnitTestUtils.INVALID_PK;
		OutputVO outputVO = outputDAO.query(outputPK, TextOutputVO.class);
		assertNull(outputVO);
	}

	@Test
	public void testQueryByAction() {
		textOutputVO = JUnitTestUtils.generateTextOutputVO(actionId, userId);
		int outputPK1 = outputDAO.insert(textOutputVO);
		assertTrue(outputPK1 > 0);
		int outputPK2 = outputDAO.insert(textOutputVO);
		assertTrue(outputPK2 > 0);

		List<OutputVO> resultOutputVOList = outputDAO.queryOutputListByAction(actionId);
		assertEquals(2, resultOutputVOList.size());
		List<Integer> resultOutputPKList = new ArrayList<Integer>();
		for (OutputVO vo : resultOutputVOList)
			resultOutputPKList.add(vo.getId());
		assertTrue(resultOutputPKList.contains(outputPK1));
		assertTrue(resultOutputPKList.contains(outputPK2));
		outputDAO.delete(outputPK1, TextOutputVO.class);
		outputDAO.delete(outputPK2, TextOutputVO.class);
	}

	@Test
	public void testInvalidQueryByLocation() {
		textOutputVO = JUnitTestUtils.generateTextOutputVO(actionId, userId);
		int outputPK1 = outputDAO.insert(textOutputVO);
		assertTrue(outputPK1 > 0);
		int outputPK2 = outputDAO.insert(textOutputVO);
		assertTrue(outputPK2 > 0);

		List<OutputVO> resultOutputVOList = outputDAO.queryOutputListByAction(JUnitTestUtils.INVALID_PK);
		assertEquals(0, resultOutputVOList.size());
		outputDAO.delete(outputPK1, TextOutputVO.class);
		outputDAO.delete(outputPK2, TextOutputVO.class);
	}
	
	@Test
	public void testQueryByActionAndIndicator() {
		TextOutputVO textOutputVO1 = JUnitTestUtils.generateTextOutputVO(actionId, userId);
		textOutputVO1.setIndicator(1);
		int outputPK1 = outputDAO.insert(textOutputVO1);
		assertTrue(outputPK1 > 0);
		TextOutputVO textOutputVO2 = JUnitTestUtils.generateTextOutputVO(actionId, userId);
		textOutputVO2.setIndicator(2);
		int outputPK2 = outputDAO.insert(textOutputVO2);
		assertTrue(outputPK2 > 0);

		List<OutputVO> resultOutputVOList = outputDAO.queryOutputListByActionAndIndicator(actionId, textOutputVO1.getIndicator());
		assertEquals(1, resultOutputVOList.size());
		outputDAO.delete(outputPK1, TextOutputVO.class);
		outputDAO.delete(outputPK2, TextOutputVO.class);
	}

	@Test
	public void testInvalidQueryByActionAndIndicator() {
		TextOutputVO textOutputVO1 = JUnitTestUtils.generateTextOutputVO(actionId, userId);
		textOutputVO1.setIndicator(1);
		int outputPK1 = outputDAO.insert(textOutputVO1);
		assertTrue(outputPK1 > 0);
		TextOutputVO textOutputVO2 = JUnitTestUtils.generateTextOutputVO(actionId, userId);
		textOutputVO2.setIndicator(2);
		int outputPK2 = outputDAO.insert(textOutputVO2);
		assertTrue(outputPK2 > 0);

		List<OutputVO> resultOutputVOList = outputDAO.queryOutputListByActionAndIndicator(JUnitTestUtils.INVALID_PK, textOutputVO1.getIndicator());
		assertEquals(0, resultOutputVOList.size());
		resultOutputVOList = outputDAO.queryOutputListByActionAndIndicator(actionId, JUnitTestUtils.INVALID_PK);
		assertEquals(0, resultOutputVOList.size());
		outputDAO.delete(outputPK1, TextOutputVO.class);
		outputDAO.delete(outputPK2, TextOutputVO.class);
	}

	@Test
	public void testQueryByActionAndUser() {
		TextOutputVO textOutputVO1 = JUnitTestUtils.generateTextOutputVO(actionId, userId);
		textOutputVO1.setIndicator(1);
		int outputPK1 = outputDAO.insert(textOutputVO1);
		assertTrue(outputPK1 > 0);
		TextOutputVO textOutputVO2 = JUnitTestUtils.generateTextOutputVO(actionId, userId);
		textOutputVO2.setIndicator(2);
		int outputPK2 = outputDAO.insert(textOutputVO2);
		assertTrue(outputPK2 > 0);

		List<OutputVO> resultOutputVOList = outputDAO.queryOutputListByActionAndUser(actionId, userId, textOutputVO1.getIndicator());
		assertEquals(1, resultOutputVOList.size());
		outputDAO.delete(outputPK1, TextOutputVO.class);
		outputDAO.delete(outputPK2, TextOutputVO.class);
	}

	@Test
	public void testInvalidQueryByActionAndUser() {
		TextOutputVO textOutputVO1 = JUnitTestUtils.generateTextOutputVO(actionId, userId);
		textOutputVO1.setIndicator(1);
		int outputPK1 = outputDAO.insert(textOutputVO1);
		assertTrue(outputPK1 > 0);
		TextOutputVO textOutputVO2 = JUnitTestUtils.generateTextOutputVO(actionId, userId);
		textOutputVO2.setIndicator(2);
		int outputPK2 = outputDAO.insert(textOutputVO2);
		assertTrue(outputPK2 > 0);

		List<OutputVO> resultOutputVOList = outputDAO.queryOutputListByActionAndUser(JUnitTestUtils.INVALID_PK, userId, textOutputVO1.getIndicator());
		assertEquals(0, resultOutputVOList.size());
		resultOutputVOList = outputDAO.queryOutputListByActionAndUser(actionId, JUnitTestUtils.INVALID_PK, textOutputVO1.getIndicator());
		assertEquals(0, resultOutputVOList.size());
		resultOutputVOList = outputDAO.queryOutputListByActionAndUser(actionId, userId, JUnitTestUtils.INVALID_PK);
		assertEquals(0, resultOutputVOList.size());
		outputDAO.delete(outputPK1, TextOutputVO.class);
		outputDAO.delete(outputPK2, TextOutputVO.class);
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
