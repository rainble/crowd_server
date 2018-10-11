package fudan.mcd.service;

import fudan.mcd.dao.impl.*;
import fudan.mcd.runtime.JPushUtil;
import fudan.mcd.servlet.ServletUtils;
import fudan.mcd.utils.WeixinUtil;
import fudan.mcd.vo.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublishTaskService extends AbstractService {

	private static final Log LOG = LogFactory.getLog(PublishTaskService.class);
	public static final int INSERT_SUCCESS = 1, INSERT_FAIL = -1;
	public static final int LOCATION_DEST = 1;
	private OutputDAO outputDAO = new OutputDAO(context);
	private int task_pk;

	public PublishTaskService(ServletContext context) {
		super(context);
		task_pk = -1;
	}

	public int insertTask(TaskVO tvo) {
		// DAO of the TaskVO
		TaskDAO tDAO = new TaskDAO(context);
		task_pk = tDAO.insert(tvo);
		if (task_pk > 0) {
			System.out.println(String.format("Insert task successfully!"));
			return INSERT_SUCCESS;
		}
		else {
			System.out.println(String.format("Insert task fail!"));
			return INSERT_FAIL;
		}
	}

	public int getTaskPK() {
		return task_pk;
	}

	public int insertStage(StageVO svo) {
		// DAO of the StageVO
		StageDAO sDAO = new StageDAO(context);
		int stageId = sDAO.insert(svo);
		if (stageId > 0)
			return stageId;
		else
			return INSERT_FAIL;
	}

	public int insertLocation(LocationVO location) {
		// DAO of the LocationVO
		LocationDAO lDAO = new LocationDAO(context);
		int locationId = lDAO.insert(location);
		if (locationId > 0)
			return locationId;
		else
			return INSERT_FAIL;
	}

	public int insertAction(ActionVO avo) {
		// DAO of the ActionVO
		ActionDAO aDAO = new ActionDAO(context);
		int actionId = aDAO.insert(avo);
		if (actionId > 0)
			return actionId;
		else
			return INSERT_FAIL;
	}

	public int insertInput(List<InputVO> insertIvoList) {
		// DAO of the InputVO
		InputDAO iDAO = new InputDAO(context);

		int result = -1;
		for (InputVO ivo : insertIvoList) {
			result = iDAO.insert(ivo);
		}

		if (result > 0)
			return INSERT_SUCCESS;
		else
			return INSERT_FAIL;
	}

	public int updateTemplate(int templateId) {
		TemplateDAO tpDAO = new TemplateDAO(context);
		
		TemplateVO templateVO = tpDAO.query(templateId);
		templateVO.setHeat(templateVO.getHeat() + 1);
		int result = tpDAO.update(templateVO);
		
		return result;
	}

	public int insertPictureOutput(PictureOutputVO pictureOutputVO) {
		int result = outputDAO.insert(pictureOutputVO);

		if (result > 0)
			return INSERT_SUCCESS;
		else
			return INSERT_FAIL;
	}

	public int insertTextOutput(List<TextOutputVO> textOutputList) {
		int result = -1;
		for(TextOutputVO textOutputVO : textOutputList){
			result = outputDAO.insert(textOutputVO);
		}
		if (result > 0)
			return INSERT_SUCCESS;
		else
			return INSERT_FAIL;
	}

	public int insertNumericalOutput(List<NumericalOutputVO> numericalOutputList) {
		int result = -1;
		for(NumericalOutputVO numericalOutput : numericalOutputList){
			result = outputDAO.insert(numericalOutput);
		}

		if (result > 0)
			return INSERT_SUCCESS;
		else
			return INSERT_FAIL;
	}

	public int insertEnumOutput(List<EnumOutputVO> enumOutputList) {
		int result = -1;
		for(EnumOutputVO enumOutput : enumOutputList){
			result = outputDAO.insert(enumOutput);
		}

		if (result > 0)
			return INSERT_SUCCESS;
		else
			return INSERT_FAIL;
	}

	public int updateCredit(TaskVO tvo, int requesterId, double totalReward) {
		int result = -1;
		UserDAO uDAO = new UserDAO(context);
		UserVO uvo = uDAO.query(requesterId);
		if(uvo != null){
			uvo.setPublishCredit(uvo.getPublishCredit() - totalReward);
			result = uDAO.update(uvo);
			
			/**
			 * 给所有worker推送消息
			 */
			//APP端推送消息
			List<UserVO> users = uDAO.queryAllUser();
			String content = "有新任务发布，点击查看详情！";
			Map<String,String> map = new HashMap<String,String>();
			map.put("type", JPushUtil.TYPE_OF_PUBLISHTASK);
			map.put("requester", uvo.getAccount());
			map.put("taskTitle", tvo.getTitle());
			map.put("totalReward", String.valueOf(totalReward));
			map.put("ddl", ServletUtils.getTime(tvo.getDeadline()));
			//除去JPush推送，同时构造微信推送用户列表：除去发布者本身
			List<String> weixinIds = new ArrayList<String>();
			for(UserVO user : users){
				if(user.getId() != requesterId){
					String alias = String.valueOf(user.getId());
					JPushUtil.pushMessage(alias, content,map);
					weixinIds.add(user.getWeChatId());
				}
			}
			//微信端推送消息
			WeixinUtil weixin = new WeixinUtil();
			Map<String,String> data = new HashMap<String, String>();
			//查询当前目的地信息
			StageDAO sDAO = new StageDAO(context);
			StageVO stage = sDAO.queryByTaskAndIndex(tvo.getId(), tvo.getCurrentStage());
			LocationDAO lDao = new LocationDAO(context);
			LocationVO currentDest = lDao.queryByStageAndType(stage.getId(), LOCATION_DEST);
			String address = currentDest.getAddress().replace("=", " ");
			
			//按照微信企业号的格式要求，构造touser
			String toUser = "";
			for(String weixinId : weixinIds){
				toUser += weixinId;
				toUser += "|";
			}
			toUser = toUser.substring(0, toUser.length() - 1);
			data.put("touser", toUser);
			data.put("title","新任务发布通知");
			String description = String.format("用户%s发布了新任务：%s。当前目的地为%s，截止时间为%s,任务总奖励为%s。请点击打开APP查看具体详细信息", 
					uvo.getAccount(),tvo.getTitle(),address,ServletUtils.getTime(tvo.getDeadline()),String.valueOf(totalReward));
			LOG.info(description);
			data.put("description", description);
			data.put("url", "http://118.178.94.215/taskCard.php");
			weixin.sendMessage("textcard",data);
			
			return result;
		}
		else
			return -1;
		
		
	}
}
