package fudan.mcd.dao.impl;

import fudan.mcd.dao.abs.AbstractAccepetSimpleTaskDAO;
import fudan.mcd.dao.abs.AbstractSimpleTaskDAO;
import fudan.mcd.vo.SimpleTaskVO;

import javax.servlet.ServletContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AcceptSimpleTaskDAO extends AbstractAccepetSimpleTaskDAO {

    public static final String TABLE_ACTION = "acceptsimpletask";
    public static final String FIELD_ID = "userId";
    public static final String FIELD_TASK_ID = "taskId";
    public static final String FIELD_TASK_DESC = "taskDesc";
    public static final String FIELD_DURATION = "duration";
    public static final String FIELD_LOCATION_DESC = "locationDesc";
    public static final String FIELD_BONUS = "bonus";
    public static final String FIELD_PUBLISH_TIME = "publishTime";
    public static final String TABLE_TARGET = "simpletask";
    public static final String FIELD_STATE = "taskState";


    public AcceptSimpleTaskDAO(ServletContext context) {
        super(context);
    }

    public AcceptSimpleTaskDAO(String configPath) {
        super(configPath);
    }

    @Override
    public List<SimpleTaskVO> queryAcceptTaskListByUser(int userId) {
        String sql = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?", TABLE_ACTION, FIELD_ID, FIELD_STATE);
        List<SimpleTaskVO> list = new ArrayList<SimpleTaskVO>();
        PreparedStatement ps = null;
        Connection connection = getConnection();
        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, 0);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SimpleTaskVO vo = loadFromResultSet(rs);
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
    public List<SimpleTaskVO> queryCompleteTaskListByUser(int userId) {
        String sql = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?", TABLE_ACTION, FIELD_ID, FIELD_STATE);
        List<SimpleTaskVO> list = new ArrayList<SimpleTaskVO>();
        PreparedStatement ps = null;
        Connection connection = getConnection();
        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, 1);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SimpleTaskVO vo = loadFromResultSet(rs);
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


    //insert into acceptsimpletask(userId,taskId,taskDesc,locationDesc,bonus,publishTime,duration) select * from simpletask where userId=32 and  taskId=5;

    public Integer insert(int userId, int taskId) {

        String sql_insert = String.format("INSERT INTO %s(%s, %s, %s, %s, %s, %s, %s) SELECT * FROM %s WHERE %s = ? AND %s = ?",
                TABLE_ACTION, FIELD_ID, FIELD_TASK_ID, FIELD_TASK_DESC, FIELD_LOCATION_DESC, FIELD_BONUS, FIELD_PUBLISH_TIME,FIELD_DURATION,
                TABLE_TARGET, FIELD_ID, FIELD_TASK_ID);
        String sql_drop = String.format("DELETE FROM %s WHERE %s = ? and %s = ?", TABLE_TARGET, FIELD_ID, FIELD_TASK_ID);
        PreparedStatement ps_insert = null;
        PreparedStatement ps_drop = null;
        Connection connection = getConnection();
        try {
            ps_insert = connection.prepareStatement(sql_insert, Statement.RETURN_GENERATED_KEYS);
            ps_insert.setInt(1, userId);
            ps_insert.setInt(2, taskId);
            ps_insert.executeUpdate();
            ps_drop = connection.prepareStatement(sql_drop, Statement.RETURN_GENERATED_KEYS);
            ps_drop.setInt(1, userId);
            ps_drop.setInt(2, taskId);
            ps_drop.executeUpdate();
            ResultSet rs = ps_insert.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return -2;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return -3;
        }

    }


    public int complete(int userId, int taskId) {
        String sql = String.format("UPDATE %s SET %s = ? WHERE %s = ? AND %s = ?", TABLE_ACTION, FIELD_STATE, FIELD_ID, FIELD_TASK_ID);
        PreparedStatement ps = null;
        Connection connection = getConnection();
        try {
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, 1);
            ps.setInt(2, userId);
            ps.setInt(3, taskId);
            ps.executeUpdate();
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return -2;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return -3;
        }
    }

    private SimpleTaskVO loadFromResultSet(ResultSet rs) {
        SimpleTaskVO vo = new SimpleTaskVO();
        try {
            vo.setUserId(rs.getInt(FIELD_ID));
            vo.setTaskId(rs.getInt(FIELD_TASK_ID));
            vo.setBonus(rs.getInt(FIELD_BONUS));
            vo.setDuration(rs.getInt(FIELD_DURATION));
            vo.setLocationDesc(rs.getString(FIELD_LOCATION_DESC));
            vo.setTaskDesc(rs.getString(FIELD_TASK_DESC));
            vo.setPublishTime(rs.getTimestamp(FIELD_PUBLISH_TIME));
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return vo;
    }

    @Override
    public Object insert(Object o) {
        return null;
    }

    @Override
    public Object delete(Object o) {
        return null;
    }

    @Override
    public int update(Object o) {
        return 0;
    }

    @Override
    public Object query(Object o) {
        return null;
    }
}
