package com.restaurant.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Map;
import java.util.LinkedHashMap;

import com.restaurant.util.DatabaseUtility;

@WebServlet("/admin/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        Integer restaurantId = (Integer) session.getAttribute("restaurantId");
        
        try (Connection conn = DatabaseUtility.getConnection(getServletContext())) {

            // total bookings for this restaurant (or all if null)
            String totalSql = (restaurantId != null)
                ? "SELECT COUNT(*) FROM bookings WHERE restaurant_id = ?"
                : "SELECT COUNT(*) FROM bookings";
            long totalBookings = runCount(conn, totalSql, restaurantId);

            // bookings today for this restaurant
            String todaySql = (restaurantId != null)
                ? "SELECT COUNT(*) FROM bookings WHERE booking_date = date('now') AND restaurant_id = ?"
                : "SELECT COUNT(*) FROM bookings WHERE booking_date = date('now')";
            long bookingsToday = runCount(conn, todaySql, restaurantId);

            // distinct customers
            String distinctSql = (restaurantId != null)
                ? "SELECT COUNT(DISTINCT user_id) FROM bookings WHERE user_id IS NOT NULL AND restaurant_id = ?"
                : "SELECT COUNT(DISTINCT user_id) FROM bookings WHERE user_id IS NOT NULL";
            long distinctCustomers = runCount(conn, distinctSql, restaurantId);

            // status counts
            Map<String, Long> statusCounts = loadStatusCounts(conn, restaurantId);

            req.setAttribute("totalBookings", totalBookings);
            req.setAttribute("bookingsToday", bookingsToday);
            req.setAttribute("distinctCustomers", distinctCustomers);
            req.setAttribute("statusCounts", statusCounts);

        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("reportError", "Unable to load dashboard statistics.");
        }

        req.getRequestDispatcher("/WEB-INF/jsp/admin/dashboard.jsp")
           .forward(req, resp);
    }


    /**
     * Helper method to execute a simple SELECT COUNT(*) ... query.
     */
    private long runCount(Connection conn, String sql, Integer restaurantId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (restaurantId != null) {
                ps.setInt(1, restaurantId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        }
    }

    /**
     * Load counts of bookings grouped by status.
     * Ensures we always return all four known statuses,
     * even if some are zero.
     */
    private Map<String, Long> loadStatusCounts(Connection conn, Integer restaurantId) throws SQLException {
        String sql;
        if (restaurantId != null) {
            sql = """
                  SELECT booking_status, COUNT(*)
                  FROM bookings
                  WHERE restaurant_id = ?
                  GROUP BY booking_status
                  """;
        } else {
            sql = """
                  SELECT booking_status, COUNT(*)
                  FROM bookings
                  GROUP BY booking_status
                  """;
        }

        Map<String, Long> result = new LinkedHashMap<>();
        result.put("PENDING",   0L);
        result.put("CONFIRMED", 0L);
        result.put("SEATED",    0L);
        result.put("CANCELLED", 0L);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (restaurantId != null) {
                ps.setInt(1, restaurantId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString(1);
                    long count    = rs.getLong(2);
                    if (status != null) {
                        status = status.toUpperCase();
                        result.put(status, count);
                    }
                }
            }
        }
        return result;
    }


}
