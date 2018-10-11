package fudan.mcd.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fudan.mcd.dao.impl.*;
import fudan.mcd.runtime.Constant;
import fudan.mcd.servlet.ServletUtils;
import fudan.mcd.test.HttpPostProxy;
import fudan.mcd.vo.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GetRecommendedTaskService extends AbstractService {
	private static final int TASK_ONGOING = TaskVO.STATUS_ONGOING;
	private static final int LOCATION_DEST = LocationVO.TYPE_DEST;
	private static final Log LOG = LogFactory.getLog(GetRecommendedTaskService.class);

	public GetRecommendedTaskService(ServletContext context) {
		super(context);
	}

	public List<ResultVO> getRecommendedTask(int userId,double longitude,double latitude) {
		LOG.info(String.format("User [ %d ] request the recommended task list at [ %s ] in the location [ longitude=%f, latitude=%f ].", userId, ServletUtils.getTime(),longitude, latitude));
		List<ResultVO> result = new ArrayList<ResultVO>();
		// The DAO of Task, Template, User and Location
		TaskDAO dao = new TaskDAO(context);
		TemplateDAO tpDao = new TemplateDAO(context);
		LocationDAO lDao = new LocationDAO(context);
		StageDAO sDAO = new StageDAO(context);
		UserDAO uDAO = new UserDAO(context);
		UndertakeDAO undertakeDAO = new UndertakeDAO(context);
		ApplicationDAO applicationDAO = new ApplicationDAO(context);
		// Data distributed in Task(all information of Task)，Template(stageNum) and Location(longitude and latitude)
		List<TaskVO> tasks = dao.queryAllTaskList();
		LOG.info(String.format("The total number of task at [ %s ] is [ %d ].", ServletUtils.getTime(),tasks.size()));
		
		/**
		 * 根据user类型返回对应类型的任务
		 */
		UserVO looker = uDAO.query(userId);
		//默认不需要有限制，返回所有符合条件的任务集合
		boolean needConstraint = false;
		//如果是普通用户，需要限制：只能返回userType为0的任务集合
		if(looker.getTag() == UserVO.USER_NORMAL){
			needConstraint = true;
//			LOG.info(String.format("The needConstraint value change to true"));
		}
		for (TaskVO vo : tasks) {
			if (vo.getUserId() != userId && vo.getStatus() == TASK_ONGOING) {
				StageVO stage = sDAO.queryByTaskAndIndex(vo.getId(), vo.getCurrentStage());
				if (stage != null) {
					UndertakeVO undervo = undertakeDAO.queryByUserAndStage(userId,stage.getId());
					ApplicationVO avo = applicationDAO.queryByUserAndStage(userId,stage.getId());
					if(undervo == null && avo == null && isIdle(stage)){
						ResultVO rvo = new ResultVO();
						UserVO uvo = uDAO.query(vo.getUserId()); 
						//int stageId = sDAO.queryByTaskAndIndex(vo.getId(), vo.getCurrentStage()).getId(); // Modified
						LocationVO currentDest = lDao.queryByStageAndType(stage.getId(), LOCATION_DEST);
						//利用百度地图API，计算是否能在ddl之前到达目的地
						int time = calTime(longitude,latitude,currentDest);
//						LOG.info(String.format("The needed time is %s",time));
						boolean isWithin = compareTime(time,stage.getDeadline());
						if(isWithin){
							rvo.setTvo(vo);
							rvo.setUvo(uvo);
							TemplateVO tempVO = tpDao.query(vo.getTemplateId());
							rvo.setTpvo(tempVO);
							rvo.setSvo(stage);
							rvo.setLvo(currentDest);
							if(tempVO.getTotalStageNum() > 1){
								//计算contract
								time = time + Constant.TIME_EXTRA_FOR_WORKER * 60;//额外预留时间，默认为10分钟
								long contractMillionSec = System.currentTimeMillis() + time * 1000;
								Timestamp contract = new Timestamp(contractMillionSec);
								SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				                String timeStr = format.format(contract);
				                contract = Timestamp.valueOf(timeStr);
				                //因为额外预留了十分钟，可能导致契约时间超过ddl，这里做一个比较和选择
				                if(contract.getTime() > stage.getDeadline().getTime())
				                	contract = stage.getDeadline();
				                rvo.setContract(contract);
							}
							//单个stage的任务，直接设定为ddl
							else
				                rvo.setContract(stage.getDeadline());
							
//							LOG.info(String.format("The contract time is %s",contract));
//							
//
//							LOG.info(String.format("The needConstraint value is %s", needConstraint));
//							LOG.info(String.format("The user scope of this task is %d",vo.getUserType()));
							//VIP用户，返回所有符合条件任务集合
							if(!needConstraint)	
								result.add(rvo);
							//普通用户，只返回userType为0的任务集合
							else if(needConstraint && vo.getUserType()== UserVO.USER_NORMAL)
								result.add(rvo);
						}
					}
				}
			}
		}

		LOG.info(String.format("The number of recommended task to the worker [ %d ] at [ %s ] is [ %d ].", userId, ServletUtils.getTime(),result.size()));
		return result;
	}

	public int calTime(double longitude, double latitude,LocationVO currentDest){
		//http请求，调用百度地图API获得时间预估
		String result = HttpPostProxy.doGet(latitude, longitude, currentDest.getLatitude(), currentDest.getLongitude());
		
		//解析返回结果，获得预估时间
		JSONObject resultObj = JSON.parseObject(result);
		int time = -1;
		if(resultObj.getIntValue("status") == 0){
			JSONArray resultArray = resultObj.getJSONArray("result");
			resultObj = resultArray.getJSONObject(0);
			String tmp = resultObj.getString("duration");
			resultObj = JSON.parseObject(tmp);
			time = resultObj.getIntValue("value");
		}
		
		return time;
	}
	
	private boolean compareTime(int time,Timestamp ddl) {
		boolean isWithin = true;	
		LOG.info(String.format("The stage ddl is %s", ddl));
		
		//进行时间比较，确定是否来得及
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		long milliseconds = ddl.getTime() - currentTime.getTime();
		int seconds = (int)milliseconds / 1000;
		
		//如果ddl与当前时间的时间差小于所需的时间预估，返回false
		if(seconds < time)
			isWithin = false;
		
		return isWithin;
	}

	private boolean isIdle(StageVO stage) {
		boolean isIdle = true;
		UndertakeDAO undertakeDAO = new UndertakeDAO(context);
		int workerNum = stage.getWorkerNum();//需要的workernumber
		List<UndertakeVO> unders = undertakeDAO.queryByStage(stage.getId());
		int currentNum = 0;
		for(UndertakeVO under:unders){
			if(under.getStatus() != -1){
				currentNum++;
			}
		}
		if(currentNum == workerNum)
			isIdle = false;
		
		return isIdle;
	}

	public static class ResultVO {
		private TaskVO tvo;
		private UserVO uvo;
		private TemplateVO tpvo;
		private StageVO svo;
		private LocationVO lvo;
		private Timestamp contract;

		public TaskVO getTvo() {
			return tvo;
		}

		public void setTvo(TaskVO tvo) {
			this.tvo = tvo;
		}

		public UserVO getUvo() {
			return uvo;
		}

		public void setUvo(UserVO uvo) {
			this.uvo = uvo;
		}

		public TemplateVO getTpvo() {
			return tpvo;
		}

		public void setTpvo(TemplateVO tpvo) {
			this.tpvo = tpvo;
		}

		public StageVO getSvo() {
			return svo;
		}

		public void setSvo(StageVO svo) {
			this.svo = svo;
		}

		public LocationVO getLvo() {
			return lvo;
		}

		public void setLvo(LocationVO lvo) {
			this.lvo = lvo;
		}

		public Timestamp getContract() {
			return contract;
		}

		public void setContract(Timestamp contract) {
			this.contract = contract;
		}

	}

}
