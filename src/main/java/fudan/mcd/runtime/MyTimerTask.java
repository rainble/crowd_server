package fudan.mcd.runtime;

import fudan.mcd.service.AcceptTaskService;
import fudan.mcd.servlet.AcceptTaskServlet;
import fudan.mcd.servlet.AcceptTaskServlet.RequestBO;
import fudan.mcd.servlet.ServletUtils;
import fudan.mcd.vo.StageVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.util.*;

public class MyTimerTask extends TimerTask {

	private int stageId;
	private AcceptTaskService service;
	private static final Log LOG = LogFactory.getLog(TimerTask.class);
	
	public MyTimerTask(int stageId,AcceptTaskService service) {
		super();
		this.stageId = stageId;
		this.service = service;
	}

	@Override
	public void run() {
		AcceptStructure acceptStructure = AcceptTaskServlet.map.get(stageId);
		acceptStructure.setFlag(false);
		
		List<RequestBO> requests = acceptStructure.getRequests();
		LOG.info(String.format("There are totally [ %d ] workers apply for the stage [ %d ] at [ %s ]", requests.size(), stageId, ServletUtils.getTime()));	
		List<RequestBO> targets = new ArrayList<RequestBO>();
		
		//由于微信端的关系，所以需要先确认当前结束等待的时候，是否任务所需人数已满
		if(service.isNeedMoreWorker(stageId)){	
			
			//根据该stage需要的worker数量进行worker选择
			int leftWorkerNum = service.getLeftWorkerNumber(stageId);
			//需要大于请求，全部成功
			if(leftWorkerNum >= requests.size())
				targets = requests;
			//请求大于需要，进行选择:时间最近的
			else{
				//按契约时间进行排序
				Collections.sort(requests, new Comparator<RequestBO>(){

					@Override
					public int compare(RequestBO r1, RequestBO r2) {
						Timestamp t1 = r1.getContractTime();
						Timestamp t2 = r2.getContractTime();
						if(t1.getTime() > t2.getTime())
							return 1;
						if(t1.getTime() == t2.getTime())
							return 0;
						return -1;
					}			
				});
				//根据leftWorkerNum进行添加
				for(int i = 0; i < leftWorkerNum; i++){
					targets.add(requests.get(i));
				}
			}
		}	
		
		int result;
		if(targets.size() > 0)
			result = service.acceptStage(stageId,targets);
		else
			result = 2;//用于标记任务所需人数已满
		
		//针对接任务成功的人返回的字符串消息
		String targetString = "";
		if(result == 1)
			targetString = "你已成功接受该任务，请立即出发完成任务！";
		else if(result == -1)
			targetString = "接任务失败，请稍候再尝试";
		else
			targetString = "该任务所需人数已满，请换一个任务";
		//获取stage和task的相关信息
		StageVO stage = service.getStageInfo(stageId);
		String taskTitle = service.getTaskTitle(stageId);
		Map<String,String> map = new HashMap<String,String>();
		map.put("type", JPushUtil.TYPE_OF_ACCEPTTASK);
		map.put("taskTitle", taskTitle);
		map.put("stageDesc", stage.getDescription());
		//进行消息推送
		for(RequestBO request : requests){
			//接任务成功的用户
			if(isInTargets(request.getUserId(),targets)){
				LOG.info(String.format("User [ %d ] accept the stage [ %d ] at [ %s ] successfully!", request.getUserId(), stageId, ServletUtils.getTime()));
				String alias = String.valueOf(request.getUserId());
				String content = targetString;
				map.put("result", "1");
				map.put("contractTime", request.getContractTime().toString());
				JPushUtil.pushMessage(alias, content,map);
			}
			//接任务失败
			else{
				LOG.info(String.format("User [ %d ] accept the stage [ %d ] at [ %s ] fail!", request.getUserId(), stageId, ServletUtils.getTime()));
				String alias = String.valueOf(request.getUserId());
				String content = "接任务失败，请换一个任务尝试！";
				map.put("result", "-1");
				map.put("contractTime", request.getContractTime().toString());
				JPushUtil.pushMessage(alias, content,map);			
			}
			//从application表格当中删除
			service.deleteApplication(request.getUserId(),stageId);
		}
	}

	//返回该用户是否申请成功
	private boolean isInTargets(int userId,List<RequestBO> targets) {
		boolean result = false;
		for(RequestBO target : targets){
			if(userId == target.getUserId()){
				result = true;
				break;
			}
		}
		return result;
	}

}
