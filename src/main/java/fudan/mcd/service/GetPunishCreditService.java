package fudan.mcd.service;

import fudan.mcd.dao.impl.StageDAO;
import fudan.mcd.servlet.ServletUtils;
import fudan.mcd.vo.StageVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class GetPunishCreditService extends AbstractService {

	private static final Log LOG = LogFactory.getLog(GetPunishCreditService.class);
	
	public GetPunishCreditService(ServletContext context) {
		super(context);
	}
	
	public double getPunishCredit(int userId, int stageId) {

		LOG.info(String.format("User [ userId = %d ] click the abort button of stage [ stageId = %d ] at [ %s ].", userId, stageId, ServletUtils.getTime()));
		StageDAO sDAO = new StageDAO(context);
		
		StageVO stage = sDAO.query(stageId);
		double creditPunish = -1;
		if(stage != null)
			creditPunish = getPunish(stage);
		
		return creditPunish;
	}

	//根据stage的ddl确定惩罚积分
	private double getPunish(StageVO stage) {
		double punish;
		double reward = stage.getReward();
		Timestamp ddl = stage.getDeadline();
		
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		long milliseconds = ddl.getTime() - currentTime.getTime();
		int minutes = (int)(milliseconds / 60000);
		//一个小时以内，按照50%；三个小时以内，按照30%；三个小时以上，10%
		if(minutes < 60)
			punish = reward * 0.5;
		else if(minutes < 180)
			punish = reward * 0.3;
		else
			punish = reward * 0.1;
		
		//保留一位小数
		NumberFormat nf = new DecimalFormat( "0.0 "); 
		punish = Double.parseDouble(nf.format(punish));
		
		return punish;
	}

}
