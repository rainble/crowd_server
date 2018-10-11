package fudan.mcd.dao.impl;

import fudan.mcd.dao.abs.AbstractCollectionDAO;
import fudan.mcd.vo.CollectionVO;
import fudan.mcd.vo.TemplateVO;
import fudan.mcd.vo.UserVO;

import javax.servlet.ServletContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CollectionDAO extends AbstractCollectionDAO {
	public static final String TABLE_COLLECTION = "collect";
	public static final String FIELD_ID = "collectId";
	public static final String FIELD_USER_ID = "userId";
	public static final String FIELD_TEMPLATE_ID = "templateId";

	public CollectionDAO(ServletContext context) {
		super(context);
	}

	public CollectionDAO(String configPath) {
		super(configPath);
	}

	@Override
	public Integer insert(CollectionVO vo) {
		String sql = String.format("INSERT INTO %s (%s, %s) values(?, ?)", TABLE_COLLECTION, FIELD_USER_ID, FIELD_TEMPLATE_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, vo.getUserId());
			ps.setInt(2, vo.getTemplateId());
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
	public CollectionVO delete(Integer pk) {
		CollectionVO vo = query(pk);
		if (vo == null)
			return null;
		String sql = String.format("DELETE FROM %s WHERE %s = ?", TABLE_COLLECTION, FIELD_ID);
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
	public int update(CollectionVO vo) {
		if (query(vo.getId()) == null)
			return -1;
		String sql = String.format("UPDATE %s SET %s = ?, %s = ? WHERE %s = ?", TABLE_COLLECTION, FIELD_USER_ID, FIELD_TEMPLATE_ID, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, vo.getUserId());
			ps.setInt(2, vo.getTemplateId());
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
	public CollectionVO query(Integer pk) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_COLLECTION, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, pk);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				CollectionVO vo = loadFromResultSet(rs);
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
		String sql = String.format("SELECT * FROM %s, %s WHERE %s.%s = %s.%s AND %s.%s = ?", TABLE_COLLECTION, TemplateDAO.TABLE_TEMPLATE,
				TABLE_COLLECTION, FIELD_TEMPLATE_ID, TemplateDAO.TABLE_TEMPLATE, TemplateDAO.FIELD_ID, TABLE_COLLECTION, FIELD_USER_ID);
		List<TemplateVO> list = new ArrayList<TemplateVO>();
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				TemplateVO vo = new TemplateVO();
				vo.setId(rs.getInt(String.format("%s.%s", TemplateDAO.TABLE_TEMPLATE, TemplateDAO.FIELD_ID)));
				vo.setUserId(rs.getInt(String.format("%s.%s", TemplateDAO.TABLE_TEMPLATE, TemplateDAO.FIELD_USER_ID)));
				vo.setName(rs.getString(String.format("%s.%s", TemplateDAO.TABLE_TEMPLATE, TemplateDAO.FIELD_NAME)));
				vo.setUri(rs.getString(String.format("%s.%s", TemplateDAO.TABLE_TEMPLATE, TemplateDAO.FIELD_URI)));
				vo.setCreateTime(rs.getTimestamp(String.format("%s.%s", TemplateDAO.TABLE_TEMPLATE, TemplateDAO.FIELD_CREATE_TIME)));
				vo.setTotalStageNum(rs.getInt(String.format("%s.%s", TemplateDAO.TABLE_TEMPLATE, TemplateDAO.FIELD_TOTAL_STAGE_NUM)));
				vo.setHeat(rs.getInt(String.format("%s.%s", TemplateDAO.TABLE_TEMPLATE, TemplateDAO.FIELD_HEAT)));
				vo.setDescription(rs.getString(String.format("%s.%s", TemplateDAO.TABLE_TEMPLATE, TemplateDAO.FIELD_DESCRIPTION)));
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
	public List<UserVO> queryUserListByTemplate(int templateId) {
		String sql = String.format("SELECT * FROM %s, %s WHERE %s.%s = %s.%s AND %s.%s = ?", TABLE_COLLECTION, UserDAO.TABLE_USER, TABLE_COLLECTION,
				FIELD_USER_ID, UserDAO.TABLE_USER, UserDAO.FIELD_ID, TABLE_COLLECTION, FIELD_TEMPLATE_ID);
		List<UserVO> list = new ArrayList<UserVO>();
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, templateId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				UserVO vo = new UserVO();
				vo.setId(rs.getInt(String.format("%s.%s", UserDAO.TABLE_USER, UserDAO.FIELD_ID)));
				vo.setAccount(rs.getString(String.format("%s.%s", UserDAO.TABLE_USER, UserDAO.FIELD_USERNAME)));
				vo.setPassword(rs.getString(String.format("%s.%s", UserDAO.TABLE_USER, UserDAO.FIELD_PASSWORD)));
				vo.setPublishCredit(rs.getDouble(String.format("%s.%s", UserDAO.TABLE_USER, UserDAO.FIELD_PUBLISH_CREDIT)));
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
	public CollectionVO deleteByUserAndTemplate(int userId, int templateId) {
		CollectionVO vo = query(userId, templateId);
		if (vo == null)
			return null;
		String sql = String.format("DELETE FROM %s WHERE %s = ? AND %s = ?", TABLE_COLLECTION, FIELD_USER_ID, FIELD_TEMPLATE_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, userId);
			ps.setInt(2, templateId);
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

	private CollectionVO query(int userId, int templateId) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?", TABLE_COLLECTION, FIELD_USER_ID, FIELD_TEMPLATE_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, userId);
			ps.setInt(2, templateId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				CollectionVO vo = loadFromResultSet(rs);
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

	private CollectionVO loadFromResultSet(ResultSet rs) {
		CollectionVO vo = new CollectionVO();
		try {
			vo.setId(rs.getInt(FIELD_ID));
			vo.setUserId(rs.getInt(FIELD_USER_ID));
			vo.setTemplateId(rs.getInt(FIELD_TEMPLATE_ID));
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return vo;
	}
}
