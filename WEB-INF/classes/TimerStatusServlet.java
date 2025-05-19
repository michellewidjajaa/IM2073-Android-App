import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/status")
public class TimerStatusServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");

        long timeLeft = DisplayServlet.durationSeconds;

        if (DisplayServlet.isRunning) {
            long now = System.currentTimeMillis();
            long elapsed = (now - DisplayServlet.startMillis) / 1000;
            timeLeft = DisplayServlet.durationSeconds - elapsed;

            if (timeLeft <= 0) {
                timeLeft = 0;
                DisplayServlet.isRunning = false;
                DisplayServlet.hasTimerExpired = true;
            }
        }

        String correctAnswer = "";
        if (DisplayServlet.hasTimerExpired) {
            try (
                Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/quiz?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                    "myuser", "xxxx");
                Statement stmt = conn.createStatement()
            ) {
                ResultSet rs = stmt.executeQuery("SELECT correctAnswer FROM questions WHERE questionNo = " + DisplayServlet.currentQuestionNo);
                if (rs.next()) correctAnswer = rs.getString("correctAnswer");
            } catch (Exception e) { e.printStackTrace(); }
        }

        PrintWriter out = response.getWriter();
        out.println(DisplayServlet.isRunning);         // true or false
        out.println(timeLeft);                         // seconds remaining
        out.println(DisplayServlet.hasTimerExpired);   // true or false
        out.println(correctAnswer);                    // correct answer or empty
    }
}
