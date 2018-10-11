package fudan.mcd.dao.impl;

import fudan.mcd.dao.abs.AbstractEnumOutputDAO;
import fudan.mcd.vo.EnumOutputVO;

import javax.servlet.ServletContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class EnumOutputDAO extends AbstractEnumOutputDAO {
	public static final String TABLE_ENUM_OUTPUT = "enumoutput";
	public static final String FIELD_ID = "outputId";
	public static final String FIELD_ACTION_ID = "actionId";
	public static final String FIELD_DESCRIPTION = "outputDesc";
	public static final String FIELD_VALUE = "outputValue";
	public static final String FIELD_WORKER_ID = "workerId";
	public static final String FIELD_INDICATOR = "outputIndicator";
	public static final String FIELD_ENTRIES = "entries";
	public static final String FIELD_AGGREGATION_METHOD = "enumAggregate";

	public EnumOutputDAO(ServletContext context) {
		super(context);
	}

	public EnumOutputDAO(String configPath) {
		super(configPath);
	}

	@Override
	public Integer insert(EnumOutputVO vo) {
		String sql = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s) values(?, ?, ?, ?, ?, ?, ?)", TABLE_ENUM_OUTPUT, FIELD_ACTION_ID,
				FIELD_DESCRIPTION, FIELD_VALUE, FIELD_WORKER_ID, FIELD_INDICATOR, FIELD_ENTRIES, FIELD_AGGREGATION_METHOD);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, vo.getActionId());
			ps.setString(2, vo.getDesc());
			ps.setString(3, vo.getValue());
			ps.setInt(4, vo.getWorkerId());
			ps.setInt(5, vo.getIndicator());
			ps.setString(6, vo.getEntries());
			ps.setInt(7, vo.getAggregationMethod());
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
	public EnumOutputVO delete(Integer pk) {
		EnumOutputVO vo = query(pk);
		if (vo == null)
			return null;
		String sql = String.format("DELETE FROM %s WHERE %s = ?", TABLE_ENUM_OUTPUT, FIELD_ID);
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
	public int update(EnumOutputVO vo) {
		if (query(vo.getId()) == null)
			return -1;
		String sql = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?", TABLE_ENUM_OUTPUT,
				FIELD_ACTION_ID, FIELD_DESCRIPTION, FIELD_VALUE, FIELD_WORKER_ID, FIELD_INDICATOR, FIELD_ENTRIES, FIELD_AGGREGATION_METHOD, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, vo.getActionId());
			ps.setString(2, vo.getDesc());
			ps.setString(3, vo.getValue());
			ps.setInt(4, vo.getWorkerId());
			ps.setInt(5, vo.getIndicator());
			ps.setString(6, vo.getEntries());
			ps.setInt(7, vo.getAggregationMethod());
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
	public EnumOutputVO query(Integer pk) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_ENUM_OUTPUT, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, pk);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				EnumOutputVO vo = loadFromResultSet(rs);
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
	public List<EnumOutputVO> queryOutputListByAction(int actionId) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_ENUM_OUTPUT, FIELD_ACTION_ID);
		List<EnumOutputVO> list = new ArrayList<EnumOutputVO>();
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, actionId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				EnumOutputVO vo = loadFromResultSet(rs);
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
	public List<EnumOutputVO> queryOutputListByActionAndIndicator(int actionId, int indicator) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?", TABLE_ENUM_OUTPUT, FIELD_ACTION_ID, FIELD_INDICATOR);
		List<EnumOutputVO> list = new ArrayList<EnumOutputVO>();
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, actionId);
			ps.setInt(2, indicator);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				EnumOutputVO vo = loadFromResultSet(rs);
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
	public List<EnumOutputVO> queryOutputListByActionAndUser(int actionId, int userId, int indicator) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ? AND %s = ?", TABLE_ENUM_OUTPUT, FIELD_ACTION_ID, FIELD_WORKER_ID,
				FIELD_INDICATOR);
		List<EnumOutputVO> list = new ArrayList<EnumOutputVO>();
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, actionId);
			ps.setInt(2, userId);
			ps.setInt(3, indicator);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				EnumOutputVO vo = loadFromResultSet(rs);
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

	private EnumOutputVO loadFromResultSet(ResultSet rs) {
		EnumOutputVO vo = new EnumOutputVO();
		try {
			vo.setId(rs.getInt(FIELD_ID));
			vo.setActionId(rs.getInt(FIELD_ACTION_ID));
			vo.setDesc(rs.getString(FIELD_DESCRIPTION));
			vo.setValue(rs.getString(FIELD_VALUE));
			vo.setWorkerId(rs.getInt(FIELD_WORKER_ID));
			vo.setIndicator(rs.getInt(FIELD_INDICATOR));
			vo.setEntries(rs.getString(FIELD_ENTRIES));
			vo.setAggregationMethod(rs.getInt(FIELD_AGGREGATION_METHOD));
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return vo;
	}
}
