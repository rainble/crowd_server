package fudan.mcd.service;

import fudan.mcd.dao.impl.*;
import fudan.mcd.runtime.Constant;
import fudan.mcd.runtime.JPushUtil;
import fudan.mcd.servlet.ServletUtils;
import fudan.mcd.utils.WeixinUtil;
import fudan.mcd.vo.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompleteTaskService extends AbstractService {
	private static final Log LOG = LogFactory.getLog(CompleteTaskService.class);
	public static final int UNDERTAKE_ONGOING = 0, UNDERTAKE_FINISHED = 1, UNDERTAKE_EXPIRED = -1, UNDERTAKE_CREDIT_GIVEN = 5;
	private int pk;
	private int userId;
	private double longitude;
	private double latitude;
	UndertakeDAO undertakeDAO = new UndertakeDAO(context);
	TaskDAO tDAO = new TaskDAO(context);
	TemplateDAO templateDAO = new TemplateDAO(context);
	StageDAO stageDAO = new StageDAO(context);
	UserDAO userDAO = new UserDAO(context);
	OutputDAO outputDAO = new OutputDAO(context);
	
	public CompleteTaskService(ServletContext context) {
		super(context);
		pk = -1;
		userId = -1;
	}
	
	public CompleteTaskService(ServletContext context, double longitude, double latitude){
		super(context);
		pk = -1;
		userId = -1;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public int updataUndertakeVO(UndertakeVO uvo) {
		LOG.info(String.format("Begin to process the complete task request: [ userId = %d, stageId = %d ].", uvo.getUserId(), uvo.getStageId()));
		// Check user id
		if (userDAO.query(uvo.getUserId()) == null)
			return -1;
		userId = uvo.getUserId();
		
		// Check stage id
		StageVO stage = stageDAO.query(uvo.getStageId());
		if (stage == null)
			return -1;

		// Check undertake id
		UndertakeVO tempUndertakeVO = undertakeDAO.queryByUserAndStage(uvo.getUserId(), uvo.getStageId());
		if (tempUndertakeVO == null) {
			LOG.info("Unable to find corresponding undertake.");
			return -1;
		}
		else {
			pk = tempUndertakeVO.getId();
			//LOG.info("Find corresponding undertake successfully.");
		}
		int updateResult = -1;
		
		//更新stage的状态（是否多人情况不同）；更新task的状态（最后一个stage完成标志着task也完成）
		
		//只有一个worker的情况
		if(stage.getWorkerNum() == 1){

			updateResult = updateUndertakeInfo(tempUndertakeVO,uvo);//更新该undertake本身的信息
			updateResult = updateStageInfo(stage);//更新stage的状态为已完成，1的状态
			updateResult = updateTaskInfo(stage);//更新task的信息
			
		}
		//有多个worker的情况
		else{
			int workerNum = stage.getWorkerNum();
			int currentNum = undertakeDAO.queryByStage(stage.getId()).size();
			List<UndertakeVO> undertakes = undertakeDAO.queryByStage(stage.getId());
			//aggregateMethod：1是select the first finished, 2是select the most workers' answer， 3是user choose freely
			int aggregateMethod = stage.getAggregateMethod();
			
			//选择第一个完成的
			if(aggregateMethod == 1){
				//记录之前是否已经有完成的
				boolean isFinished = false;
				for(UndertakeVO undertake : undertakes){
					if(undertake.getStatus() == UNDERTAKE_FINISHED){
						isFinished = true; 
						break;
					}
				}
				//之前没有已完成的，即是第一个完成
				if(!isFinished){
					updateResult = updateUndertakeInfo(tempUndertakeVO,uvo);//更新该undertake本身的信息
					updateResult = updateStageInfo(stage);//更新stage的状态为已完成，1的状态
					updateResult = updateTaskInfo(stage);//更新task的信息
				}
				else{
					updateResult = -2;//代表已经有人完成
				}
				//更新其它未完成的undertake状态为过期
				for(UndertakeVO undertake : undertakes ){
					//&&后面很重要！！不能覆盖当前的undertake
					if(undertake.getStatus() == UNDERTAKE_ONGOING && undertake.getId() != tempUndertakeVO.getId()){
						undertake.setStatus(UNDERTAKE_EXPIRED);
						undertakeDAO.update(undertake);
					}
				}
			} 
			//如果不是第一个策略，那么都要等到接受的人数满了才行
			else {
				//如果还没有足够worker，那么仅仅更新该undertake信息即可
				if(currentNum < workerNum)
					updateResult = updateUndertakeInfo(tempUndertakeVO,uvo);
				//如果已经有足够数量的worker，那么可能需要更新task的状态
				else{
					//如果存在未完成的undertake
					int isNotFinished = 0;
					for(UndertakeVO undertake : undertakes){
						if(undertake.getStatus() != UNDERTAKE_FINISHED)
							isNotFinished++; 
					}
					//除了当前worker还有其它worker没完成，只需更新当前undertake的状态
					if(isNotFinished > 1){
						updateResult = updateUndertakeInfo(tempUndertakeVO,uvo);//更新该undertake本身的信息
					}
					//只有当前worker没完成，则还需要更新task的状态
					else{
						updateResult = updateUndertakeInfo(tempUndertakeVO,uvo);//更新该undertake本身的信息
						updateResult = updateStageInfo(stage);//更新stage的状态为已完成，1的状态
						updateResult = updateTaskInfo(stage);//更新task的信息
					}
					
					//选大多数worker的答案
					if(aggregateMethod == 2){
						//TODO: 聚合结果并存储到stage的aggregateResult当中
					}
				}
			}
		
		}

		return updateResult;
	}

	private int updateStageInfo(StageVO stage) {
		stage.setStatus(StageVO.STAGE_FINISHED);
		int result = stageDAO.update(stage);
		if(result > 0)
			LOG.info(String.format("User [ %d ] complete the stage [ %d ] at the location [ longitude=%f, latitude=%f ] at [ %s ] successfully!", userId, stage.getId(),longitude, latitude, ServletUtils.getTime()));
		else
			LOG.info(String.format("User [ %d ] fail to complete the stage [ %d ] at [ %s ]: database error!", userId, stage.getId(),ServletUtils.getTime()));
		return result;
	}

	private int updateTaskInfo(StageVO stage) {
		//更新task的currentStage，或者更新task的状态为完成
		TaskVO task = tDAO.query(stage.getTaskId());
		int numOfStage = templateDAO.query(task.getTemplateId()).getTotalStageNum();
		if(numOfStage == stage.getIndex())
			task.setStatus(TaskVO.STATUS_FINISHED);
		else{
			task.setCurrentStage(stage.getIndex() + 1);
			
			//如果下一个stage的worker strategy是“same as the last stage's worker”，在用户点击完成任务后自动帮用户接下一这个任务的下一个阶段
			StageVO nextStage = stageDAO.queryByTaskAndIndex(task.getId(), stage.getIndex() + 1);
			long workerStrategy = nextStage.getRestrictions();
			//workerStragety：1是Same as the last stage's worker， 2是Different from all previous workers， 3是No requirement
			if(workerStrategy == 1){
				UndertakeVO autoUndertake = new UndertakeVO();
				autoUndertake.setUserId(userId);
				autoUndertake.setStageId(nextStage.getId());
				autoUndertake.setStatus(UNDERTAKE_ONGOING);
				//获得当前时间
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		        String timeStr = format.format(currentTime);
		        currentTime = Timestamp.valueOf(timeStr);
				autoUndertake.setStartTime(currentTime);
				TemplateVO tpvo = templateDAO.query(task.getTemplateId());
				/**
				 * 设定
				 */
				//如果不是最后一个stage，根据worker的当前位置动态计算contract
				if(tpvo.getTotalStageNum() != nextStage.getIndex()){
					Timestamp contract = nextStage.getDeadline();
					GetRecommendedTaskService service = new GetRecommendedTaskService(context);
					LocationDAO lDAO = new LocationDAO(context);
					LocationVO dest = lDAO.queryByStageAndType(nextStage.getId(), LocationVO.TYPE_DEST);
					if(dest != null){
						int time = service.calTime(longitude, latitude, dest);
						time = time + Constant.TIME_EXTRA_FOR_WORKER * 60;//额外预留时间，默认为10分钟
						long contractMillionSec = System.currentTimeMillis() + time * 1000;
						contract = new Timestamp(contractMillionSec);
		                timeStr = format.format(contract);
		                contract = Timestamp.valueOf(timeStr);
		                //因为额外预留了十分钟，可能导致契约时间超过ddl，这里做一个比较和选择
		                if(contract.getTime() > nextStage.getDeadline().getTime())
		                	contract = nextStage.getDeadline();
					}
					autoUndertake.setContractTime(contract);//设置契约时间，否则会过期
				}
				//否则直接设定为task的ddl
				else
					autoUndertake.setContractTime(task.getDeadline());
				
				
				int autoResult = undertakeDAO.insert(autoUndertake);
				if (autoResult > 0)
					LOG.info(String.format("System succeed to undertake the next stage [ %d ]for the worker [ %d ] automatically at [ %s ]!", nextStage.getId(),userId, ServletUtils.getTime()));
				else
					LOG.info(String.format("System fail to undertake the next stage [ %d ]for the worker [ %d ] automatically at [ %s ]!", nextStage.getId(),userId, ServletUtils.getTime()));				
			}
		}
		
		int updateTask = tDAO.update(task);
		if (updateTask > 0){
			if(numOfStage == stage.getIndex()){
				LOG.info(String.format("User [ %d ] complete the task [ %d ] at [ %s ] successfully!", userId, task.getId(),ServletUtils.getTime()));
				
				//APP端推送消息，通知requester任务已完成
				String alias = String.valueOf(task.getUserId());
				String taskTitle = task.getTitle();
				String content = "你发布的任务已完成！";
				UserVO worker = userDAO.query(userId);
				UndertakeVO undertakeVO = undertakeDAO.query(pk);
				String workerName = "";
				String finishTime = "";
				if(worker != null)
					workerName = worker.getAccount();
				if(undertakeVO != null)
					finishTime = ServletUtils.getTime(undertakeVO.getEndTime());
				
				Map<String,String> map = new HashMap<String,String>();
				map.put("type", JPushUtil.TYPE_OF_COMPLETETASK);
				map.put("taskTitle", taskTitle);
				map.put("workerName", workerName);
				map.put("finishTime", finishTime);
				
				JPushUtil.pushMessage(alias, content,map);
				
				//微信端推送消息
				WeixinUtil weixin = new WeixinUtil();
				Map<String,String> data = new HashMap<String, String>();
				
				UserVO requester = userDAO.query(task.getUserId());
				String weixinId = requester.getWeChatId();
				data.put("touser", weixinId);
				data.put("title","发布任务完成通知");
				String description = String.format("你发布的任务：[%s]已由用户%s于%s完成。请点击打开APP查看具体详细信息", 
						taskTitle,workerName,finishTime);
				LOG.info(description);
				data.put("description", description);
				data.put("url", "");
				weixin.sendMessage("textcard",data);
			}
			else
				LOG.info(String.format("The process of task [ %d ] has been updated at [ %s ] by worker [ %d ]!",task.getId(),ServletUtils.getTime(), userId));
		}
		else
			LOG.info(String.format("User [ %d ] fail to complete the task [ %d ] at [ %s ]: database error!", userId, task.getId(),ServletUtils.getTime()));
		
		return updateTask;
		
	}

	private int updateUndertakeInfo(UndertakeVO tempUndertakeVO,UndertakeVO uvo) {
		tempUndertakeVO.setEndTime(uvo.getEndTime());
		tempUndertakeVO.setStatus(UNDERTAKE_FINISHED);
		int updateResult = undertakeDAO.update(tempUndertakeVO);

		if (updateResult > 0){
			LOG.info("Update undertake info successfully.");
			//将积分计入user的withdrawCredit之中
//			UserVO userVO = userDAO.query(userId);
//			double reward = stageDAO.query(tempUndertakeVO.getStageId()).getReward();
//			userVO.setWithdrawCredit(userVO.getWithdrawCredit() + reward);
//			userDAO.update(userVO);
		}
		else
			LOG.info("Fail to update undertake info.");
		
		return updateResult;
		
	}

	public int getPrimaryKey() {
		return pk;
	}

}
