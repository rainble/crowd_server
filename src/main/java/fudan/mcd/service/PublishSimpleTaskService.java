package fudan.mcd.service;

import fudan.mcd.dao.impl.SimpleTaskDAO;
import fudan.mcd.vo.SimpleTaskVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import java.util.List;

public class PublishSimpleTaskService extends AbstractService {
    private static final Log LOG = LogFactory.getLog(PublishSimpleTaskService.class);
    private static final int SUCCESS = 1;
    private static final int FAIL = -1;




    public PublishSimpleTaskService(ServletContext context) {
        super(context);
    }

    public Integer insertTask(SimpleTaskVO simpleTaskVO) {
        SimpleTaskDAO simpleTaskDAO = new SimpleTaskDAO(context);
        Integer integer = simpleTaskDAO.insert(simpleTaskVO);
        if (integer == 1) {
            LOG.info(String.format("User [ %d ] publish a simple task [ %d ] at [ %s ] successfully!", simpleTaskVO.getUserId(), simpleTaskVO.getTaskId(), simpleTaskVO.getPublishTime()));
            return SUCCESS;
        } else {
            return integer;
        }
    }

    public int deleteSimpleTask(int simTaskId) {
        SimpleTaskDAO simpleTaskDAO = new SimpleTaskDAO(context);
        simpleTaskDAO.delete(simTaskId);
        if (simpleTaskDAO.delete(simTaskId) != null) {
            return SUCCESS;
        } else {
            return FAIL;
        }

    }

    public List<SimpleTaskVO> querySimpleTaskByUser(int userId) {
        SimpleTaskDAO simpleTaskDAO = new SimpleTaskDAO(context);
        return simpleTaskDAO.queryTaskListByUser(userId);
    }



}