package com.restaurant.dao;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardDao {

    private final String dbPath;

    public DashboardDao(String dbPath) {
        this.dbPath = dbPath;
    }

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    //Total bookings for a specific restaurant. 
    public long getTotalBookings(int restaurantId) {
        String sql = """
            SELECT COUNT(*) AS cnt
            FROM bookings
            WHERE restaurant_id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong("cnt") : 0L;
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    //Bookings today for a specific restaurant
    public long getBookingsToday(int restaurantId) {
        String sql = """
            SELECT COUNT(*) AS cnt
            FROM bookings
            WHERE restaurant_id = ?
              AND date(booking_date) = date('now', 'localtime')
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong("cnt") : 0L;
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    //Distinct customers for this restaurant
    public long getDistinctCustomers(int restaurantId) {
        String sql = """
            SELECT COUNT(DISTINCT user_id) AS cnt
            FROM bookings
            WHERE user_id IS NOT NULL
              AND restaurant_id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong("cnt") : 0L;
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    //Status counts for one restaurant
    public Map<String, Long> getStatusCounts(int restaurantId) {
        String sql = """
            SELECT booking_status, COUNT(*) AS cnt
            FROM bookings
            WHERE restaurant_id = ?
            GROUP BY booking_status
        """;

        Map<String, Long> map = new LinkedHashMap<>();
        map.put("PENDING", 0L);
        map.put("CONFIRMED", 0L);
        map.put("SEATED", 0L);
        map.put("CANCELLED", 0L);

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("booking_status");
                    long count = rs.getLong("cnt");
                    if (status != null) {
                        map.put(status.toUpperCase(), count);
                    }
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return map;
    }
}
