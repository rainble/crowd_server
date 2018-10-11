package fudan.mcd.test;

import fudan.mcd.vo.*;

import java.sql.Timestamp;

import static org.junit.Assert.assertEquals;

public class JUnitTestUtils {
	public static final double FLOAT_ERROR = 0.01;
	public static final int INVALID_PK = 999999;
	public static final String INVALID_FIELD_STRING = "invalid_field_string";

	public static void assertTimestampEquals(Timestamp expected, Timestamp actual) {
		assertEquals(expected.getTime(), actual.getTime(), 2000);
	}

	public static String generateTestString(String str) {
		return "jtest_" + str + "_" + System.currentTimeMillis();
	}

	public static UserVO generateUserVO() {
		UserVO vo = new UserVO();
		vo.setAccount(generateTestString("account"));
		vo.setPassword(generateTestString("password"));
		vo.setPublishCredit(Math.random() * 10000);
		vo.setWithdrawCredit(Math.random() * 10000);
		vo.setAvatar(generateTestString("avatar"));
		vo.setTag((int) (1 + Math.random() * 10));
		vo.setLoginFlag((int) (1 + Math.random() * 10));
		vo.setWeChatId(generateTestString("avatar"));
		vo.setPhoneNum(String.valueOf((int)(Math.random() * 1000000000)));
		return vo;
	}

	public static TemplateVO generateTemplateVO(int userId) {
		TemplateVO vo = new TemplateVO();
		vo.setUserId(userId);
		vo.setName(generateTestString("name"));
		vo.setUri(generateTestString("uri"));
		vo.setCreateTime(new Timestamp(System.currentTimeMillis()));
		vo.setTotalStageNum((int) (1 + Math.random() * 10));
		vo.setHeat((int) (1 + Math.random() * 10000));
		vo.setDescription(generateTestString("description"));
		return vo;
	}

	public static TaskVO generateTaskVO(int templateId, int userId) {
		int[] statusEnum = new int[] { TaskVO.STATUS_EXPIRED, TaskVO.STATUS_FINISHED, TaskVO.STATUS_ONGOING };

		TaskVO vo = new TaskVO();
		vo.setTemplateId(templateId);
		vo.setUserId(userId);
		vo.setTitle(generateTestString("title"));
		vo.setDescription(generateTestString("description"));
		vo.setStatus(statusEnum[(int) (Math.random() * statusEnum.length)]);
		vo.setCurrentStage((int) (1 + Math.random() * 10));
		vo.setBonusReward(Math.random() * 10000);
		vo.setPublishTime(new Timestamp(System.currentTimeMillis()));
		vo.setDeadline(new Timestamp(System.currentTimeMillis()));
		vo.setUserType((int) (1 + Math.random() * 10));
		return vo;
	}

	public static StageVO generateStageVO(int taskId) {
		StageVO vo = new StageVO();
		vo.setTaskId(taskId);
		vo.setName(generateTestString("name"));
		vo.setDescription(generateTestString("description"));
		vo.setDeadline(new Timestamp(System.currentTimeMillis()));
		vo.setReward(Math.random() * 10000);
		vo.setIndex((int) (1 + Math.random() * 10));
		vo.setWorkerNum((int) (1 + Math.random() * 10));
		vo.setAggregateMethod((int) (1 + Math.random() * 10));
		vo.setAggregateResult(generateTestString("aggregate_result"));
		vo.setDuration(Math.random() * 10000);
		vo.setContract(new Timestamp(System.currentTimeMillis()));
		vo.setStatus((int) (1 + Math.random() * 10));
		return vo;
	}

	public static LocationVO generateLocationVO(int stageId) {
		int[] typeEnum = new int[] { LocationVO.TYPE_NOTNEED, LocationVO.TYPE_SRC, LocationVO.TYPE_DEST };

		LocationVO vo = new LocationVO();
		vo.setStageId(stageId);
		vo.setAddress(generateTestString("address"));
		vo.setLatitude(-90 + Math.random() * 180);
		vo.setLongitude(-180 + Math.random() * 360);
		vo.setType(typeEnum[(int) (Math.random() * typeEnum.length)]);
		return vo;
	}

	public static ActionVO generateActionVO(int locationId) {
		ActionVO vo = new ActionVO();
		vo.setLocationId(locationId);
		vo.setDuration(Math.random() * 10000);
		vo.setType((int) (1 + Math.random() * 10));
		return vo;
	}

	public static InputVO generateInputVO(int actionId) {
		InputVO vo = new InputVO();
		vo.setActionId(actionId);
		vo.setType((int) (1 + Math.random() * 10));
		vo.setValue(generateTestString("value"));
		vo.setDesc(generateTestString("description"));
		return vo;
	}

	public static CollectionVO generateCollectionVO(int userId, int templateId) {
		CollectionVO vo = new CollectionVO();
		vo.setUserId(userId);
		vo.setTemplateId(templateId);
		return vo;
	}

	public static UndertakeVO generateUndertakeVO(int userId, int stageId) {
		int[] statusEnum = new int[] { TaskVO.STATUS_EXPIRED, TaskVO.STATUS_FINISHED, TaskVO.STATUS_ONGOING };

		UndertakeVO vo = new UndertakeVO();
		vo.setUserId(userId);
		vo.setStageId(stageId);
		vo.setStartTime(new Timestamp(System.currentTimeMillis()));
		vo.setEndTime(new Timestamp(System.currentTimeMillis()));
		vo.setContractTime(new Timestamp(System.currentTimeMillis()));
		vo.setStatus(statusEnum[(int) (Math.random() * statusEnum.length)]);
		return vo;
	}

	public static EnumOutputVO generateEnumOutputVO(int actionId, int workerId) {
		EnumOutputVO vo = new EnumOutputVO();
		vo.setActionId(actionId);
		vo.setValue(generateTestString("value"));
		vo.setDesc(generateTestString("description"));
		vo.setWorkerId(workerId);
		vo.setIndicator((int) (1 + Math.random() * 10));
		vo.setEntries(generateTestString("entries"));
		vo.setAggregationMethod((int) (1 + Math.random() * 10));
		return vo;
	}

	public static NumericalOutputVO generateNumericalOutputVO(int actionId, int workerId) {
		NumericalOutputVO vo = new NumericalOutputVO();
		vo.setActionId(actionId);
		vo.setValue(generateTestString("value"));
		vo.setDesc(generateTestString("description"));
		vo.setWorkerId(workerId);
		vo.setIndicator((int) (1 + Math.random() * 10));
		vo.setInterval((int) (1 + Math.random() * 10));
		vo.setLowerBound(Math.random() * 10000);
		vo.setUpperBound(Math.random() * 10000);
		vo.setAggregationMethod((int) (1 + Math.random() * 10));
		return vo;
	}

	public static PictureOutputVO generatePictureOutputVO(int actionId, int workerId) {
		PictureOutputVO vo = new PictureOutputVO();
		vo.setActionId(actionId);
		vo.setValue(generateTestString("value"));
		vo.setDesc(generateTestString("description"));
		vo.setWorkerId(workerId);
		vo.setIndicator((int) (1 + Math.random() * 10));
		return vo;
	}

	public static TextOutputVO generateTextOutputVO(int actionId, int workerId) {
		TextOutputVO vo = new TextOutputVO();
		vo.setActionId(actionId);
		vo.setValue(generateTestString("value"));
		vo.setDesc(generateTestString("description"));
		vo.setWorkerId(workerId);
		vo.setIndicator((int) (1 + Math.random() * 10));
		return vo;
	}
	
	public static ReserveVO generateReserveVO(int userId, int previousUserId, int stageId, int previousStageId) {
		ReserveVO vo = new ReserveVO();
		vo.setUserId(userId);
		vo.setStageId(stageId);
		vo.setReserveTime(new Timestamp(System.currentTimeMillis()));
		vo.setPreviousUserId(previousUserId);
		vo.setPreviousStageId(previousStageId);
		vo.setStatus((int) (1 + Math.random() * 10));
		vo.setContract(new Timestamp(System.currentTimeMillis()));
		return vo;
	}
	
	public static ApplicationVO generateApplicationVO(int userId, int stageId) {
		ApplicationVO vo = new ApplicationVO();
		vo.setUserId(userId);
		vo.setStageId(stageId);
		return vo;
	}
}
