package fudan.mcd.dao.impl;

import fudan.mcd.dao.abs.AbstractInputDAO;
import fudan.mcd.vo.InputVO;

import javax.servlet.ServletContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InputDAO extends AbstractInputDAO {
	public static final String TABLE_INPUT = "actioninput";
	public static final String FIELD_ID = "inputId";
	public static final String FIELD_ACTION_ID = "actionId";
	public static final String FIELD_TYPE = "inputType";
	public static final String FIELD_VALUE = "inputValue";
	public static final String FIELD_DESCRIPTION = "inputDesc";

	public InputDAO(ServletContext context) {
		super(context);
	}

	public InputDAO(String configPath) {
		super(configPath);
	}

	@Override
	public Integer insert(InputVO vo) {
		String sql = String.format("INSERT INTO %s (%s, %s, %s, %s) values(?, ?, ?, ?)", TABLE_INPUT, FIELD_ACTION_ID, FIELD_TYPE, FIELD_VALUE,
				FIELD_DESCRIPTION);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, vo.getActionId());
			ps.setInt(2, vo.getType());
			ps.setString(3, vo.getValue());
			ps.setString(4, vo.getDesc());
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
	public InputVO delete(Integer pk) {
		InputVO vo = query(pk);
		if (vo == null)
			return null;
		String sql = String.format("DELETE FROM %s WHERE %s = ?", TABLE_INPUT, FIELD_ID);
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
	public int update(InputVO vo) {
		if (query(vo.getId()) == null)
			return -1;
		String sql = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?", TABLE_INPUT, FIELD_ACTION_ID, FIELD_TYPE,
				FIELD_VALUE, FIELD_DESCRIPTION, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, vo.getActionId());
			ps.setInt(2, vo.getType());
			ps.setString(3, vo.getValue());
			ps.setString(4, vo.getDesc());
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
	public InputVO query(Integer pk) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_INPUT, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, pk);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				InputVO vo = loadFromResultSet(rs);
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
	public List<InputVO> queryInputListByAction(int actionId) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_INPUT, FIELD_ACTION_ID);
		List<InputVO> list = new ArrayList<InputVO>();
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, actionId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				InputVO vo = loadFromResultSet(rs);
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

	private InputVO loadFromResultSet(ResultSet rs) {
		InputVO vo = new InputVO();
		try {
			vo.setId(rs.getInt(FIELD_ID));
			vo.setActionId(rs.getInt(FIELD_ACTION_ID));
			vo.setType(rs.getInt(FIELD_TYPE));
			vo.setValue(rs.getString(FIELD_VALUE));
			vo.setDesc(rs.getString(FIELD_DESCRIPTION));
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return vo;
	}
}
