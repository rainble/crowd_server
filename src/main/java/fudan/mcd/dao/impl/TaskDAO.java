package fudan.mcd.dao.impl;

import fudan.mcd.dao.abs.AbstractTaskDAO;
import fudan.mcd.vo.TaskVO;

import javax.servlet.ServletContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO extends AbstractTaskDAO {
	public static final String TABLE_TASK = "task";
	public static final String FIELD_ID = "taskId";
	public static final String FIELD_TEMPLATE_ID = "templateId";
	public static final String FIELD_USER_ID = "userId";
	public static final String FIELD_TITLE = "title";
	public static final String FIELD_DESCRIPTION = "description";
	public static final String FIELD_STATUS = "taskStatus";
	public static final String FIELD_CURRENT_STAGE = "currentStage";
	public static final String FIELD_BONUS_REWARD = "bonus";
	public static final String FIELD_PUBLISH_TIME = "publish_time";
	public static final String FIELD_DEADLINE = "ddl";
	public static final String FIELD_USER_TYPE = "userScope";

	public TaskDAO(ServletContext context) {
		super(context);
	}

	public TaskDAO(String configPath) {
		super(configPath);
	}

	@Override
	public Integer insert(TaskVO vo) {
		String sql = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", TABLE_TASK,
				FIELD_TEMPLATE_ID, FIELD_USER_ID, FIELD_TITLE, FIELD_DESCRIPTION, FIELD_STATUS, FIELD_CURRENT_STAGE, FIELD_BONUS_REWARD,
				FIELD_PUBLISH_TIME, FIELD_DEADLINE, FIELD_USER_TYPE);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, vo.getTemplateId());
			ps.setInt(2, vo.getUserId());
			ps.setString(3, vo.getTitle());
			ps.setString(4, vo.getDescription());
			ps.setInt(5, vo.getStatus());
			ps.setInt(6, vo.getCurrentStage());
			ps.setDouble(7, vo.getBonusReward());
			ps.setTimestamp(8, vo.getPublishTime());
			ps.setTimestamp(9, vo.getDeadline());
			ps.setInt(10, vo.getUserType());
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
	public TaskVO delete(Integer pk) {
		TaskVO vo = query(pk);
		if (vo == null)
			return null;
		String sql = String.format("DELETE FROM %s WHERE %s = ?", TABLE_TASK, FIELD_ID);
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
	public int update(TaskVO vo) {
		if (query(vo.getId()) == null)
			return -1;
		String sql = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?", TABLE_TASK,
				FIELD_TITLE, FIELD_DESCRIPTION, FIELD_STATUS, FIELD_CURRENT_STAGE, FIELD_BONUS_REWARD, FIELD_PUBLISH_TIME, FIELD_DEADLINE,
				FIELD_USER_TYPE, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setString(1, vo.getTitle());
			ps.setString(2, vo.getDescription());
			ps.setInt(3, vo.getStatus());
			ps.setInt(4, vo.getCurrentStage());
			ps.setDouble(5, vo.getBonusReward());
			ps.setTimestamp(6, vo.getPublishTime());
			ps.setTimestamp(7, vo.getDeadline());
			ps.setInt(8, vo.getUserType());
			ps.setInt(9, vo.getId());
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
	public TaskVO query(Integer pk) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_TASK, FIELD_ID);
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, pk);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				TaskVO vo = loadFromResultSet(rs);
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
	public List<TaskVO> queryTaskListByUser(int userId) {
		String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_TASK, FIELD_USER_ID);
		List<TaskVO> list = new ArrayList<TaskVO>();
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				TaskVO vo = loadFromResultSet(rs);
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
	public List<TaskVO> queryAllTaskList() {
		String sql = String.format("SELECT * FROM %s", TABLE_TASK);
		List<TaskVO> list = new ArrayList<TaskVO>();
		PreparedStatement ps = null;
		Connection connection = getConnection();
		try {
			ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				TaskVO vo = loadFromResultSet(rs);
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

	private TaskVO loadFromResultSet(ResultSet rs) {
		TaskVO vo = new TaskVO();
		try {
			vo.setId(rs.getInt(FIELD_ID));
			vo.setTemplateId(rs.getInt(FIELD_TEMPLATE_ID));
			vo.setUserId(rs.getInt(FIELD_USER_ID));
			vo.setTitle(rs.getString(FIELD_TITLE));
			vo.setDescription(rs.getString(FIELD_DESCRIPTION));
			vo.setStatus(rs.getInt(FIELD_STATUS));
			vo.setCurrentStage(rs.getInt(FIELD_CURRENT_STAGE));
			vo.setBonusReward(rs.getDouble(FIELD_BONUS_REWARD));
			vo.setPublishTime(rs.getTimestamp(FIELD_PUBLISH_TIME));
			vo.setDeadline(rs.getTimestamp(FIELD_DEADLINE));
			vo.setUserType(rs.getInt(FIELD_USER_TYPE));
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return vo;
	}
}
