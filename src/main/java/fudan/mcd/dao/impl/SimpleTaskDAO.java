package fudan.mcd.dao.impl;

import fudan.mcd.dao.abs.AbstractSimpleTaskDAO;
import fudan.mcd.vo.SimpleTaskVO;

import javax.servlet.ServletContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleTaskDAO extends AbstractSimpleTaskDAO {
    public SimpleTaskDAO(ServletContext context) {
        super(context);
    }

    public SimpleTaskDAO(String configPath) { super(configPath); }

    public static final String TABLE_ACTION = "simpletask";
    public static final String FIELD_ID = "userId";
    public static final String FIELD_TASK_ID = "taskId";
    public static final String FIELD_MAX_TASK_ID = "max(taskId)";
    public static final String FIELD_TASK_DESC = "taskDesc";
    public static final String FIELD_DURATION = "duration";
    public static final String FIELD_LOCATION_DESC = "locationDesc";
    public static final String FIELD_BONUS = "bonus";
    public static final String FIELD_PUBLISHTIME = "publishTime";
    public static final String FIELD_CALLBACKURL = "callbackurl";


    @Override
    public List<SimpleTaskVO> queryTaskListByUser(int userId) {
        String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_ACTION, FIELD_ID);
        PreparedStatement ps = null;
        List<SimpleTaskVO> list = new ArrayList<SimpleTaskVO>();
        Connection connection = getConnection();
        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SimpleTaskVO svo = loadFromResultSet(rs);
                list.add(svo);
            }
            return list;
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
    public Integer insert(SimpleTaskVO simpleTaskVO) {
        String sql = String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) values(?, ?, ?, ?, ?, ?)",
                TABLE_ACTION, FIELD_ID, FIELD_TASK_DESC, FIELD_LOCATION_DESC, FIELD_BONUS, FIELD_DURATION, FIELD_CALLBACKURL);
        PreparedStatement ps = null;
        String sql_select = String.format("SELECT MAX(%s) FROM %s", FIELD_TASK_ID, TABLE_ACTION);
        PreparedStatement ps_select = null;
        Connection connection = getConnection();
        try {
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, simpleTaskVO.getUserId());
            ps.setString(2, simpleTaskVO.getTaskDesc());
            ps.setString(3, simpleTaskVO.getLocationDesc());
            ps.setInt(4, simpleTaskVO.getBonus());
            ps.setInt(5, simpleTaskVO.getDuration());
            ps.setString(6, simpleTaskVO.getCallbackUrl());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();

            ps_select = connection.prepareStatement(sql_select, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs_select = ps_select.executeQuery();

//            if (rs_select.next()) {
//                int tmp = rs_select.findColumn(FIELD_TASK_ID);
//            }
            if (rs_select.next()) {
                int taskId = rs_select.getInt(FIELD_MAX_TASK_ID);
                simpleTaskVO.setTaskId(taskId);
            }



//            psSelect = connection.prepareStatement(sqlSelectForCallback, Statement.RETURN_GENERATED_KEYS);
//            ResultSet rsSelect = psSelect.executeQuery();
//            String callback = null;
//
//            callback = rsSelect.getString(FIELD_CALLBACKURL);
//




            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return -2;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return -3;
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
        }    }

    public List<SimpleTaskVO> queryAllTask() {
        String sql = String.format("SELECT * FROM %s", TABLE_ACTION);
        PreparedStatement ps = null;
        List<SimpleTaskVO> list = new ArrayList<SimpleTaskVO>();
        Connection connection = getConnection();
        try {
            ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SimpleTaskVO svo = loadFromResultSet(rs);
                list.add(svo);
            }
            return list;
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
    public SimpleTaskVO delete(Integer integer) {
        return null;
    }

    @Override
    public int update(SimpleTaskVO simpleTaskVO) {
        return 0;
    }

    @Override
    public SimpleTaskVO query(Integer integer) {
        return null;
    }


    private SimpleTaskVO loadFromResultSet(ResultSet rs) {
        SimpleTaskVO svo = new SimpleTaskVO();
        try {
            svo.setUserId(rs.getInt(FIELD_ID));
            svo.setTaskId(rs.getInt(FIELD_TASK_ID));
            svo.setTaskDesc(rs.getString(FIELD_TASK_DESC));
            svo.setLocationDesc(rs.getString(FIELD_LOCATION_DESC));
            svo.setBonus(rs.getInt(FIELD_BONUS));
            svo.setDuration(rs.getInt(FIELD_DURATION));
            svo.setTaskId(rs.getInt(FIELD_TASK_ID));
            svo.setPublishTime(rs.getTimestamp(FIELD_PUBLISHTIME));
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return svo;
    }

}
