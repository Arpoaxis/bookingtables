package com.restaurant.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import com.restaurant.util.DatabaseUtility;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/testdb")
public class TestServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection conn = DatabaseUtility.getConnection(getServletContext())) {
            if (conn != null) {
                out.println("<h2> Database connection successful!</h2>");
            } else {
                out.println("<h2> Database connection failed!</h2>");
            }
        } catch (Exception e) {
            out.println("<h2>Error: " + e.getMessage() + "</h2>");
            e.printStackTrace(out);
        }
    }
}
