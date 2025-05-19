import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/select")
public class SelectServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        String choice = request.getParameter("choice");
        String email = request.getParameter("email");

        if (choice == null || email == null || choice.isEmpty() || email.isEmpty()) {
            out.println("fail: missing data");
            return;
        }

        int questionNo = DisplayServlet.getCurrentQuestionNo();
        boolean isCorrect = false;

        try (
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/quiz?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                "myuser", "xxxx");
        ) {
            String correctAnswer = "";
            PreparedStatement correctStmt = conn.prepareStatement(
                "SELECT correctAnswer FROM questions WHERE questionNo = ?");
            correctStmt.setInt(1, questionNo);
            ResultSet rs = correctStmt.executeQuery();
            if (rs.next()) {
                correctAnswer = rs.getString("correctAnswer");
                isCorrect = correctAnswer.equalsIgnoreCase(choice);
            }

            PreparedStatement insertStmt = conn.prepareStatement(
                "INSERT INTO responses (questionNo, choice, email, isCorrect) VALUES (?, ?, ?, ?)");
            insertStmt.setInt(1, questionNo);
            insertStmt.setString(2, choice.toLowerCase());
            insertStmt.setString(3, email.toLowerCase());
            insertStmt.setBoolean(4, isCorrect);

            int affected = insertStmt.executeUpdate();
            if (affected > 0) {
                out.println("success");
            } else {
                out.println("fail: insert error");
            }

        } catch (Exception e) {
            out.println("fail: " + e.getMessage());
            e.printStackTrace(out);
        }

        out.close();
    }
}