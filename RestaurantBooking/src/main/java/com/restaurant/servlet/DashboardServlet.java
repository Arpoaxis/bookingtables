package com.restaurant.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.restaurant.util.DatabaseUtility;

@WebServlet("/admin/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1) Simple access check: only logged-in users can see this
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // 2) Ask the database for some summary numbers
        try (Connection conn = DatabaseUtility.getConnection(getServletContext())) {

            long totalBookings = runCount(conn,
                    "SELECT COUNT(*) FROM bookings");

            long bookingsToday = runCount(conn,
                    "SELECT COUNT(*) FROM bookings " +
                    "WHERE booking_date = date('now')");

            long distinctCustomers = runCount(conn,
                    "SELECT COUNT(DISTINCT user_id) " +
                    "FROM bookings " +
                    "WHERE user_id IS NOT NULL");

            // 3) Store results in the request so the JSP can use them
            req.setAttribute("totalBookings", totalBookings);
            req.setAttribute("bookingsToday", bookingsToday);
            req.setAttribute("distinctCustomers", distinctCustomers);

        } catch (SQLException e) {
            e.printStackTrace();
            // In case of error, we set a message the JSP can show
            req.setAttribute("reportError", "Unable to load dashboard statistics.");
        }

        // 4) Forward to the JSP (view)
        req.getRequestDispatcher("/WEB-INF/jsp/admin/dashboard.jsp")
           .forward(req, resp);
    }

    /**
     * Helper method to execute a simple SELECT COUNT(*) ... query.
     */
    private long runCount(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        }
    }
}
