package fudan.mcd.dao.impl;

import fudan.mcd.dao.abs.AbstractAccepetSimpleTaskDAO;
import fudan.mcd.dao.abs.AbstractSimpleTaskDAO;
import fudan.mcd.utils.HttpRequestUtil;
import fudan.mcd.vo.SimpleTaskVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
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
    public static final String FIELD_CALLBACKURL = "callbackurl";

    private static final Log LOG = LogFactory.getLog(AcceptSimpleTaskDAO.class);



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

        // TODO: 2019-01-24 要把这句sql语句改成两句，而且不要用*，会有顺序问题
        String sql_insert = String.format("INSERT INTO %s(%s, %s, %s, %s, %s, %s, %s, %s) SELECT * FROM %s WHERE %s = ?",
                TABLE_ACTION, FIELD_ID, FIELD_TASK_ID, FIELD_TASK_DESC, FIELD_LOCATION_DESC, FIELD_BONUS, FIELD_DURATION, FIELD_CALLBACKURL, FIELD_PUBLISH_TIME,
                TABLE_TARGET, FIELD_TASK_ID);
        String sql_drop = String.format("DELETE FROM %s WHERE %s = ?", TABLE_TARGET, FIELD_TASK_ID);
        String sql_update = String.format("UPDATE %s SET %s = ? WHERE %s = ?", TABLE_ACTION, FIELD_ID, FIELD_TASK_ID);
        PreparedStatement ps_insert = null;
        PreparedStatement ps_drop = null;
        PreparedStatement ps_update = null;
        Connection connection = getConnection();
        try {
            ps_insert = connection.prepareStatement(sql_insert, Statement.RETURN_GENERATED_KEYS);
            ps_insert.setInt(1, taskId);
            ps_insert.executeUpdate();
            ps_update = connection.prepareStatement(sql_update, Statement.RETURN_GENERATED_KEYS);
            ps_update.setInt(1, userId);
            ps_update.setInt(2, taskId);
            ps_update.executeUpdate();
            ps_drop = connection.prepareStatement(sql_drop, Statement.RETURN_GENERATED_KEYS);
            ps_drop.setInt(1, taskId);
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
        String sqlSelect = String.format("SELECT %s FROM %s WHERE %s = ? AND %s = ?", FIELD_CALLBACKURL, TABLE_ACTION, FIELD_TASK_ID, FIELD_ID);
        PreparedStatement psUpdate = null;
        PreparedStatement psSelect = null;
        Connection connection = getConnection();
        try {
            psUpdate = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            psUpdate.setInt(1, 2);
            psUpdate.setInt(2, userId);
            psUpdate.setInt(3, taskId);
            psUpdate.executeUpdate();
            ResultSet rsUpdate = psUpdate.getGeneratedKeys();

            psSelect = connection.prepareStatement(sqlSelect, Statement.RETURN_GENERATED_KEYS);
            psSelect.setInt(1,taskId);
            psSelect.setInt(2, userId);
            ResultSet rsSelect = psSelect.executeQuery();
            String callback = null;
            if (rsSelect.next())
            {
                int i =1;
                int j  = i+23;
            }


            callback = rsSelect.getString(FIELD_CALLBACKURL);

            String resutl = HttpRequestUtil.HTTPRequestDoGet(callback);
            LOG.debug(String.format("Callback result from workflow engine is [ %s ]. ", resutl));



            if (rsUpdate.next()) {
                return rsUpdate.getInt(1);

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
