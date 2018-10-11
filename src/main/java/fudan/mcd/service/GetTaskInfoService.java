package fudan.mcd.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fudan.mcd.dao.impl.LocationDAO;
import fudan.mcd.dao.impl.StageDAO;
import fudan.mcd.dao.impl.TaskDAO;
import fudan.mcd.dao.impl.TemplateDAO;
import fudan.mcd.runtime.Constant;
import fudan.mcd.servlet.ServletUtils;
import fudan.mcd.test.HttpPostProxy;
import fudan.mcd.vo.LocationVO;
import fudan.mcd.vo.StageVO;
import fudan.mcd.vo.TaskVO;
import fudan.mcd.vo.TemplateVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GetTaskInfoService extends AbstractService {
	public static final int LOCATION_SRC = 0, LOCATION_DEST = 1;
	private static final Log LOG = LogFactory.getLog(GetTaskInfoService.class);

	public GetTaskInfoService(ServletContext context) {
		super(context);
	}

	public ResultVO getTaskInfo(int taskId,double longitude,double latitude) {
		LOG.info(String.format("The detail information of task [ %d ] has been requested at [ %s ].", taskId, ServletUtils.getTime()));
		ResultVO rvo = new ResultVO();
		// The DAO of Task, Template, Stage and Location
		TaskDAO tDAO = new TaskDAO(context);
		TemplateDAO tpDAO = new TemplateDAO(context);
		StageDAO sDAO = new StageDAO(context);

		// Get the object using the corresponding DAO
		TaskVO tvo = tDAO.query(taskId);
		if (tvo == null) {
			return null;
		}

		TemplateVO tpvo = tpDAO.query(tvo.getTemplateId());
		List<StageVO> svoList = sDAO.queryStageListByTask(taskId);

		// Generate the stage with location of src and dest
		List<GetStageInfoService.ResultVO> stageInfoList = new ArrayList<GetStageInfoService.ResultVO>();
		GetStageInfoService service = new GetStageInfoService(context);
		for (StageVO svo : svoList) {
			//以mode 0的模式查看stage信息：即只查看需要哪些output
			GetStageInfoService.ResultVO stageInfo = service.getStageInfo(svo.getId(),-1,0);
			stageInfoList.add(stageInfo);
		}
		//Calculate the contract time of current stage
		StageVO stage = sDAO.queryByTaskAndIndex(tvo.getId(), tvo.getCurrentStage());
		if (stage != null) {
			LocationDAO lDao = new LocationDAO(context);
			LocationVO currentDest = lDao.queryByStageAndType(stage.getId(), LOCATION_DEST);
			//利用百度地图API，计算是否能在ddl之前到达目的地
			int time = calTime(longitude,latitude,currentDest);

			//计算contract:如果stage数量大于1，且不是最后一个stage，动态计算
            if(tpvo.getTotalStageNum() > 1 && stage.getIndex() != tpvo.getTotalStageNum()){
				time = time + Constant.TIME_EXTRA_FOR_WORKER * 60;//额外预留时间，默认为10分钟
				long contractMillionSec = System.currentTimeMillis() + time * 1000;
				Timestamp contract = new Timestamp(contractMillionSec);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String timeStr = format.format(contract);
                contract = Timestamp.valueOf(timeStr);
                //因为额外预留了十分钟，可能导致契约时间超过ddl，这里做一个比较和选择
                if(contract.getTime() > stage.getDeadline().getTime())
                	contract = stage.getDeadline();
                rvo.setContractTime(contract);
			}
			//单个stage的任务，直接设定为ddl
			else
                rvo.setContractTime(tvo.getDeadline());
		}
		rvo.setTvo(tvo);
		rvo.setTpvo(tpvo);
		rvo.setStageInfoList(stageInfoList);
		return rvo;
	}
	private int calTime(double longitude, double latitude,LocationVO currentDest){
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

	public static class ResultVO {
		private TaskVO tvo;
		private TemplateVO tpvo;
		private List<GetStageInfoService.ResultVO> stageInfoList;
		private Timestamp contractTime;

		public TaskVO getTvo() {
			return tvo;
		}

		public void setTvo(TaskVO tvo) {
			this.tvo = tvo;
		}

		public TemplateVO getTpvo() {
			return tpvo;
		}

		public void setTpvo(TemplateVO tpvo) {
			this.tpvo = tpvo;
		}

		public List<GetStageInfoService.ResultVO> getStageInfoList() {
			return stageInfoList;
		}

		public void setStageInfoList(List<GetStageInfoService.ResultVO> stageInfoList) {
			this.stageInfoList = stageInfoList;
		}

		public Timestamp getContractTime() {
			return contractTime;
		}

		public void setContractTime(Timestamp contractTime) {
			this.contractTime = contractTime;
		}

	}
}
