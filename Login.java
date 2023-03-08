package com.journaldev.examples;
import java.io.IOException;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import com.google.gson.Gson; 

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {}
    }

    Helper helper = new Helper();

    String db_user = "root";
    String db_pwd = "Cx123!";


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        boolean success = false;
        String username = helper.removeSpecialChars(request.getParameter("username"));
        String password = request.getParameter("password");

        if (helper.validatePassword(password) == true) {

            String query = "select * from tbluser where username='" + username + "' and password = '" + password + "'";
            Connection conn = null;
            Statement stmt = null;
            try {
                conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/user", db_user, db_pwd);
                stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                if (rs.next()) {
                    success = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    stmt.close();
                    conn.close();
                } catch (Exception e) {}
            }
            if (success) {
                response.sendRedirect("home.html");
            } else {
                response.sendRedirect("login.html?error=1");
            }
        } else {
            response.sendRedirect("login.html?error=2");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        String userId = helper.removeSpecialChars(request.getParameter("userid"));

        String query = "select * from tbluser where userid='" + userId + "'";
        Connection conn = null;
        Statement stmt = null;
        Statement stmt2 = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/user", db_user, db_pwd);
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {

                String creditCardId = helper.removeSpecialChars(rs.getString("creditcardid"));

                String query2 = "select cardname, cardnumber, cvv from creditcards where creditcardid='" + creditCardId + "'";

                stmt2 = conn.createStatement();

                ResultSet rs2 = stmt.executeQuery(query2);
                if (rs2.next()) {
                   
                    String json = new Gson().toJson(rs2);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(json);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
    
    }
}
