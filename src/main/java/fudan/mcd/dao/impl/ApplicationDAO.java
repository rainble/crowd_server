package fudan.mcd.dao.impl;

import fudan.mcd.dao.abs.AbstractApplicationDAO;
import fudan.mcd.vo.ApplicationVO;

import javax.servlet.ServletContext;
import java.sql.*;

public class ApplicationDAO extends AbstractApplicationDAO {
	public static final String TABLE_APPLICATION = "application";
	public static final String FIELD_ID = "applicationId";
	public static final String FIELD_USER_ID = "userId";
	public static final String FIELD_STAGE_ID = "stageId";

	public ApplicationDAO(ServletContext context) {
		super(context);
	}

	public ApplicationDAO(String configPath) {
		super(configPath);
	}

	@Override
	public Integer insert(ApplicationVO vo) {
		String sql = String.format("INSERT INTO %s (%s, %s) values(?, ?)", TABLE_APPLICATION, FIELD_USER_ID, FIELD_STAGE_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, vo.getUserId());
			ps.setInt(2, vo.getStageId());
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
	public ApplicationVO delete(Integer pk) {
		ApplicationVO vo = query(pk);
		if (vo == null)
			return null;
		String sql = String.format("DELETE FROM %s WHERE %s = ?", TABLE_APPLICATION, FIELD_ID);
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
	public int update(ApplicationVO vo) {
		if (query(vo.getId()) == null)
			return -1;
		String sql = String.format("UPDATE %s SET %s = ?, %s = ? WHERE %s = ?", TABLE_APPLICATION, FIELD_USER_ID, FIELD_STAGE_ID, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, vo.getUserId());
			ps.setInt(2, vo.getStageId());
			ps.setInt(3, vo.getId());
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
	public ApplicationVO query(Integer pk) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_APPLICATION, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, pk);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ApplicationVO vo = loadFromResultSet(rs);
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
	
	public ApplicationVO queryByUserAndStage(int userId, int stageId) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?", TABLE_APPLICATION, FIELD_USER_ID, FIELD_STAGE_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, userId);
			ps.setInt(2, stageId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ApplicationVO vo = loadFromResultSet(rs);
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
	public ApplicationVO deleteByUserAndStage(int userId, int stageId) {
		ApplicationVO vo = queryByUserAndStage(userId, stageId);
		if (vo == null)
			return null;
		String sql = String.format("DELETE FROM %s WHERE %s = ? AND %s = ?", TABLE_APPLICATION, FIELD_USER_ID, FIELD_STAGE_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, userId);
			ps.setInt(2, stageId);
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

	private ApplicationVO loadFromResultSet(ResultSet rs) {
		ApplicationVO vo = new ApplicationVO();
		try {
			vo.setId(rs.getInt(FIELD_ID));
			vo.setUserId(rs.getInt(FIELD_USER_ID));
			vo.setStageId(rs.getInt(FIELD_STAGE_ID));
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return vo;
	}
}
