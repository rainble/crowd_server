package fudan.mcd.vo;

import java.sql.Timestamp;

public class UndertakeVO {
	public static final int STATUS_ONGOING = 0, STATUS_EXPIRED = -1, STATUS_FINISHED = 1,  STATUS_CREDIT_GIVEN = 5;
	private int id;
	private int userId;
	private int stageId;
	private Timestamp startTime;
	private Timestamp endTime;
	private Timestamp contractTime;
	private int status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Timestamp getContractTime() {
		return contractTime;
	}

	public void setContractTime(Timestamp contractTime) {
		this.contractTime = contractTime;
	}
}
