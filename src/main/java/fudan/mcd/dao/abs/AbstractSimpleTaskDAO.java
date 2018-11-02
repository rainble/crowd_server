package fudan.mcd.dao.abs;

import fudan.mcd.vo.SimpleTaskVO;

import javax.servlet.ServletContext;
import java.util.List;

public abstract class AbstractSimpleTaskDAO extends AbstractDAO<Integer, SimpleTaskVO> {
    public AbstractSimpleTaskDAO(ServletContext context) {
        super(context);
    }

    public AbstractSimpleTaskDAO(String configPath) {
        super(configPath);
    }

    public abstract List<SimpleTaskVO> queryTaskListByUser(int userId);

}