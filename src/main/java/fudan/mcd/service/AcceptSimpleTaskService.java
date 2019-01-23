package fudan.mcd.service;


import fudan.mcd.dao.impl.AcceptSimpleTaskDAO;
import fudan.mcd.vo.SimpleTaskVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import java.util.List;

public class AcceptSimpleTaskService extends AbstractService {

    private static final Log LOG = LogFactory.getLog(AcceptTaskService.class);

    public AcceptSimpleTaskService(ServletContext context) {
        super(context);
    }

    public int insert_task(int userId, int taskId) {
        AcceptSimpleTaskDAO acceptSimpleTaskDAO = new AcceptSimpleTaskDAO(context);
        int res = acceptSimpleTaskDAO.insert(userId, taskId);
        if (res != -3) {
            LOG.info(String.format("SimpltTask %d has been accepted successfully by %d.", taskId, userId));
            return res;
        } else {
            LOG.info(String.format("SimpltTask %d has been accepted abortively by %d.", taskId, userId));
            return res;
        }
    }


    public int complete_task(int userId, int taskId) {
        AcceptSimpleTaskDAO dao = new AcceptSimpleTaskDAO(context);
        return dao.complete(userId, taskId);
    }

    public List<SimpleTaskVO> queryAcceptSimpleTaskListByUser(int userId) {
        AcceptSimpleTaskDAO dao = new AcceptSimpleTaskDAO(context);
        return dao.queryAcceptTaskListByUser(userId);
    }

    public List<SimpleTaskVO> queryCompleteSimpleTaskListByUser(int userId) {
        AcceptSimpleTaskDAO dao = new AcceptSimpleTaskDAO(context);
        return dao.queryCompleteTaskListByUser(userId);
    }



}
