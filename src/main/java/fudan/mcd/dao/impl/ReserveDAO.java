package fudan.mcd.dao.impl;

import fudan.mcd.dao.abs.AbstractReserveDAO;
import fudan.mcd.vo.ReserveVO;

import javax.servlet.ServletContext;
import java.sql.*;

public class ReserveDAO extends AbstractReserveDAO {
	public static final String TABLE_RESERVE = "reserve";
	public static final String FIELD_ID = "reserveId";
	public static final String FIELD_USER_ID = "userId";
	public static final String FIELD_STAGE_ID = "stageId";
	public static final String FIELD_RESERVE_TIME = "reserveTime";
	public static final String FIELD_PREVIOUS_USER_ID = "previousUserId";
	public static final String FIELD_PREVIOUS_STAGE_ID = "previousStageId";
	public static final String FIELD_STATUS = "reserveStatus";
	public static final String FIELD_CONTRACT = "reserveContract";

	public ReserveDAO(ServletContext context) {
		super(context);
	}

	public ReserveDAO(String configPath) {
		super(configPath);
	}

	@Override
	public Integer insert(ReserveVO vo) {
		String sql = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s) values(?, ?, ?, ?, ?, ?, ?)", TABLE_RESERVE, FIELD_USER_ID,
				FIELD_STAGE_ID, FIELD_RESERVE_TIME, FIELD_PREVIOUS_USER_ID, FIELD_PREVIOUS_STAGE_ID, FIELD_STATUS, FIELD_CONTRACT);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, vo.getUserId());
			ps.setInt(2, vo.getStageId());
			ps.setTimestamp(3, vo.getReserveTime());
			ps.setInt(4, vo.getPreviousUserId());
			ps.setInt(5, vo.getPreviousStageId());
			ps.setInt(6, vo.getStatus());
			ps.setTimestamp(7, vo.getContract());
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
	public ReserveVO delete(Integer pk) {
		ReserveVO vo = query(pk);
		if (vo == null)
			return null;
		String sql = String.format("DELETE FROM %s WHERE %s = ?", TABLE_RESERVE, FIELD_ID);
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
	public int update(ReserveVO vo) {
		if (query(vo.getId()) == null)
			return -1;
		String sql = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?", TABLE_RESERVE, FIELD_USER_ID,
				FIELD_STAGE_ID, FIELD_RESERVE_TIME, FIELD_PREVIOUS_USER_ID, FIELD_PREVIOUS_STAGE_ID, FIELD_STATUS, FIELD_CONTRACT, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, vo.getUserId());
			ps.setInt(2, vo.getStageId());
			ps.setTimestamp(3, vo.getReserveTime());
			ps.setInt(4, vo.getPreviousUserId());
			ps.setInt(5, vo.getPreviousStageId());
			ps.setInt(6, vo.getStatus());
			ps.setTimestamp(7, vo.getContract());
			ps.setInt(8, vo.getId());
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
	public ReserveVO query(Integer pk) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_RESERVE, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, pk);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ReserveVO vo = loadFromResultSet(rs);
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
	
	private ReserveVO loadFromResultSet(ResultSet rs) {
		ReserveVO vo = new ReserveVO();
		try {
			vo.setId(rs.getInt(FIELD_ID));
			vo.setUserId(rs.getInt(FIELD_USER_ID));
			vo.setStageId(rs.getInt(FIELD_STAGE_ID));
			vo.setReserveTime(rs.getTimestamp(FIELD_RESERVE_TIME));
			vo.setPreviousUserId(rs.getInt(FIELD_PREVIOUS_USER_ID));
			vo.setPreviousStageId(rs.getInt(FIELD_PREVIOUS_STAGE_ID));
			vo.setStatus(rs.getInt(FIELD_STATUS));
			vo.setContract(rs.getTimestamp(FIELD_CONTRACT));
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return vo;
	}
}
