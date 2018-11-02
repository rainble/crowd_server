package fudan.mcd.dao.abs;

import fudan.mcd.vo.SimpleTaskVO;

import javax.servlet.ServletContext;
import java.util.List;

public abstract class AbstractAccepetSimpleTaskDAO extends AbstractDAO {

    public AbstractAccepetSimpleTaskDAO(ServletContext context) {
        super(context);
    }

    public AbstractAccepetSimpleTaskDAO(String configPath) {
        super(configPath);
    }

    public abstract List<SimpleTaskVO> queryAcceptTaskListByUser(int userId);

    public abstract List<SimpleTaskVO> queryCompleteTaskListByUser(int userId);

}
