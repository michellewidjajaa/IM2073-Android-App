import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try (
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/quiz?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                "myuser", "xxxx");
            PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM users WHERE email=? OR username=?");
            PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO users (email, username, password) VALUES (?, ?, ?)");
        ) {
            checkStmt.setString(1, email);
            checkStmt.setString(2, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                out.println("fail: email or username already exists");
            } else {
                insertStmt.setString(1, email);
                insertStmt.setString(2, username);
                insertStmt.setString(3, password);
                insertStmt.executeUpdate();
                out.println("success");
            }

        } catch (Exception e) {
            e.printStackTrace(out);
            out.println("fail: error occurred");
        }
    }
}