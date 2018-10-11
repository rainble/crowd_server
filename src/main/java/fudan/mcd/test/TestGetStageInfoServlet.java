package fudan.mcd.test;

import fudan.mcd.dao.abs.AbstractDAO;
import fudan.mcd.dao.impl.*;
import fudan.mcd.servlet.GetStageInfoServlet.*;
import fudan.mcd.servlet.GetStageInfoServlet.ResponseBO.*;
import fudan.mcd.servlet.ServletResponseData;
import fudan.mcd.utils.JSONUtils;
import fudan.mcd.vo.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class TestGetStageInfoServlet {
	private static final String SERVLET_NAME = "GetStageInfoServlet";
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
	private int undertakeId;
	private UndertakeDAO undertakeDAO;

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
		undertakeDAO = new UndertakeDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		UndertakeVO undertakeVO = JUnitTestUtils.generateUndertakeVO(userId, stageId);
		undertakeId = undertakeDAO.insert(undertakeVO);
	}

	@Test
	public void testPostCorrectData() {
		UserVO userVO = userDAO.query(userId);
		StageVO stageVO = stageDAO.query(stageId);
		LocationVO locationSrcVO = locationDAO.query(locationSrcId);
		InputVO inputSrcVO = inputDAO.query(inputSrcId);
		InputVO inputDstVO = inputDAO.query(outputDstId);
		OutputVO outputSrcVO = outputDAO.query(outputSrcId, EnumOutputVO.class);
		OutputVO outputDstVO = outputDAO.query(outputDstId, EnumOutputVO.class);
		LocationVO locationDstVO = locationDAO.query(locationDstId);

		RequestBO requestBO = new RequestBO();
		requestBO.setStageId(stageId);
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", JSONUtils.toJSONString(requestBO));

		String response = HttpPostProxy.doPost(SERVLET_NAME, map);
		ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertEquals(1, responseData.getResult());
		ResponseBO responseBO = JSONUtils.toBean(responseData.getData(), ResponseBO.class);

		assertEquals(stageVO.getId(), responseBO.getId());
		assertEquals(stageVO.getName(), responseBO.getName());
		assertEquals(stageVO.getDescription(), responseBO.getDesc());
		assertEquals(stageVO.getReward(), responseBO.getReward(), JUnitTestUtils.FLOAT_ERROR);
		JUnitTestUtils.assertTimestampEquals(stageVO.getDeadline(), responseBO.getDdl());
		assertEquals(stageVO.getWorkerNum(), responseBO.getWorkerNum());
		List<String> workerList = responseBO.getWorkerNames();
		assertEquals(1, workerList.size());
		assertEquals(userVO.getAccount(), workerList.get(0));
		assertEquals(stageVO.getRestrictions(), responseBO.getRestrictions());

		List<LocationBO> locationBOList = responseBO.getLocations();
		assertEquals(2, locationBOList.size());
		LocationBO locationSrcBO = locationBOList.get(0);
		LocationBO locationDstBO = locationBOList.get(1);
		assertEquals(locationSrcVO.getType(), locationSrcBO.getType());
		assertEquals(locationSrcVO.getAddress(), locationSrcBO.getAddress());
		assertEquals(locationSrcVO.getLongitude(), locationSrcBO.getLongitude(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(locationSrcVO.getLatitude(), locationSrcBO.getLatitude(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(locationDstVO.getType(), locationDstBO.getType());
		assertEquals(locationDstVO.getAddress(), locationDstBO.getAddress());
		assertEquals(locationDstVO.getLongitude(), locationDstBO.getLongitude(), JUnitTestUtils.FLOAT_ERROR);
		assertEquals(locationDstVO.getLatitude(), locationDstBO.getLatitude(), JUnitTestUtils.FLOAT_ERROR);

		List<InputBO> inputSrcBOList = locationSrcBO.getInputs();
		assertEquals(1, inputSrcBOList.size());
		InputBO inputSrcBO = inputSrcBOList.get(0);
		assertEquals(inputSrcBO.getType(), inputSrcVO.getType());
		assertEquals(inputSrcBO.getDesc(), inputSrcVO.getDesc());
		assertEquals(inputSrcBO.getValue(), inputSrcVO.getValue());
		List<OutputBO> outputSrcBOList = locationSrcBO.getOutputs();
		assertEquals(1, outputSrcBOList.size());
		OutputBO outputSrcBO = outputSrcBOList.get(0);
		assertEquals(outputSrcBO.getDesc(), outputSrcVO.getDesc());
		assertEquals(outputSrcBO.getValue(), outputSrcVO.getValue());
		List<InputBO> inputDstBOList = locationDstBO.getInputs();
		assertEquals(1, inputDstBOList.size());
		InputBO inputDstBO = inputDstBOList.get(0);
		assertEquals(inputDstBO.getType(), inputDstVO.getType());
		assertEquals(inputDstBO.getDesc(), inputDstVO.getDesc());
		assertEquals(inputDstBO.getValue(), inputDstVO.getValue());
		List<OutputBO> outputDstBOList = locationDstBO.getOutputs();
		assertEquals(1, outputDstBOList.size());
		OutputBO outputDstBO = outputDstBOList.get(0);
		assertEquals(outputDstBO.getDesc(), outputDstVO.getDesc());
		assertEquals(outputDstBO.getValue(), outputDstVO.getValue());
	}

	@Test
	public void testPostInvalidStage() {
		RequestBO requestBO = new RequestBO();
		requestBO.setStageId(JUnitTestUtils.INVALID_PK);
		Map<String, String> map = new HashMap<String, String>();
		map.put("data", JSONUtils.toJSONString(requestBO));

		String response = HttpPostProxy.doPost(SERVLET_NAME, map);
		ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
		assertEquals(-1, responseData.getResult());
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
		undertakeDAO.delete(undertakeId);
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
