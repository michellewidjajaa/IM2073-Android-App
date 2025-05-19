import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        try (
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/quiz?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                "myuser", "xxxx");

            PreparedStatement stmt = conn.prepareStatement(
                "SELECT username FROM users WHERE email = ? AND password = ?")
        ) {
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String username = rs.getString("username");
                out.println("success:" + username);  // Important: matches Android expectations
            } else {
                out.println("fail: invalid credentials");
            }

        } catch (Exception e) {
            out.println("fail: error - " + e.getMessage());
            e.printStackTrace(out);
        }

        out.close();
    }
}
