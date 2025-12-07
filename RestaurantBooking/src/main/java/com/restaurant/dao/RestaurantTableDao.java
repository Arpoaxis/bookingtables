package com.restaurant.dao;

import com.restaurant.model.RestaurantTable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalTime;
public class RestaurantTableDao {

    private final String dbPath;

    public RestaurantTableDao(String dbPath) {
        this.dbPath = dbPath;
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:sqlite:" + dbPath;
        return DriverManager.getConnection(url);
    }


    // INSERT TABLE 
    public boolean addTable(RestaurantTable table) {

        String checkSql = """
            SELECT COUNT(*) 
            FROM restaurant_tables 
            WHERE restaurant_id = ? AND table_number = ?
        """;

        String insertSql = """
            INSERT INTO restaurant_tables
            (restaurant_id, table_number, min_capacity, max_capacity, can_combine)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = getConnection()) {

            // Duplicate check (PER RESTAURANT!)
            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setInt(1, table.getRestaurantId());
                check.setInt(2, table.getTableNumber());
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return false;
                    }
                }
            }

            // Perform insert
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, table.getRestaurantId());
                ps.setInt(2, table.getTableNumber());
                ps.setInt(3, table.getMinCapacity());
                ps.setInt(4, table.getMaxCapacity());
                ps.setInt(5, table.isCanCombine() ? 1 : 0);
                ps.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE TABLE
    public boolean deleteTable(int tableId) {
        String sql = "DELETE FROM restaurant_tables WHERE table_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tableId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // GET TABLE BY ID
    public RestaurantTable getTableById(int tableId) {
        String sql = """
            SELECT table_id, restaurant_id, table_number, min_capacity, max_capacity, can_combine
            FROM restaurant_tables
            WHERE table_id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tableId);
            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new RestaurantTable(
                        rs.getInt("table_id"),
                        rs.getInt("restaurant_id"),
                        rs.getInt("table_number"),
                        rs.getInt("min_capacity"),
                        rs.getInt("max_capacity"),
                        rs.getInt("can_combine") == 1
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // GET TABLES BY RESTAURANT
    public List<RestaurantTable> getTablesByRestaurant(int restaurantId) {
        String sql = """
            SELECT table_id, restaurant_id, table_number, min_capacity, max_capacity, can_combine
            FROM restaurant_tables
            WHERE restaurant_id = ?
            ORDER BY table_number
        """;

        List<RestaurantTable> result = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, restaurantId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new RestaurantTable(
                        rs.getInt("table_id"),
                        rs.getInt("restaurant_id"),
                        rs.getInt("table_number"),
                        rs.getInt("min_capacity"),
                        rs.getInt("max_capacity"),
                        rs.getInt("can_combine") == 1
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }


    // GET ALL TABLES
    public List<RestaurantTable> getAllTables() {
        String sql = """
            SELECT table_id, restaurant_id, table_number, min_capacity, max_capacity, can_combine
            FROM restaurant_tables
            ORDER BY restaurant_id, table_number
        """;

        List<RestaurantTable> result = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(new RestaurantTable(
                    rs.getInt("table_id"),
                    rs.getInt("restaurant_id"),
                    rs.getInt("table_number"),
                    rs.getInt("min_capacity"),
                    rs.getInt("max_capacity"),
                    rs.getInt("can_combine") == 1
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    // UPDATE TABLE
    public boolean updateTable(RestaurantTable table) {
        String sql = """
            UPDATE restaurant_tables
            SET table_number = ?,
                min_capacity = ?,
                max_capacity = ?,
                can_combine  = ?
            WHERE table_id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, table.getTableNumber());
            ps.setInt(2, table.getMinCapacity());
            ps.setInt(3, table.getMaxCapacity());
            ps.setInt(4, table.isCanCombine() ? 1 : 0);
            ps.setInt(5, table.getTableId());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // REINSERT TABLE
    public boolean reinsertTable(RestaurantTable t) {
        String sql = """
            INSERT INTO restaurant_tables 
            (restaurant_id, table_number, min_capacity, max_capacity, can_combine)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, t.getRestaurantId());
            ps.setInt(2, t.getTableNumber());
            ps.setInt(3, t.getMinCapacity());
            ps.setInt(4, t.getMaxCapacity());
            ps.setInt(5, t.isCanCombine() ? 1 : 0);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
 // get available tables for a specific restaurant
    public List<RestaurantTable> getAvailableTables(int restaurantId, String date, String time, int guests) {

        // Fix: ensure seconds always included (HH:mm:ss)
        LocalTime start = LocalTime.parse(time.length() == 5 ? time + ":00" : time);
        LocalTime end   = start.plusHours(1);
        String startTime = start.toString(); // Always HH:mm:ss
        String endTime   = end.toString();   // Always HH:mm:ss

        System.out.println("=== Checking available tables ===");
        System.out.println("Restaurant ID: " + restaurantId);
        System.out.println("Date: " + date);
        System.out.println("Start: " + startTime);
        System.out.println("End: " + endTime);
        System.out.println("Guests: " + guests);

        String sql = """
            SELECT rt.*
            FROM restaurant_tables rt
            WHERE rt.restaurant_id = ?
              AND rt.min_capacity <= ?
              AND rt.max_capacity >= ?
              AND rt.table_id NOT IN (
                    SELECT bt.table_id
                    FROM booking_tables bt
                    JOIN bookings b ON bt.booking_id = b.booking_id
                    WHERE b.restaurant_id = ?
                      AND b.booking_date = ?
                      AND time(b.booking_time) < time(?)
                      AND time(b.booking_time, '+1 hour') > time(?)
                      AND b.booking_status <> 'CANCELLED'
              )
            ORDER BY rt.table_number
        """;

        List<RestaurantTable> result = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int i = 1;
            ps.setInt(i++, restaurantId);
            ps.setInt(i++, guests);      // min_capacity <= guests
            ps.setInt(i++, guests);      // max_capacity >= guests

            ps.setInt(i++, restaurantId);
            ps.setString(i++, date);

            ps.setString(i++, endTime);   // existing booking starts before new end
            ps.setString(i++, startTime); // existing booking ends after new start

            System.out.println("SQL parameters set, executing query…");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new RestaurantTable(
                        rs.getInt("table_id"),
                        rs.getInt("restaurant_id"),
                        rs.getInt("table_number"),
                        rs.getInt("min_capacity"),
                        rs.getInt("max_capacity"),
                        rs.getInt("can_combine") == 1
                    ));
                }
            }

            System.out.println("Tables found: " + result.size());
        }
        catch (SQLException e) {
            System.err.println("SQL ERROR in getAvailableTables():");
            e.printStackTrace();
        }

        return result;
    }
    }
