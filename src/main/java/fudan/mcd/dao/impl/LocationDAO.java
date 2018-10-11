package fudan.mcd.dao.impl;

import fudan.mcd.dao.abs.AbstractLocationDAO;
import fudan.mcd.vo.LocationVO;

import javax.servlet.ServletContext;
import java.sql.*;

public class LocationDAO extends AbstractLocationDAO {
	public static final String TABLE_LOCATION = "location";
	public static final String FIELD_ID = "locationId";
	public static final String FIELD_STAGE_ID = "stageId";
	public static final String FIELD_ADDRESS = "address";
	public static final String FIELD_LATITUDE = "latitude";
	public static final String FIELD_LONGITUDE = "longitude";
	public static final String FIELD_TYPE = "type";

	public LocationDAO(ServletContext context) {
		super(context);
	}

	public LocationDAO(String configPath) {
		super(configPath);
	}

	@Override
	public Integer insert(LocationVO vo) {
		String sql = String.format("INSERT INTO %s (%s, %s, %s, %s, %s) values(?, ?, ?, ?, ?)", TABLE_LOCATION, FIELD_STAGE_ID, FIELD_ADDRESS,
				FIELD_LATITUDE, FIELD_LONGITUDE, FIELD_TYPE);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, vo.getStageId());
			ps.setString(2, vo.getAddress());
			ps.setDouble(3, vo.getLatitude());
			ps.setDouble(4, vo.getLongitude());
			ps.setInt(5, vo.getType());
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
	public LocationVO delete(Integer pk) {
		LocationVO vo = query(pk);
		if (vo == null)
			return null;
		String sql = String.format("DELETE FROM %s WHERE %s = ?", TABLE_LOCATION, FIELD_ID);
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
	public int update(LocationVO vo) {
		if (query(vo.getId()) == null)
			return -1;
		String sql = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?", TABLE_LOCATION, FIELD_ADDRESS, FIELD_LATITUDE,
				FIELD_LONGITUDE, FIELD_TYPE, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setString(1, vo.getAddress());
			ps.setDouble(2, vo.getLatitude());
			ps.setDouble(3, vo.getLongitude());
			ps.setInt(4, vo.getType());
			ps.setInt(5, vo.getId());
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
	public LocationVO query(Integer pk) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_LOCATION, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, pk);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				LocationVO vo = loadFromResultSet(rs);
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
	public LocationVO queryByStageAndType(int stageId, int type) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?", TABLE_LOCATION, FIELD_STAGE_ID, FIELD_TYPE);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, stageId);
			ps.setInt(2, type);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				LocationVO vo = loadFromResultSet(rs);
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

	private LocationVO loadFromResultSet(ResultSet rs) {
		LocationVO vo = new LocationVO();
		try {
			vo.setId(rs.getInt(FIELD_ID));
			vo.setStageId(rs.getInt(FIELD_STAGE_ID));
			vo.setAddress(rs.getString(FIELD_ADDRESS));
			vo.setLatitude(rs.getDouble(FIELD_LATITUDE));
			vo.setLongitude(rs.getDouble(FIELD_LONGITUDE));
			vo.setType(rs.getInt(FIELD_TYPE));
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return vo;
	}
}
