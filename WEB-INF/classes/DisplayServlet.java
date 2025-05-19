import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/display")
public class DisplayServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public static int currentQuestionNo = 1;
    public static boolean isRunning = false;
    public static boolean hasTimerExpired = false;
    public static long startMillis = 0;
    public static final int durationSeconds = 15;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long timeLeft = durationSeconds;

        if (isRunning) {
            long now = System.currentTimeMillis();
            long elapsed = (now - startMillis) / 1000;
            timeLeft = durationSeconds - elapsed;

            if (timeLeft <= 0) {
                timeLeft = 0;
                isRunning = false;
                hasTimerExpired = true;
            }
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Quiz Display</title>");
        out.println("<link rel='stylesheet' href='css/styleDisplay.css'>");
        out.println("<script src='https://cdn.jsdelivr.net/npm/chart.js'></script>");
        out.println("</head><body>");
        out.println("<div class='container'>");

        try (
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/quiz?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                "myuser", "xxxx");
            Statement stmt = conn.createStatement();
        ) {
            String questionSQL = "SELECT * FROM questions WHERE questionNo = " + currentQuestionNo;
            ResultSet qResult = stmt.executeQuery(questionSQL);

            if (qResult.next()) {
                String questionText = qResult.getString("questionText");
                String a = qResult.getString("choiceA");
                String b = qResult.getString("choiceB");
                String c = qResult.getString("choiceC");
                String d = qResult.getString("choiceD");
                String correct = qResult.getString("correctAnswer");

                out.println("<h2>Question " + currentQuestionNo + ": " + questionText + "</h2>");
                out.println("<ul class='choices'>");
                out.println("<li>A. " + a + "</li>");
                out.println("<li>B. " + b + "</li>");
                out.println("<li>C. " + c + "</li>");
                out.println("<li>D. " + d + "</li>");
                out.println("</ul>");

                out.println("<div class='controls'>");
                out.println("<form method='post'>");
                out.println("<button class='btn' name='action' value='start'>Start</button>");
                out.println("<button class='btn' name='action' value='stop'>Stop</button>");
                out.println("<button class='btn' name='action' value='next'>Next Question</button>");
                out.println("<button class='btn' name='action' value='restart'>Restart</button>");
                out.println("</form>");
                out.println("</div>");

                out.println("<div class='status'>");
                out.println("<strong>" + (isRunning ? "Running." : "Stopped.") + "</strong>");
                out.println("<p>" + (isRunning ? "Accepting responses..." : "Responses closed.") + "</p>");
                if (hasTimerExpired) {
                    out.println("<div class='correct-answer-display'>");
                    out.println("<span class='correct-answer-text'>Correct Answer: " + correct.toUpperCase() + "</span>");
                    out.println("</div>");
                }
                out.println("<h3 id='timerDisplay'>Time left: " + timeLeft + "s</h3>");
                out.println("</div>");

                if (hasTimerExpired) {
                    out.println("<h3>Statistics</h3>");
                    out.println("<canvas id='barChart' width='400' height='300'></canvas>");

                    int[] counts = new int[4];
                    String statSQL = "SELECT choice, COUNT(*) as count FROM responses WHERE questionNo = " + currentQuestionNo + " GROUP BY choice";
                    ResultSet stats = stmt.executeQuery(statSQL);
                    while (stats.next()) {
                        String choice = stats.getString("choice");
                        int count = stats.getInt("count");
                        switch (choice.toLowerCase()) {
                            case "a": counts[0] = count; break;
                            case "b": counts[1] = count; break;
                            case "c": counts[2] = count; break;
                            case "d": counts[3] = count; break;
                        }
                    }

                    out.println("<script>");
                    out.println("const ctx = document.getElementById('barChart').getContext('2d');");
                    out.println("const barChart = new Chart(ctx, {");
                    out.println("    type: 'bar',");
                    out.println("    data: {");
                    out.println("        labels: ['A', 'B', 'C', 'D'],");
                    out.println("        datasets: [{");
                    out.println("            label: 'Response Count',");
                    out.println("            data: [" + counts[0] + ", " + counts[1] + ", " + counts[2] + ", " + counts[3] + "],");
                    out.println("            backgroundColor: ['#ff4d4d', '#4da6ff', '#85e085', '#ffe066']");
                    out.println("        }]");
                    out.println("    },");
                    out.println("    options: {");
                    out.println("        scales: {");
                    out.println("            y: {");
                    out.println("                beginAtZero: true,");
                    out.println("                ticks: {");
                    out.println("                    precision: 0");
                    out.println("                }");
                    out.println("            }");
                    out.println("        }");
                    out.println("    }");
                    out.println("});");
                    out.println("</script>");
                }

                out.println("<script>");
                out.println("let accepting = " + isRunning + ";");
                out.println("let timeLeft = " + timeLeft + ";");
                out.println("let countdown;");
                out.println("function startTimer() {");
                out.println("  if (!accepting) return;");
                out.println("  countdown = setInterval(() => {");
                out.println("    if (timeLeft <= 0) {");
                out.println("      stopResponses();");
                out.println("    } else {");
                out.println("      document.getElementById('timerDisplay').innerText = 'Time left: ' + timeLeft + 's';");
                out.println("      timeLeft--;");
                out.println("    }");
                out.println("  }, 1000);");
                out.println("}");
                out.println("function stopResponses() {");
                out.println("  accepting = false;");
                out.println("  clearInterval(countdown);");
                out.println("  setTimeout(() => { location.reload(); }, 1000);");
                out.println("}");
                out.println("window.onload = startTimer;");
                out.println("</script>");
            } 
            
            ResultSet maxQResult = stmt.executeQuery("SELECT MAX(questionNo) FROM questions");
            if (maxQResult.next()) {
                int maxQ = maxQResult.getInt(1);
                if (currentQuestionNo == maxQ) {
                    out.println("<div class='result-button'>");
                    out.println("<form action='result-dashboard' method='get'>");
                    out.println("<button class='btn'>Go to Result Page</button>");
                    out.println("</form>");
                    out.println("</div>");
                }
            }


        } catch (Exception e) {
            out.println("<p>Error: " + e.getMessage() + "</p>");
            e.printStackTrace(out);
        }

        out.println("</div></body></html>");
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        switch (action) {
            case "start":
                isRunning = true;
                hasTimerExpired = false;
                startMillis = System.currentTimeMillis();
                break;
            case "stop":
                isRunning = false;
                hasTimerExpired = true;
                break;
            case "next":
                currentQuestionNo++;
                isRunning = false;
                hasTimerExpired = false;
                break;
            case "restart":
                currentQuestionNo = 1;
                isRunning = false;
                hasTimerExpired = false;
                break;
        }

        response.sendRedirect("display");
    }

    public static int getCurrentQuestionNo() {
        return currentQuestionNo;
    }
}
