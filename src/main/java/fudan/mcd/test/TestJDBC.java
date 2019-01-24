package fudan.mcd.test;

import java.sql.* ;
public class TestJDBC
{
    public static void main(String[] arg){
        try{ Class.forName("com.mysql.jdbc.Driver") ;
            String url ="jdbc:mysql://120.79.72.242:3306/crowdframedb?characterEncoding=utf8" ;
            Connection conn = DriverManager.getConnection(url,"root","123") ;
            System.out.println("success...") ;
        }catch(Exception e){
            System.out.println("failure!!!") ;
        }
    }
}
