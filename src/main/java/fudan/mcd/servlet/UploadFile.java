package fudan.mcd.servlet;

import fudan.mcd.utils.JSONUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@WebServlet("/UploadFile")
public class UploadFile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(UploadFile.class);
//	private static final String PATH_CONSTANT = "http://118.178.94.215:8080/MobiCrowdsourcingTest/upload/templates/";//测试值
	private static final String PATH_CONSTANT = "http://118.178.94.215:8080/MobiCrowdsourcing/upload/templates/";//运行值

	public UploadFile() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		uploadFile(request, response);
	}

	private void uploadFile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		// 获得磁盘文件条目工厂。
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// 创建临时存储目录，tmp文件夹如果不存在就创建。
		String path = request.getSession().getServletContext().getRealPath("/tmp");
		File file = new File(path);
		if (!file.exists())
			file.mkdir();
		// 设置暂时存放文件的存储室，这个存储室可以和最终存储文件的文件夹不同。因为当文件很大的话会占用过多内存所以设置存储室。
		factory.setRepository(new File(path));
		// 设置缓存的大小，当上传文件的容量超过缓存时，就放到暂时存储室。
		factory.setSizeThreshold(1024 * 1024);
		// 上传处理工具类（高水平API上传处理？）
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setHeaderEncoding("UTF-8");// 解决http报头乱码
		// 设置存储目录，如果不存在就创建
		String storeDirectory = request.getSession().getServletContext().getRealPath("/upload/templates");
		file = new File(storeDirectory);
		if (!file.exists())
			file.mkdirs();

		// 记录返回信息
		String message = "";

		try {
			// 调用 parseRequest（request）方法 获得上传文件 FileItem 的集合list 可实现多文件上传。
			List<FileItem> list = (List<FileItem>) upload.parseRequest(new ServletRequestContext(request));
			String fileName = "";
			for (FileItem item : list) {
				// 获取表单属性名字。
				String name = item.getFieldName();
				// 如果获取的表单信息是普通的文本信息。即通过页面表单形式传递来的字符串。
				if (name.equals("fileName")) {
					fileName = item.getString("UTF-8");// 解决表单输入乱码
					LOG.info(String.format("The value of file name is %s", fileName));
				}
				// 如果传入的是非简单字符串，而是图片，音频，视频等二进制文件。
				else {
					// 获取路径名
					String filename = fileName + ".xml";

					// 写到接收的文件夹中。
					File xmlFile = new File(storeDirectory, filename);
					xmlFile.createNewFile();
					OutputStream out = new FileOutputStream(xmlFile);
					InputStream in = item.getInputStream();

					int length = 0;
					byte[] buf = new byte[1024];
					while ((length = in.read(buf)) != -1) {
						out.write(buf, 0, length);
					}
					out.flush();
					in.close();
					out.close();

					// Generate response data
					ServletResponseData responseData = new ServletResponseData();
					ResponseBO responseBO = new ResponseBO();
					responseBO.setUri(PATH_CONSTANT + filename);
					// Transform the responseBO to json string and output it
					responseData.setResult(1);
					responseData.setData(JSONUtils.toJSONString(responseBO));
					response.setContentType("text/html;charset=UTF-8");
					response.getWriter().println(JSONUtils.toJSONString(responseData));
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			message = e.getMessage();
			response.getWriter().write(message);
		}
	}

	public static class ResponseBO {
		private String uri;

		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}
	}
}
