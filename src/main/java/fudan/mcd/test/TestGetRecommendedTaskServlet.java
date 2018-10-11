package fudan.mcd.test;

import fudan.mcd.dao.abs.AbstractDAO;
import fudan.mcd.dao.impl.*;
import fudan.mcd.servlet.GetRecommendedTaskServlet.*;
import fudan.mcd.servlet.GetRecommendedTaskServlet.ResponseBO.*;
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

public class TestGetRecommendedTaskServlet {
	private static final String SERVLET_NAME = "GetRecommendedTaskServlet";
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
		taskVO.setStatus(TaskVO.STATUS_ONGOING);
		taskId = taskDAO.insert(taskVO);
		stageDAO = new StageDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		StageVO stageVO = JUnitTestUtils.generateStageVO(taskId);
		stageVO.setIndex(taskVO.getCurrentStage());
		stageId = stageDAO.insert(stageVO);
		locationDAO = new LocationDAO(AbstractDAO.DEVELOP_CONFIG_PATH);
		LocationVO locationSrcVO = JUnitTestUtils.generateLocationVO(stageId);
		locationSrcVO.setType(LocationVO.TYPE_SRC);
		locationSrcId = locationDAO.insert(locationSrcVO);
		LocationVO locationDstVO = JUnitTestUtils.generateLocationVO(stageId);
		locationDstVO.setType(LocationVO.TYPE_DEST);
		locationDstId = locationDAO.insert(locationDstVO);
	}

	@Test
	public void testPostData() {
		TaskVO taskVO2 = JUnitTestUtils.generateTaskVO(templateId, userId);
		taskVO2.setStatus(TaskVO.STATUS_FINISHED);
		int taskPK2 = taskDAO.insert(taskVO2);

		TaskVO taskVO3 = JUnitTestUtils.generateTaskVO(templateId, userId);
		taskVO3.setStatus(TaskVO.STATUS_ONGOING);
		int taskPK3 = taskDAO.insert(taskVO3);
		StageVO stageVO3 = JUnitTestUtils.generateStageVO(taskPK3);
		stageVO3.setIndex(taskVO3.getCurrentStage());
		int stagePK3 = stageDAO.insert(stageVO3);
		LocationVO locationSrcVO3 = JUnitTestUtils.generateLocationVO(stagePK3);
		locationSrcVO3.setType(LocationVO.TYPE_SRC);
		int locationSrcPK3 = locationDAO.insert(locationSrcVO3);
		LocationVO locationDstVO3 = JUnitTestUtils.generateLocationVO(stagePK3);
		locationDstVO3.setType(LocationVO.TYPE_DEST);
		int locationDstPK3 = locationDAO.insert(locationDstVO3);

		try {
			RequestBO requestBO = new RequestBO();
			Map<String, String> map = new HashMap<String, String>();
			map.put("data", JSONUtils.toJSONString(requestBO));
			String response = HttpPostProxy.doPost(SERVLET_NAME, map);
			ServletResponseData responseData = JSONUtils.toBean(response, ServletResponseData.class);
			assertEquals(1, responseData.getResult());
			ResponseBO responseBO = JSONUtils.toBean(responseData.getData(), ResponseBO.class);
			List<TaskBO> taskBOList = responseBO.getTasks();
			assertEquals(2, taskBOList.size());

			UserVO userVO = userDAO.query(userId);
			TaskVO taskVO = taskDAO.query(taskPK3);
			LocationVO locationDstVO = locationDAO.query(locationDstPK3);
			for (TaskBO taskBO : taskBOList) {
				if (taskBO.getId() == taskPK3) {
					assertEquals(taskVO.getId(), taskBO.getId());
					assertEquals(taskVO.getTemplateId(), taskBO.getTemplateId());
					assertEquals(taskVO.getUserId(), taskBO.getUserId());
					assertEquals(userVO.getAccount(), taskBO.getRequester());
//					assertEquals(taskVO.getTitle(), taskBO.getTitle());
//					assertEquals(taskVO.getDescription(), taskBO.getDescription());
					assertEquals(String.format("%d/%d", taskVO.getCurrentStage(), templateDAO.query(templateId).getTotalStageNum()),
							taskBO.getProgress());
					assertEquals(taskVO.getCurrentStage(), taskBO.getCurrentStage());
					assertEquals(taskVO.getBonusReward(), taskBO.getBonusReward(), JUnitTestUtils.FLOAT_ERROR);
					JUnitTestUtils.assertTimestampEquals(taskVO.getPublishTime(), taskBO.getPublishTime());
					JUnitTestUtils.assertTimestampEquals(taskVO.getDeadline(), taskBO.getDeadline());
					assertEquals(locationDstVO.getLongitude(), taskBO.getLongitude(), JUnitTestUtils.FLOAT_ERROR);
					assertEquals(locationDstVO.getLatitude(), taskBO.getLatitude(), JUnitTestUtils.FLOAT_ERROR);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			locationDAO.delete(locationSrcPK3);
			locationDAO.delete(locationDstPK3);
			stageDAO.delete(stagePK3);
			taskDAO.delete(taskPK2);
			taskDAO.delete(taskPK3);
		}
	}

	@After
	public void recycle() {
		locationDAO.delete(locationSrcId);
		locationDAO.delete(locationDstId);
		stageDAO.delete(stageId);
		taskDAO.delete(taskId);
		templateDAO.delete(templateId);
		userDAO.delete(userId);
	}

}
