package fudan.mcd.vo;

import java.sql.Timestamp;

public class StageVO {
	public static final int STAGE_EXPIRED = -1, STAGE_ONGOING = 0, STAGE_FINISHED = 1, STAGE_IDLE = 2;
	private int id;
	private int taskId;
	private String name;
	private String description;
	private Timestamp deadline;
	private double reward;
	private int index;
	private int workerNum;
	private int aggregateMethod;
	private String aggregateResult;
	private long restrictions;
	private double duration;
	private Timestamp contract;
	private int status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getDeadline() {
		return deadline;
	}

	public void setDeadline(Timestamp deadline) {
		this.deadline = deadline;
	}

	public double getReward() {
		return reward;
	}

	public void setReward(double reward) {
		this.reward = reward;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getWorkerNum() {
		return workerNum;
	}

	public void setWorkerNum(int workerNum) {
		this.workerNum = workerNum;
	}

	public int getAggregateMethod() {
		return aggregateMethod;
	}

	public void setAggregateMethod(int aggregateMethod) {
		this.aggregateMethod = aggregateMethod;
	}

	public String getAggregateResult() {
		return aggregateResult;
	}

	public void setAggregateResult(String aggregateResult) {
		this.aggregateResult = aggregateResult;
	}

	public long getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(long restrictions) {
		this.restrictions = restrictions;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public Timestamp getContract() {
		return contract;
	}

	public void setContract(Timestamp contract) {
		this.contract = contract;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
