package fudan.mcd.dao.abs;

import fudan.mcd.vo.PictureOutputVO;

import javax.servlet.ServletContext;

public abstract class AbstractPictureOutputDAO extends AbstractOutputDAO<PictureOutputVO> {
	public AbstractPictureOutputDAO(ServletContext context) {
		super(context);
	}

	public AbstractPictureOutputDAO(String configPath) {
		super(configPath);
	}
}
