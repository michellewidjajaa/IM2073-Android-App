import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/question")
public class QuestionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");

        try (
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/quiz?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                "myuser", "xxxx");
            Statement stmt = conn.createStatement()
        ) {
            int questionNo = DisplayServlet.getCurrentQuestionNo();
            ResultSet rs = stmt.executeQuery("SELECT * FROM questions WHERE questionNo = " + questionNo);

            if (rs.next()) {
                PrintWriter out = response.getWriter();
                out.println(questionNo); 
                out.println(rs.getString("questionText"));
                out.println(rs.getString("choiceA"));
                out.println(rs.getString("choiceB"));
                out.println(rs.getString("choiceC"));
                out.println(rs.getString("choiceD"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
