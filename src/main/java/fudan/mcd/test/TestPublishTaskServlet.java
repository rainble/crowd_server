package fudan.mcd.test;

import fudan.mcd.dao.abs.AbstractDAO;
import fudan.mcd.dao.impl.*;
import fudan.mcd.servlet.PublishTaskServlet.*;
import fudan.mcd.servlet.PublishTaskServlet.RequestBO.*;
import fudan.mcd.servlet.ServletResponseData;
import fudan.mcd.utils.JSONUtils;
import fudan.mcd.vo.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TestPublishTaskServlet {
	private static final String SERVLET_NAME = "PublishTaskServlet";
	private int userId;
	private UserDAO userDAO;
	private int templateId;
	private TemplateDAO templateDAO;
	private int taskId;
	private TaskDAO taskDAO;
	private int stageId;
	private StageDAO stageDAO;
	private int locationSrcId;
	private int locationDstId;
	private LocationDAO locationDAO;
	private int actionSrcId;
	private int actionDstId;
	private ActionDAO actionDAO;
	private int inputSrcId;
	private int inputDstId;
	private InputDAO inputDAO;
	private int outputSrcId;
	private int outputDstId;
	private OutputDAO outputDAO;

	@Before
	public void init() {
		userDAO = new UserDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		UserVO userVO = JUnitTestUtils.generateUserVO();
		userId = userDAO.insert(userVO);
		templateDAO = new TemplateDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		TemplateVO templateVO = JUnitTestUtils.generateTemplateVO(userId);
		templateId = templateDAO.insert(templateVO);
		taskDAO = new TaskDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		TaskVO taskVO = JUnitTestUtils.generateTaskVO(templateId, userId);
		taskId = taskDAO.insert(taskVO);
		stageDAO = new StageDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		StageVO stageVO = JUnitTestUtils.generateStageVO(taskId);
		stageId = stageDAO.insert(stageVO);
		locationDAO = new LocationDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		LocationVO locationSrcVO = JUnitTestUtils.generateLocationVO(stageId);
		locationSrcVO.setType(LocationVO.TYPE_SRC);
		locationSrcId = locationDAO.insert(locationSrcVO);
		LocationVO locationDstVO = JUnitTestUtils.generateLocationVO(stageId);
		locationDstVO.setType(LocationVO.TYPE_DEST);
		locationDstId = locationDAO.insert(locationDstVO);
		actionDAO = new ActionDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		ActionVO actionSrcVO = JUnitTestUtils.generateActionVO(locationSrcId);
		ActionVO actionDstVO = JUnitTestUtils.generateActionVO(locationDstId);
		actionSrcId = actionDAO.insert(actionSrcVO);
		actionDstId = actionDAO.insert(actionDstVO);
		inputDAO = new InputDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		InputVO srcInputVO = JUnitTestUtils.generateInputVO(actionSrcId);
		InputVO dstInputVO = JUnitTestUtils.generateInputVO(actionDstId);
		inputSrcId = inputDAO.insert(srcInputVO);
		inputDstId = inputDAO.insert(dstInputVO);
		outputDAO = new OutputDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		OutputVO srcOutputVO = JUnitTestUtils.generateEnumOutputVO(actionSrcId, userId);
		OutputVO dstOutputVO = JUnitTestUtils.generateEnumOutputVO(actionDstId, userId);
		outputSrcId = outputDAO.insert(srcOutputVO);
		outputDstId = outputDAO.insert(dstOutputVO);
	}

	@Test
	public void testPostCorrectData() {
		TemplateVO templateVO = templateDAO.query(templateId);
		TaskVO taskVO = taskDAO.query(taskId);
		StageVO stageVO = stageDAO.query(stageId);
		LocationVO locationSrcVO = locationDAO.query(locationSrcId);
		LocationVO locationDstVO = locationDAO.query(locationDstId);
		InputVO inputVO = inputDAO.query(inputSrcId);
		OutputVO outputVO = outputDAO.query(outputSrcId, EnumOutputVO.class);

		RequestBO requestBO = new RequestBO();
		requestBO.setTemplateId(templateVO.getId());
		requestBO.setRequesterId(taskVO.getUserId());
		requestBO.setTitle(taskVO.getTitle());
		requestBO.setDescription(taskVO.getDescription());
		requestBO.setBonusReward(taskVO.getBonusReward());
		requestBO.setPublishTime(taskVO.getPublishTime());
		requestBO.setDeadline(taskVO.getDeadline());

		List<StageBO> stages = new ArrayList<StageBO>();
		StageBO stageBO = new StageBO();
		stageBO.setName(stageVO.getName());
		stageBO.setDesc(stageVO.getDescription());
		stageBO.setReward(stageVO.getReward());
		stageBO.setStageIndex(stageVO.getIndex());
		stageBO.setDdl(stageVO.getDeadline());
		stageBO.setWorkerNum(stageVO.getWorkerNum());
		stageBO.setAggregateMethod(stageVO.getAggregateMethod());
		stageBO.setRestrictions(stageVO.getRestrictions());

		List<LocationBO> locations = new ArrayList<LocationBO>();
		LocationBO locationSrcBO = new LocationBO();
		locationSrcBO.setType(locationSrcVO.getType());
		locationSrcBO.setAddress(locationSrcVO.getAddress());
		locationSrcBO.setLatitude(locationSrcVO.getLatitude());
		locationSrcBO.setLongitude(locationSrcVO.getLongitude());

		LocationBO locationDstBO = new LocationBO();
		locationDstBO.setType(locationDstVO.getType());
		locationDstBO.setAddress(locationDstVO.getAddress());
		locationDstBO.setLatitude(locationDstVO.getLatitude());
		locationDstBO.setLongitude(locationDstVO.getLongitude());

		List<InputBO> srcInputs = new ArrayList<InputBO>();
		InputBO inputBO = new InputBO();
		inputBO.setId(inputVO.getId());
		inputBO.setActionId(actionSrcId);
		inputBO.setType(inputVO.getType());
		inputBO.setDesc(inputVO.getDesc());
		inputBO.setValue(inputVO.getValue());
		srcInputs.add(inputBO);

		List<OutputBO> srcOutputs = new ArrayList<OutputBO>();
		OutputBO outputBO = new OutputBO();
		outputBO.setId(outputVO.getId());
		outputBO.setActionId(actionSrcId);
		outputBO.setDesc(outputVO.getDesc());
		outputBO.setValue(outputVO.getValue());
		srcOutputs.add(outputBO);

		locationSrcBO.setInputs(srcInputs);
		locationSrcBO.setOutputs(srcOutputs);

		List<InputBO> dstInputs = new ArrayList<InputBO>();
		InputBO dstInputBO = new InputBO();
		dstInputBO.setId(inputVO.getId());
		dstInputBO.setActionId(actionDstId);
		dstInputBO.setType(inputVO.getType());
		dstInputBO.setDesc(inputVO.getDesc());
		dstInputBO.setValue(inputVO.getValue());
		dstInputs.add(dstInputBO);

		List<OutputBO> dstOutputs = new ArrayList<OutputBO>();
		OutputBO dstOutputBO = new OutputBO();
		dstOutputBO.setId(outputVO.getId());
		dstOutputBO.setActionId(actionDstId);
		dstOutputBO.setDesc(outputVO.getDesc());
		dstOutputBO.setValue(outputVO.getValue());
		dstOutputs.add(dstOutputBO);

		locationDstBO.setInputs(dstInputs);
		locationDstBO.setOutputs(dstOutputs);

		locations.add(locationSrcBO);
		locations.add(locationDstBO);
		stageBO.setLocations(locations);

		stages.add(stageBO);
		requestBO.setStages(stages);

		Map<String, String> map = new HashMap<String, String>();
		map.put("data", JSONUtils.toJSONString(requestBO));

		String response = HttpPostProxy.doPost(SERVLET_NAME, map);
		ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertEquals(1, responseData.getResult());
		ResponseBO responseBO = JSONUtils.toBean(responseData.getData(), ResponseBO.class);
		int newTaskId = responseBO.getTaskId();
		assertTrue(newTaskId > 0);

		TaskVO newTaskVO = taskDAO.query(newTaskId);
		assertNotNull(newTaskVO);
		List<StageVO> newStageList = stageDAO.queryStageListByTask(newTaskId);
		assertEquals(1, newStageList.size());
		StageVO newStageVO = newStageList.get(0);
		int newStageId = newStageVO.getId();
		LocationVO newSrcLocationVO = locationDAO.queryByStageAndType(newStageId, LocationVO.TYPE_SRC);
		assertNotNull(newSrcLocationVO);
		int newSrcLocationId = newSrcLocationVO.getId();
		LocationVO newDstLocationVO = locationDAO.queryByStageAndType(newStageId, LocationVO.TYPE_DEST);
		assertNotNull(newDstLocationVO);
		int newDstLocationId = newDstLocationVO.getId();
		List<ActionVO> newSrcActionVOList = actionDAO.queryActionListByLocation(newSrcLocationId);
		assertEquals(1, newSrcActionVOList.size());
		ActionVO newSrcActionVO = newSrcActionVOList.get(0);
		int newSrcActionId = newSrcActionVO.getId();
		List<ActionVO> newDstActionVOList = actionDAO.queryActionListByLocation(newDstLocationId);
		assertEquals(1, newDstActionVOList.size());
		ActionVO newDstActionVO = newDstActionVOList.get(0);
		int newDstActionId = newDstActionVO.getId();
		List<InputVO> newSrcInputVOList = inputDAO.queryInputListByAction(newSrcActionId);
		assertEquals(1, newSrcInputVOList.size());
		InputVO newSrcInputVO = newSrcInputVOList.get(0);
		int newSrcInputId = newSrcInputVO.getId();
		List<OutputVO> newSrcOutputVOList = outputDAO.queryOutputListByAction(newSrcActionId);
		assertEquals(1, newSrcOutputVOList.size());
		OutputVO newSrcOutputVO = newSrcOutputVOList.get(0);
		int newSrcOutputId = newSrcOutputVO.getId();
		List<InputVO> newDstInputVOList = inputDAO.queryInputListByAction(newDstActionId);
		assertEquals(1, newDstInputVOList.size());
		InputVO newDstInputVO = newDstInputVOList.get(0);
		int newDstInputId = newDstInputVO.getId();
		List<OutputVO> newDstOutputVOList = outputDAO.queryOutputListByAction(newDstActionId);
		assertEquals(1, newDstOutputVOList.size());
		OutputVO newDstOutputVO = newDstOutputVOList.get(0);
		int newDstOutputId = newDstOutputVO.getId();

		inputDAO.delete(newSrcInputId);
		inputDAO.delete(newDstInputId);
		outputDAO.delete(newSrcOutputId, EnumOutputVO.class);
		outputDAO.delete(newDstOutputId, EnumOutputVO.class);
		actionDAO.delete(newSrcActionId);
		actionDAO.delete(newDstActionId);
		locationDAO.delete(newSrcLocationId);
		locationDAO.delete(newDstLocationId);
		stageDAO.delete(newStageId);
		taskDAO.delete(newTaskId);
	}

	@Test
	public void testPostEmptyData() {
		Map<String, String> map = new HashMap<String, String>();
		String response = HttpPostProxy.doPost(SERVLET_NAME, map);
		ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertEquals(ServletResponseData.RESULT_PARSE_FAILED, responseData.getResult());
	}

	@After
	public void recycle() {
		inputDAO.delete(inputSrcId);
		inputDAO.delete(inputDstId);
		outputDAO.delete(outputSrcId, EnumOutputVO.class);
		outputDAO.delete(outputDstId, EnumOutputVO.class);
		actionDAO.delete(actionSrcId);
		actionDAO.delete(actionDstId);
		locationDAO.delete(locationSrcId);
		locationDAO.delete(locationDstId);
		stageDAO.delete(stageId);
		taskDAO.delete(taskId);
		templateDAO.delete(templateId);
		userDAO.delete(userId);
	}

}
