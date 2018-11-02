package fudan.mcd.dao.impl;

import fudan.mcd.dao.abs.AbstractApplyDAO;
import fudan.mcd.vo.ApplyVO;

import javax.servlet.ServletContext;
import java.sql.*;

public class ApplyDAO extends AbstractApplyDAO {

    public static final String TABLE_ACTION = "apply";
    public static final String FIELD_ID = "userId";
    public static final String FIELD_TASK_ID = "taskId";
    public static final String FIELD_STAGE = "currentStage";
    public static final String FIELD_START_TIME = "startTime";
    public static final String FIELD_CONTRACT_TIME = "contractTime";
    public static final String FIELD_LOCATION_ID = "locationId";

    public ApplyDAO(ServletContext context) {
        super(context);
    }

    public ApplyDAO(String configPath) {
        super(configPath);
    }
    ///////////////////////////////////////////////////////////////////////////
    // create table apply (userId int, taskId int, currentStage int, startTime timestamp, contractTime timestamp, locationId int);
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public Integer insert(ApplyVO applyVO) {
        String sql = String.format("INSERT INTO %s (%s, %s, %s, %s, %s) values(?, ?, ?, ?, ?)",
                TABLE_ACTION, FIELD_ID, FIELD_TASK_ID, FIELD_STAGE, FIELD_START_TIME, FIELD_CONTRACT_TIME);
        PreparedStatement ps = null;
        Connection connection = getConnection();
        try {
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, applyVO.getUserId());
            ps.setDouble(2, applyVO.getTaskId());
            ps.setInt(3, applyVO.getCurrentStage());
            ps.setTimestamp(4, applyVO.getStartTime());
            ps.setTimestamp(5, applyVO.getContractTime());
//            ps.setInt(6, applyVO.getLocationId());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return -1;
            }
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
    public ApplyVO delete(Integer integer) {

        return null;
    }

    @Override
    public int update(ApplyVO applyVO) {
        return 0;
    }

    @Override

    public ApplyVO query(Integer integer) {

        String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_ACTION, FIELD_ID);
        PreparedStatement ps = null;
        Connection connection = getConnection();
        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, integer);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ApplyVO vo = loadFromResultSet(rs);
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


    private ApplyVO loadFromResultSet(ResultSet rs) {
        ApplyVO vo = new ApplyVO();
        try {
            vo.setUserId(rs.getInt(FIELD_ID));
            vo.setTaskId(rs.getInt(FIELD_TASK_ID));
            vo.setStartTime(rs.getTimestamp(FIELD_START_TIME));
            vo.setContractTime(rs.getTimestamp(FIELD_CONTRACT_TIME));
            vo.setCurrentStage(rs.getInt(FIELD_STAGE));
            vo.setLocationId(rs.getInt(FIELD_LOCATION_ID));
//            vo.setId(rs.getInt(FIELD_ID));
//            vo.setTemplateId(rs.getInt(FIELD_TEMPLATE_ID));
//            vo.setUserId(rs.getInt(FIELD_USER_ID));
//            vo.setTitle(rs.getString(FIELD_TITLE));
//            vo.setDescription(rs.getString(FIELD_DESCRIPTION));
//            vo.setStatus(rs.getInt(FIELD_STATUS));
//            vo.setCurrentStage(rs.getInt(FIELD_CURRENT_STAGE));
//            vo.setBonusReward(rs.getDouble(FIELD_BONUS_REWARD));
//            vo.setPublishTime(rs.getTimestamp(FIELD_PUBLISH_TIME));
//            vo.setDeadline(rs.getTimestamp(FIELD_DEADLINE));
//            vo.setUserType(rs.getInt(FIELD_USER_TYPE));

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return vo;
    }

}
