package fudan.mcd.dao.impl;

import fudan.mcd.dao.abs.AbstractActionDAO;
import fudan.mcd.vo.ActionVO;

import javax.servlet.ServletContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActionDAO extends AbstractActionDAO {
	public static final String TABLE_ACTION = "locaction";
	public static final String FIELD_ID = "actionId";
	public static final String FIELD_LOCATION_ID = "locationId";
	public static final String FIELD_DURATION = "duration";
	public static final String FIELD_TYPE = "actionType";

	public ActionDAO(ServletContext context) {
		super(context);
	}

	public ActionDAO(String configPath) {
		super(configPath);
	}

	@Override
	public Integer insert(ActionVO vo) {
		String sql = String.format("INSERT INTO %s (%s, %s, %s) values(?, ?, ?)", TABLE_ACTION, FIELD_LOCATION_ID, FIELD_DURATION, FIELD_TYPE);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, vo.getLocationId());
			ps.setDouble(2, vo.getDuration());
			ps.setInt(3, vo.getType());
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
	public ActionVO delete(Integer pk) {
		ActionVO vo = query(pk);
		if (vo == null)
			return null;
		String sql = String.format("DELETE FROM %s WHERE %s = ?", TABLE_ACTION, FIELD_ID);
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
	public int update(ActionVO vo) {
		if (query(vo.getId()) == null)
			return -1;
		String sql = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ? WHERE %s = ?", TABLE_ACTION, FIELD_LOCATION_ID, FIELD_DURATION, FIELD_TYPE,
				FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, vo.getLocationId());
			ps.setDouble(2, vo.getDuration());
			ps.setInt(3, vo.getType());
			ps.setInt(4, vo.getId());
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
	public ActionVO query(Integer pk) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_ACTION, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, pk);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ActionVO vo = loadFromResultSet(rs);
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
	public List<ActionVO> queryActionListByLocation(int locationId) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_ACTION, FIELD_LOCATION_ID);
		List<ActionVO> list = new ArrayList<ActionVO>();
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, locationId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				ActionVO vo = loadFromResultSet(rs);
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

	private ActionVO loadFromResultSet(ResultSet rs) {
		ActionVO vo = new ActionVO();
		try {
			vo.setId(rs.getInt(FIELD_ID));
			vo.setLocationId(rs.getInt(FIELD_LOCATION_ID));
			vo.setDuration(rs.getDouble(FIELD_DURATION));
			vo.setType(rs.getInt(FIELD_TYPE));
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return vo;
	}
}
