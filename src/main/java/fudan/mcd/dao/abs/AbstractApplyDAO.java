package fudan.mcd.dao.abs;

import fudan.mcd.vo.ApplyVO;

import javax.servlet.ServletContext;

public abstract class AbstractApplyDAO extends AbstractDAO<Integer, ApplyVO> {

    public AbstractApplyDAO(ServletContext context) {
        super(context);
    }

    public AbstractApplyDAO(String configPath) {
        super(configPath);
    }




}
