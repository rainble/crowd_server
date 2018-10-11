package fudan.mcd.servlet;

import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@WebServlet("/GetServerVersionServlet")
public class GetServerVersionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GetServerVersionServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		File file = new File("/usr/local/tomcat/webapps/MobiCrowdsourcing/version.txt");
		String content = "-1";
		try{
			content = FileUtils.readFileToString(file);
		}
		catch(IOException e){
			e.printStackTrace();
		}
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().println(content);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
