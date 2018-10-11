package fudan.mcd.dao.impl;

import fudan.mcd.dao.abs.AbstractPictureOutputDAO;
import fudan.mcd.vo.PictureOutputVO;

import javax.servlet.ServletContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class PictureOutputDAO extends AbstractPictureOutputDAO {
	public static final String TABLE_PICTURE_OUTPUT = "pictureoutput";
	public static final String FIELD_ID = "outputId";
	public static final String FIELD_ACTION_ID = "actionId";
	public static final String FIELD_DESCRIPTION = "outputDesc";
	public static final String FIELD_VALUE = "outputValue";
	public static final String FIELD_WORKER_ID = "workerId";
	public static final String FIELD_INDICATOR = "outputIndicator";

	public PictureOutputDAO(ServletContext context) {
		super(context);
	}

	public PictureOutputDAO(String configPath) {
		super(configPath);
	}

	@Override
	public Integer insert(PictureOutputVO vo) {
		String sql = String.format("INSERT INTO %s (%s, %s, %s, %s, %s) values(?, ?, ?, ?, ?)", TABLE_PICTURE_OUTPUT, FIELD_ACTION_ID,
				FIELD_DESCRIPTION, FIELD_VALUE, FIELD_WORKER_ID, FIELD_INDICATOR);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, vo.getActionId());
			ps.setString(2, vo.getDesc());
			ps.setString(3, vo.getValue());
			ps.setInt(4, vo.getWorkerId());
			ps.setInt(5, vo.getIndicator());
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
	public PictureOutputVO delete(Integer pk) {
		PictureOutputVO vo = query(pk);
		if (vo == null)
			return null;
		String sql = String.format("DELETE FROM %s WHERE %s = ?", TABLE_PICTURE_OUTPUT, FIELD_ID);
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
	public int update(PictureOutputVO vo) {
		if (query(vo.getId()) == null)
			return -1;
		String sql = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?", TABLE_PICTURE_OUTPUT, FIELD_ACTION_ID,
				FIELD_DESCRIPTION, FIELD_VALUE, FIELD_WORKER_ID, FIELD_INDICATOR, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, vo.getActionId());
			ps.setString(2, vo.getDesc());
			ps.setString(3, vo.getValue());
			ps.setInt(4, vo.getWorkerId());
			ps.setInt(5, vo.getIndicator());
			ps.setInt(6, vo.getId());
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
	public PictureOutputVO query(Integer pk) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_PICTURE_OUTPUT, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, pk);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				PictureOutputVO vo = loadFromResultSet(rs);
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
	public List<PictureOutputVO> queryOutputListByAction(int actionId) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_PICTURE_OUTPUT, FIELD_ACTION_ID);
		List<PictureOutputVO> list = new ArrayList<PictureOutputVO>();
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, actionId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				PictureOutputVO vo = loadFromResultSet(rs);
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
	public List<PictureOutputVO> queryOutputListByActionAndIndicator(int actionId, int indicator) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?", TABLE_PICTURE_OUTPUT, FIELD_ACTION_ID, FIELD_INDICATOR);
		List<PictureOutputVO> list = new ArrayList<PictureOutputVO>();
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, actionId);
			ps.setInt(2, indicator);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				PictureOutputVO vo = loadFromResultSet(rs);
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
	public List<PictureOutputVO> queryOutputListByActionAndUser(int actionId, int userId, int indicator) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ? AND %s = ?", TABLE_PICTURE_OUTPUT, FIELD_ACTION_ID, FIELD_WORKER_ID,
				FIELD_INDICATOR);
		List<PictureOutputVO> list = new ArrayList<PictureOutputVO>();
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, actionId);
			ps.setInt(2, userId);
			ps.setInt(3, indicator);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				PictureOutputVO vo = loadFromResultSet(rs);
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

	private PictureOutputVO loadFromResultSet(ResultSet rs) {
		PictureOutputVO vo = new PictureOutputVO();
		try {
			vo.setId(rs.getInt(FIELD_ID));
			vo.setActionId(rs.getInt(FIELD_ACTION_ID));
			vo.setDesc(rs.getString(FIELD_DESCRIPTION));
			vo.setValue(rs.getString(FIELD_VALUE));
			vo.setWorkerId(rs.getInt(FIELD_WORKER_ID));
			vo.setIndicator(rs.getInt(FIELD_INDICATOR));
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return vo;
	}
}
