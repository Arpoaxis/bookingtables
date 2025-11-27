package com.restaurant.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardDao {

    private final String dbPath;

    public DashboardDao(String dbPath) {
        this.dbPath = dbPath;
    }

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        String url = "jdbc:sqlite:" + dbPath;
        return DriverManager.getConnection(url);
    }

    public long getTotalBookings() {
        String sql = "SELECT COUNT(*) AS cnt FROM bookings";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getLong("cnt");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /** Count bookings where booking_date is “today” (SQLite local date). */
    public long getBookingsToday() {
        String sql = """
            SELECT COUNT(*) AS cnt
            FROM bookings
            WHERE date(booking_date) = date('now','localtime')
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getLong("cnt");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public long getDistinctCustomers() {
        String sql = "SELECT COUNT(DISTINCT user_id) AS cnt FROM bookings";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getLong("cnt");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /** Map of STATUS -> COUNT, e.g. PENDING -> 16, CONFIRMED -> 83, ... */
    public Map<String, Long> getStatusCounts() {
        String sql = """
            SELECT booking_status, COUNT(*) AS cnt
            FROM bookings
            GROUP BY booking_status
        """;

        Map<String, Long> result = new LinkedHashMap<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String status = rs.getString("booking_status");
                long count = rs.getLong("cnt");
                result.put(status, count);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
}
