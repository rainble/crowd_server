package fudan.mcd.vo;

import java.sql.Timestamp;

public class ReserveVO {
	private int id;
	private int userId;
	private int stageId;
	private Timestamp reserveTime;
	private int previousUserId;
	private int previousStageId;
	private int status;
	private Timestamp contract;

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

	public Timestamp getReserveTime() {
		return reserveTime;
	}

	public void setReserveTime(Timestamp reserveTime) {
		this.reserveTime = reserveTime;
	}

	public int getPreviousUserId() {
		return previousUserId;
	}

	public void setPreviousUserId(int previousUserId) {
		this.previousUserId = previousUserId;
	}

	public int getPreviousStageId() {
		return previousStageId;
	}

	public void setPreviousStageId(int previousStageId) {
		this.previousStageId = previousStageId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Timestamp getContract() {
		return contract;
	}

	public void setContract(Timestamp contract) {
		this.contract = contract;
	}
}
