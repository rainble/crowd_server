package fudan.mcd.dao.impl;

import fudan.mcd.dao.abs.AbstractUserDAO;
import fudan.mcd.vo.UserVO;

import javax.servlet.ServletContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends AbstractUserDAO {
	public static final String TABLE_USER = "user";
	public static final String FIELD_ID = "userId";
	public static final String FIELD_USERNAME = "username";
	public static final String FIELD_PASSWORD = "password";
	public static final String FIELD_PUBLISH_CREDIT = "creditPublish";
	public static final String FIELD_WITHDRAW_CREDIT = "creditWithdraw";
	public static final String FIELD_AVATAR = "headImg";
	public static final String FIELD_TAG = "userTag";
	public static final String FIELD_LOGIN_FLAG = "loginIndicator";
	public static final String FIELD_WECHAT_ID = "weixinId";
	public static final String FIELD_PHONE_NUM = "phone";

	public UserDAO(ServletContext context) {
		super(context);
	}

	public UserDAO(String configPath) {
		super(configPath);
	}

	@Override
	public Integer insert(UserVO vo) {
		String sql = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s) values(?, ?, ?, ?, ?, ?, ?, ?, ?)", TABLE_USER,
				FIELD_USERNAME, FIELD_PASSWORD, FIELD_PUBLISH_CREDIT, FIELD_WITHDRAW_CREDIT, FIELD_AVATAR, FIELD_TAG, FIELD_LOGIN_FLAG,
				FIELD_WECHAT_ID, FIELD_PHONE_NUM);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, vo.getAccount());
			ps.setString(2, vo.getPassword());
			ps.setDouble(3, vo.getPublishCredit());
			ps.setDouble(4, vo.getWithdrawCredit());
			ps.setString(5, vo.getAvatar());
			ps.setInt(6, vo.getTag());
			ps.setInt(7, vo.getLoginFlag());
			ps.setString(8, vo.getWeChatId());
			ps.setString(9, vo.getPhoneNum());
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
	public UserVO delete(Integer pk) {
		UserVO vo = query(pk);
		if (vo == null)
			return null;
		String sql = String.format("DELETE FROM %s WHERE %s = ?", TABLE_USER, FIELD_ID);
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
	public int update(UserVO vo) {
		if (query(vo.getId()) == null)
			return -1;
		String sql = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?", TABLE_USER,
				FIELD_USERNAME, FIELD_PASSWORD, FIELD_PUBLISH_CREDIT, FIELD_WITHDRAW_CREDIT, FIELD_AVATAR, FIELD_TAG, FIELD_LOGIN_FLAG,
				FIELD_WECHAT_ID, FIELD_PHONE_NUM, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setString(1, vo.getAccount());
			ps.setString(2, vo.getPassword());
			ps.setDouble(3, vo.getPublishCredit());
			ps.setDouble(4, vo.getWithdrawCredit());
			ps.setString(5, vo.getAvatar());
			ps.setInt(6, vo.getTag());
			ps.setInt(7, vo.getLoginFlag());
			ps.setString(8, vo.getWeChatId());
			ps.setString(9, vo.getPhoneNum());
			ps.setInt(10, vo.getId());
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
	public UserVO query(Integer pk) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_USER, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, pk);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				UserVO vo = loadFromResultSet(rs);
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
	public UserVO queryByAccount(String account) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_USER, FIELD_USERNAME);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setString(1, account);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				UserVO vo = loadFromResultSet(rs);
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
	public List<UserVO> queryAllUser() {
		String sql = String.format("SELECT * FROM %s", TABLE_USER);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			List<UserVO> users = new ArrayList<UserVO>();
			ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				UserVO vo = loadFromResultSet(rs);
				users.add(vo);
			}
			return users;
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

	private UserVO loadFromResultSet(ResultSet rs) {
		UserVO vo = new UserVO();
		try {
			vo.setId(rs.getInt(FIELD_ID));
			vo.setAccount(rs.getString(FIELD_USERNAME));
			vo.setPassword(rs.getString(FIELD_PASSWORD));
			vo.setPublishCredit(rs.getDouble(FIELD_PUBLISH_CREDIT));
			vo.setWithdrawCredit(rs.getDouble(FIELD_WITHDRAW_CREDIT));
			vo.setAvatar(rs.getString(FIELD_AVATAR));
			vo.setTag(rs.getInt(FIELD_TAG));
			vo.setLoginFlag(rs.getInt(FIELD_LOGIN_FLAG));
			vo.setWeChatId(rs.getString(FIELD_WECHAT_ID));
			vo.setPhoneNum(rs.getString(FIELD_PHONE_NUM));
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return vo;
	}
}
