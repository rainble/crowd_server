package fudan.mcd.dao.impl;

import fudan.mcd.dao.abs.AbstractTemplateDAO;
import fudan.mcd.vo.TemplateVO;

import javax.servlet.ServletContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TemplateDAO extends AbstractTemplateDAO {
	public static final String TABLE_TEMPLATE = "template";
	public static final String FIELD_ID = "templateId";
	public static final String FIELD_USER_ID = "userId";
	public static final String FIELD_NAME = "tempName";
	public static final String FIELD_URI = "uri";
	public static final String FIELD_CREATE_TIME = "create_time";
	public static final String FIELD_TOTAL_STAGE_NUM = "stageNum";
	public static final String FIELD_HEAT = "heat";
	public static final String FIELD_DESCRIPTION = "tempDesc";

	public TemplateDAO(ServletContext context) {
		super(context);
	}

	public TemplateDAO(String configPath) {
		super(configPath);
	}

	@Override
	public Integer insert(TemplateVO vo) {
		String sql = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s) values(?, ?, ?, ?, ?, ?, ?)", TABLE_TEMPLATE, FIELD_USER_ID,
				FIELD_NAME, FIELD_URI, FIELD_CREATE_TIME, FIELD_TOTAL_STAGE_NUM, FIELD_HEAT, FIELD_DESCRIPTION);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, vo.getUserId());
			ps.setString(2, vo.getName());
			ps.setString(3, vo.getUri());
			ps.setTimestamp(4, vo.getCreateTime());
			ps.setInt(5, vo.getTotalStageNum());
			ps.setInt(6, vo.getHeat());
			ps.setString(7, vo.getDescription());
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
	public TemplateVO delete(Integer pk) {
		TemplateVO vo = query(pk);
		if (vo == null)
			return null;
		String sql = String.format("DELETE FROM %s WHERE %s = ?", TABLE_TEMPLATE, FIELD_ID);
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
	public int update(TemplateVO vo) {
		if (query(vo.getId()) == null)
			return -1;
		String sql = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?", TABLE_TEMPLATE, FIELD_NAME,
				FIELD_URI, FIELD_CREATE_TIME, FIELD_TOTAL_STAGE_NUM, FIELD_HEAT, FIELD_DESCRIPTION, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setString(1, vo.getName());
			ps.setString(2, vo.getUri());
			ps.setTimestamp(3, vo.getCreateTime());
			ps.setInt(4, vo.getTotalStageNum());
			ps.setInt(5, vo.getHeat());
			ps.setString(6, vo.getDescription());
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
	public TemplateVO query(Integer pk) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_TEMPLATE, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, pk);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				TemplateVO vo = loadFromResultSet(rs);
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
	public List<TemplateVO> queryTemplateListByUser(int userId) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_TEMPLATE, FIELD_USER_ID);
		List<TemplateVO> list = new ArrayList<TemplateVO>();
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				TemplateVO vo = loadFromResultSet(rs);
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
	public List<TemplateVO> queryAllTemplateList() {
		String sql = String.format("SELECT * FROM %s", TABLE_TEMPLATE);
		List<TemplateVO> list = new ArrayList<TemplateVO>();
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				TemplateVO vo = loadFromResultSet(rs);
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
	public List<TemplateVO> queryTemplateListContainingName(String name) {
		String sql = String.format("SELECT * FROM %s WHERE %s LIKE '%%%s%%'", TABLE_TEMPLATE, FIELD_NAME, name);
		List<TemplateVO> list = new ArrayList<TemplateVO>();
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				TemplateVO vo = loadFromResultSet(rs);
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

	private TemplateVO loadFromResultSet(ResultSet rs) {
		TemplateVO vo = new TemplateVO();
		try {
			vo.setId(rs.getInt(FIELD_ID));
			vo.setUserId(rs.getInt(FIELD_USER_ID));
			vo.setName(rs.getString(FIELD_NAME));
			vo.setUri(rs.getString(FIELD_URI));
			vo.setCreateTime(rs.getTimestamp(FIELD_CREATE_TIME));
			vo.setTotalStageNum(rs.getInt(FIELD_TOTAL_STAGE_NUM));
			vo.setHeat(rs.getInt(FIELD_HEAT));
			vo.setDescription(rs.getString(FIELD_DESCRIPTION));
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return vo;
	}
}
