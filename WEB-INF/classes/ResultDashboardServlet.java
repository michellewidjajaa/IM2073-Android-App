import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/result-dashboard")
public class ResultDashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html><html><head><title>Leaderboard</title>");
        out.println("<link rel='stylesheet' href='css/styleResultDashboard.css'>");
        out.println("</head><body><div class='container'>");
        out.println("<h2>🏆 Quiz Leaderboard</h2>");

        try (
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/quiz?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                "myuser", "xxxx");
            Statement stmt = conn.createStatement()
        ) {
            String sql = """
                SELECT u.username, COUNT(*) AS score
                FROM responses r
                JOIN users u ON r.email = u.email
                WHERE r.isCorrect = 1
                GROUP BY u.username
                ORDER BY score DESC
            """;

            ResultSet rs = stmt.executeQuery(sql);

            out.println("<table>");
            out.println("<tr><th>Rank</th><th>Username</th><th>Score</th></tr>");

            int rank = 1;
            while (rs.next()) {
                String username = rs.getString("username");
                int score = rs.getInt("score");

                if (username == null || username.trim().isEmpty()) continue;

                out.println("<tr>");
                out.println("<td>" + rank + "</td>");
                out.println("<td>" + username + "</td>");
                out.println("<td>" + score + "</td>");
                out.println("</tr>");
                rank++;
            }

            out.println("</table>");

        } catch (Exception e) {
            out.println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
            e.printStackTrace(out);
        }

        out.println("<div class='controls'>");
        out.println("<form action='display' method='get'>");
        out.println("<button class='btn'>⬅ Back to Question</button>");
        out.println("</form></div>");

        out.println("</div></body></html>");
        out.close();
    }
}
