package fudan.mcd.dao.impl;

import fudan.mcd.dao.abs.AbstractStageDAO;
import fudan.mcd.vo.StageVO;

import javax.servlet.ServletContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StageDAO extends AbstractStageDAO {
	public static final String TABLE_STAGE = "stage";
	public static final String FIELD_ID = "stageId";
	public static final String FIELD_TASK_ID = "taskId";
	public static final String FIELD_NAME = "stageName";
	public static final String FIELD_DESCRIPTION = "stageDesc";
	public static final String FIELD_DEADLINE = "deadline";
	public static final String FIELD_REWARD = "reward";
	public static final String FIELD_INDEX = "stageIndex";
	public static final String FIELD_WORKER_NUM = "workerNum";
	public static final String FIELD_AGGREGATE_METHOD = "aggregateMethod";
	public static final String FIELD_AGGREGATE_RESULT = "aggregateResult";
	public static final String FIELD_RESTRICTIONS = "restrictions";
	public static final String FIELD_DURATION = "stageDuration";
	public static final String FIELD_CONTRACT = "stageContract";
	public static final String FIELD_STATUS = "stageStatus";

	public StageDAO(ServletContext context) {
		super(context);
	}

	public StageDAO(String configPath) {
		super(configPath);
	}

	@Override
	public Integer insert(StageVO vo) {
		String sql = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", TABLE_STAGE,
				FIELD_TASK_ID, FIELD_NAME, FIELD_DESCRIPTION, FIELD_DEADLINE, FIELD_REWARD, FIELD_INDEX, FIELD_WORKER_NUM, FIELD_AGGREGATE_METHOD,
				FIELD_AGGREGATE_RESULT, FIELD_RESTRICTIONS, FIELD_DURATION, FIELD_CONTRACT, FIELD_STATUS);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, vo.getTaskId());
			ps.setString(2, vo.getName());
			ps.setString(3, vo.getDescription());
			ps.setTimestamp(4, vo.getDeadline());
			ps.setDouble(5, vo.getReward());
			ps.setInt(6, vo.getIndex());
			ps.setInt(7, vo.getWorkerNum());
			ps.setInt(8, vo.getAggregateMethod());
			ps.setString(9, vo.getAggregateResult());
			ps.setLong(10, vo.getRestrictions());
			ps.setDouble(11, vo.getDuration());
			ps.setTimestamp(12, vo.getContract());
			ps.setInt(13, vo.getStatus());
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next())
				return rs.getInt(1);
			else
				return -1;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
		finally {
			try {
				if (ps != null)
					ps.close();
				if (connection != null)
					connection.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public StageVO delete(Integer pk) {
		StageVO vo = query(pk);
		if (vo == null)
			return null;
		String sql = String.format("DELETE FROM %s WHERE %s = ?", TABLE_STAGE, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, pk);
			ps.executeUpdate();
			return vo;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		finally {
			try {
				if (ps != null)
					ps.close();
				if (connection != null)
					connection.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int update(StageVO vo) {
		if (query(vo.getId()) == null)
			return -1;
		String sql = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?", TABLE_STAGE,
				FIELD_NAME, FIELD_DESCRIPTION, FIELD_DEADLINE, FIELD_REWARD, FIELD_INDEX, FIELD_WORKER_NUM, FIELD_AGGREGATE_METHOD,
				FIELD_AGGREGATE_RESULT, FIELD_RESTRICTIONS, FIELD_DURATION, FIELD_CONTRACT, FIELD_STATUS, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setString(1, vo.getName());
			ps.setString(2, vo.getDescription());
			ps.setTimestamp(3, vo.getDeadline());
			ps.setDouble(4, vo.getReward());
			ps.setInt(5, vo.getIndex());
			ps.setInt(6, vo.getWorkerNum());
			ps.setInt(7, vo.getAggregateMethod());
			ps.setString(8, vo.getAggregateResult());
			ps.setLong(9, vo.getRestrictions());
			ps.setDouble(10, vo.getDuration());
			ps.setTimestamp(11, vo.getContract());
			ps.setInt(12, vo.getStatus());
			ps.setInt(13, vo.getId());
			ps.executeUpdate();
			return 1;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
		finally {
			try {
				if (ps != null)
					ps.close();
				if (connection != null)
					connection.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public StageVO query(Integer pk) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_STAGE, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, pk);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				StageVO vo = loadFromResultSet(rs);
				return vo;
			}
			return null;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		finally {
			try {
				if (ps != null)
					ps.close();
				if (connection != null)
					connection.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public List<StageVO> queryStageListByTask(int taskId) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_STAGE, FIELD_TASK_ID);
		List<StageVO> list = new ArrayList<StageVO>();
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, taskId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				StageVO vo = loadFromResultSet(rs);
				list.add(vo);
			}
			return list;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return list;
		}
		finally {
			try {
				if (ps != null)
					ps.close();
				if (connection != null)
					connection.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public StageVO queryByTaskAndIndex(int taskId, int stageIndex) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?", TABLE_STAGE, FIELD_TASK_ID, FIELD_INDEX);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, taskId);
			ps.setInt(2, stageIndex);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				StageVO vo = loadFromResultSet(rs);
				return vo;
			}
			return null;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		finally {
			try {
				if (ps != null)
					ps.close();
				if (connection != null)
					connection.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private StageVO loadFromResultSet(ResultSet rs) {
		StageVO vo = new StageVO();
		try {
			vo.setId(rs.getInt(FIELD_ID));
			vo.setTaskId(rs.getInt(FIELD_TASK_ID));
			vo.setName(rs.getString(FIELD_NAME));
			vo.setDescription(rs.getString(FIELD_DESCRIPTION));
			vo.setDeadline(rs.getTimestamp(FIELD_DEADLINE));
			vo.setReward(rs.getDouble(FIELD_REWARD));
			vo.setIndex(rs.getInt(FIELD_INDEX));
			vo.setWorkerNum(rs.getInt(FIELD_WORKER_NUM));
			vo.setAggregateMethod(rs.getInt(FIELD_AGGREGATE_METHOD));
			vo.setAggregateResult(rs.getString(FIELD_AGGREGATE_RESULT));
			vo.setRestrictions(rs.getLong(FIELD_RESTRICTIONS));
			vo.setDuration(rs.getDouble(FIELD_DURATION));
			vo.setContract(rs.getTimestamp(FIELD_CONTRACT));
			vo.setStatus(rs.getInt(FIELD_STATUS));
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return vo;
	}
}
