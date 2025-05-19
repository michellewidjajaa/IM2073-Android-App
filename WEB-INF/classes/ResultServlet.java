import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/result")
public class ResultServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        if (email == null || email.trim().isEmpty()) {
            out.println("error: email is missing");
            return;
        }

        try (
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/quiz?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                "myuser", "xxxx");
            Statement stmt = conn.createStatement()
        ) {
            // Get user's total score
            String userScoreSQL = "SELECT SUM(isCorrect) as score FROM responses WHERE email = ?";
            PreparedStatement userStmt = conn.prepareStatement(userScoreSQL);
            userStmt.setString(1, email);
            ResultSet userRs = userStmt.executeQuery();

            int userScore = 0;
            if (userRs.next()) {
                userScore = userRs.getInt("score");
            }

            // Get rank
            String rankSQL = "SELECT email, SUM(isCorrect) as score FROM responses GROUP BY email ORDER BY score DESC";
            ResultSet rankRs = stmt.executeQuery(rankSQL);

            int rank = 1;
            while (rankRs.next()) {
                String e = rankRs.getString("email");
                if (e.equals(email)) break;
                rank++;
            }

            out.println("score:" + userScore);
            out.println("rank:" + rank);

        } catch (Exception e) {
            e.printStackTrace();
            out.println("error: " + e.getMessage());
        }
    }
}