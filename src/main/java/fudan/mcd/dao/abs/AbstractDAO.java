package fudan.mcd.dao.abs;

import fudan.mcd.config.ConfigLoader;
import fudan.mcd.config.ConfigParameter;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class AbstractDAO<PK, VO> implements IDAO<PK, VO> {
	public static final String DEVELOP_CONFIG_PATH = "src/config.xml";
	public static final String DEPLOY_CONFIG_PATH = "/WEB-INF/classes/config.xml";
	private final String URL;
	private final String USER;
	private final String PASSWORD;

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public AbstractDAO(ServletContext context) {
//		this(String.format("%s/%s", context.getRealPath("/"), DEPLOY_CONFIG_PATH));
		String ip = "localhost";
		String port = "3306";
		String schema = "crowdframedb";
		URL = String.format("jdbc:mysql://%s:%s/%s?characterEncoding=utf8", ip, port, schema);
		USER = "root";
		PASSWORD = "123";
	}

	public AbstractDAO(String configPath) {
		ConfigLoader loader = ConfigLoader.getInstance(configPath);
//		String ip = loader.load(ConfigParameter.DATABASE_IP);
//		String port = loader.load(ConfigParameter.DATABASE_PORT);
//		String schema = loader.load(ConfigParameter.DATABASE_SCHEMA);
//		URL = String.format("jdbc:mysql://%s:%s/%s?characterEncoding=utf8", ip, port, schema);
//		USER = loader.load(ConfigParameter.DATABASE_ACCOUNT);
//		PASSWORD = loader.load(ConfigParameter.DATABASE_PASSWORD);
		String ip = "localhost";
		String port = "3306";
		String schema = "crowdframedb";
		URL = String.format("jdbc:mysql://%s:%s/%s?characterEncoding=utf8", ip, port, schema);
		USER = "root";
		PASSWORD = "123";
	}

	/**
	 * 获取数据库连接。在执行完后sql语句后应当及时关闭该连接。
	 * 
	 * @return 数据库连接
	 */
	protected Connection getConnection() {
		try {
			return DriverManager.getConnection(URL, USER, PASSWORD);
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
