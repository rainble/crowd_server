package fudan.mcd.service;

import fudan.mcd.dao.impl.CollectionDAO;
import fudan.mcd.dao.impl.UserDAO;
import fudan.mcd.servlet.ServletUtils;
import fudan.mcd.vo.TemplateVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class GetTemplateCollectionService extends AbstractService {
	private static final Log LOG = LogFactory.getLog(GetTemplateCollectionService.class);

	public GetTemplateCollectionService(ServletContext context) {
		super(context);
	}

	public List<ResultVO> getTemplateCollection(int userId) {
		LOG.info(String.format("User [ %d ] request the collected template list at [ %s ].", userId, ServletUtils.getTime()));
		List<ResultVO> result = new ArrayList<ResultVO>();

		// The DAO of Collect and Template
		CollectionDAO cDAO = new CollectionDAO(context);
		UserDAO uDAO = new UserDAO(context);

		// Get the info of collected template
		List<TemplateVO> tpvoList = cDAO.queryTemplateListByUser(userId);
		LOG.info(String.format("The number of user [ %d ] collected template is [ %d ] at [ %s ].", userId, tpvoList.size(), ServletUtils.getTime()));
		for (TemplateVO tpvo : tpvoList) {
			ResultVO rvo = new ResultVO();
			rvo.setId(tpvo.getId());
			rvo.setUserId(tpvo.getUserId());
			rvo.setName(tpvo.getName());
			rvo.setDescription(tpvo.getDescription());
			rvo.setHeat(tpvo.getHeat());
			rvo.setUri(tpvo.getUri());
			rvo.setCreateTime(tpvo.getCreateTime());
			rvo.setCreater(uDAO.query(tpvo.getUserId()).getAccount());
			rvo.setTotalStageNum(tpvo.getTotalStageNum());

			result.add(rvo);
		}

		return result;
	}

	public static class ResultVO {
		private int id;
		private int userId;
		private String name;
		private String description;
		private int heat;
		private String uri;
		private Timestamp createTime;
		private String creater;
		private int totalStageNum;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public int getHeat() {
			return heat;
		}

		public void setHeat(int heat) {
			this.heat = heat;
		}

		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

		public Timestamp getCreateTime() {
			return createTime;
		}

		public void setCreateTime(Timestamp createTime) {
			this.createTime = createTime;
		}

		public String getCreater() {
			return creater;
		}

		public void setCreater(String creater) {
			this.creater = creater;
		}

		public int getTotalStageNum() {
			return totalStageNum;
		}

		public void setTotalStageNum(int totalStageNum) {
			this.totalStageNum = totalStageNum;
		}

	}
}
