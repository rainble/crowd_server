package fudan.mcd.service;

import fudan.mcd.dao.impl.*;
import fudan.mcd.servlet.AcceptTaskServlet;
import fudan.mcd.servlet.ServletUtils;
import fudan.mcd.vo.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import java.util.List;

public class ApplyTaskService extends AbstractService{

    private static final Log LOG = LogFactory.getLog(AcceptTaskService.class);
    private static final int UNDERTAKE_SUCCESS = 1, UNDERTAKE_FAIL = -1, UNDERTAKE_ALREADY_FULL = -2;
    private static final int STAGE_ONGOING = 0;
    private static final int INSERT_FAIL = -1;

    private StageDAO stageDAO = new StageDAO(context);

    public ApplyTaskService(ServletContext context) {
        super(context);
    }

    public void insertApply(ApplyVO applyVO) {
        ApplyDAO aDAO = new ApplyDAO(context);
        int result = aDAO.insert(applyVO);
        if(result > 0)
            LOG.info(String.format("User [ %d ] apply for the task [ %d ] at [ %s ] successfully!", applyVO.getUserId(), applyVO.getTaskId(), ServletUtils.getTime()));
        else
            LOG.info(String.format("User [ %d ] apply for the task [ %d ] at [ %s ] fail!", applyVO.getUserId(), applyVO.getTaskId(), ServletUtils.getTime()));

    }

    public int insertLocation(LocationVO location) {
        // DAO of the LocationVO
        LocationDAO lDAO = new LocationDAO(context);
        int locationId = lDAO.insert(location);
        if (locationId > 0)
            return locationId;
        else
            return INSERT_FAIL;
    }

    public int getCurrentStageId(int taskId, int currentStage){
        StageDAO stageDAO = new StageDAO(context);
        StageVO stageVO = stageDAO.queryByTaskAndIndex(taskId, currentStage);
        if(stageVO == null) {
            return -1;
        }
        return stageVO.getId();
    }


    public StageVO getStageInfo(int stageId) {
        StageVO stage = stageDAO.query(stageId);
        return stage;
    }

    public String getTaskTitle(int stageId) {
        String taskTitle = "This is default string value";
        StageVO stage = stageDAO.query(stageId);
        TaskDAO tDAO = new TaskDAO(context);
        if(stage != null){
            TaskVO task = tDAO.query(stage.getTaskId());
            if(task != null)
                taskTitle = task.getTitle();
        }
        return taskTitle;
    }



}
