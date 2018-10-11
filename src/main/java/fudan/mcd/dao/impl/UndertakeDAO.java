package fudan.mcd.dao.impl;

import fudan.mcd.dao.abs.AbstractUndertakeDAO;
import fudan.mcd.vo.StageVO;
import fudan.mcd.vo.UndertakeVO;

import javax.servlet.ServletContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UndertakeDAO extends AbstractUndertakeDAO {
	public static final String TABLE_UNDERTAKE = "undertake";
	public static final String FIELD_ID = "undertakeId";
	public static final String FIELD_USER_ID = "userId";
	public static final String FIELD_STAGE_ID = "stageId";
	public static final String FIELD_START_TIME = "start_time";
	public static final String FIELD_END_TIME = "end_time";
	public static final String FIELD_STATUS = "status";
	public static final String FIELD_CONTRACT_TIME = "contract_time";

	public UndertakeDAO(ServletContext context) {
		super(context);
	}

	public UndertakeDAO(String configPath) {
		super(configPath);
	}

	@Override
	public Integer insert(UndertakeVO vo) {
		String sql = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) values(?, ?, ?, ?, ?, ?)", TABLE_UNDERTAKE, FIELD_USER_ID, FIELD_STAGE_ID,
				FIELD_START_TIME, FIELD_END_TIME, FIELD_STATUS, FIELD_CONTRACT_TIME);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, vo.getUserId());
			ps.setInt(2, vo.getStageId());
			ps.setTimestamp(3, vo.getStartTime());
			ps.setTimestamp(4, vo.getEndTime());
			ps.setInt(5, vo.getStatus());
			ps.setTimestamp(6, vo.getContractTime());
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
	public UndertakeVO delete(Integer pk) {
		UndertakeVO vo = query(pk);
		if (vo == null)
			return null;
		String sql = String.format("DELETE FROM %s WHERE %s = ?", TABLE_UNDERTAKE, FIELD_ID);
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
	public int update(UndertakeVO vo) {
		if (query(vo.getId()) == null)
			return -1;
		String sql = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?", TABLE_UNDERTAKE, FIELD_USER_ID,
				FIELD_STAGE_ID, FIELD_START_TIME, FIELD_END_TIME, FIELD_STATUS, FIELD_CONTRACT_TIME, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, vo.getUserId());
			ps.setInt(2, vo.getStageId());
			ps.setTimestamp(3, vo.getStartTime());
			ps.setTimestamp(4, vo.getEndTime());
			ps.setInt(5, vo.getStatus());
			ps.setTimestamp(6, vo.getContractTime());
			ps.setInt(7, vo.getId());
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
	public UndertakeVO query(Integer pk) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_UNDERTAKE, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, pk);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				UndertakeVO vo = loadFromResultSet(rs);
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
	public List<StageVO> queryStageListByUserAndStatus(int userId, int status) {
		String sql = String.format("SELECT * FROM %s, %s WHERE %s.%s = %s.%s AND %s.%s = ? AND %s.%s = ?", TABLE_UNDERTAKE, StageDAO.TABLE_STAGE,
				TABLE_UNDERTAKE, FIELD_STAGE_ID, StageDAO.TABLE_STAGE, StageDAO.FIELD_ID, TABLE_UNDERTAKE, FIELD_USER_ID, TABLE_UNDERTAKE,
				FIELD_STATUS);
		List<StageVO> list = new ArrayList<StageVO>();
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, userId);
			ps.setInt(2, status);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				StageVO vo = new StageVO();
				vo.setId(rs.getInt(String.format("%s.%s", StageDAO.TABLE_STAGE, StageDAO.FIELD_ID)));
				vo.setTaskId(rs.getInt(String.format("%s.%s", StageDAO.TABLE_STAGE, StageDAO.FIELD_TASK_ID)));
				vo.setName(rs.getString(String.format("%s.%s", StageDAO.TABLE_STAGE, StageDAO.FIELD_NAME)));
				vo.setDescription(rs.getString(String.format("%s.%s", StageDAO.TABLE_STAGE, StageDAO.FIELD_DESCRIPTION)));
				vo.setDeadline(rs.getTimestamp(String.format("%s.%s", StageDAO.TABLE_STAGE, StageDAO.FIELD_DEADLINE)));
				vo.setReward(rs.getDouble(String.format("%s.%s", StageDAO.TABLE_STAGE, StageDAO.FIELD_REWARD)));
				vo.setIndex(rs.getInt(String.format("%s.%s", StageDAO.TABLE_STAGE, StageDAO.FIELD_INDEX)));
				vo.setWorkerNum(rs.getInt(String.format("%s.%s", StageDAO.TABLE_STAGE, StageDAO.FIELD_WORKER_NUM)));
				vo.setAggregateMethod(rs.getInt(String.format("%s.%s", StageDAO.TABLE_STAGE, StageDAO.FIELD_AGGREGATE_METHOD)));
				vo.setAggregateResult(rs.getString(String.format("%s.%s", StageDAO.TABLE_STAGE, StageDAO.FIELD_AGGREGATE_RESULT)));
				vo.setRestrictions(rs.getLong(String.format("%s.%s", StageDAO.TABLE_STAGE, StageDAO.FIELD_RESTRICTIONS)));
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
	public UndertakeVO queryByUserAndStage(int userId, int stageId) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?", TABLE_UNDERTAKE, FIELD_USER_ID, FIELD_STAGE_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, userId);
			ps.setInt(2, stageId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				UndertakeVO vo = loadFromResultSet(rs);
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
	public List<UndertakeVO> queryByStage(int stageId) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_UNDERTAKE, FIELD_STAGE_ID);
		List<UndertakeVO> list = new ArrayList<UndertakeVO>();
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, stageId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				UndertakeVO vo = loadFromResultSet(rs);
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

	private UndertakeVO loadFromResultSet(ResultSet rs) {
		UndertakeVO vo = new UndertakeVO();
		try {
			vo.setId(rs.getInt(FIELD_ID));
			vo.setUserId(rs.getInt(FIELD_USER_ID));
			vo.setStageId(rs.getInt(FIELD_STAGE_ID));
			vo.setStartTime(rs.getTimestamp(FIELD_START_TIME));
			vo.setEndTime(rs.getTimestamp(FIELD_END_TIME));
			vo.setStatus(rs.getInt(FIELD_STATUS));
			vo.setContractTime(rs.getTimestamp(FIELD_CONTRACT_TIME));
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return vo;
	}
}
