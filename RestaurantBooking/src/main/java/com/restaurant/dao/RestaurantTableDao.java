package com.restaurant.dao;

import com.restaurant.model.RestaurantTable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantTableDao {

    private final String dbPath;

    public RestaurantTableDao(String dbPath) {
        this.dbPath = dbPath;
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:sqlite:" + dbPath;
        return DriverManager.getConnection(url);
    }

    public boolean addTable(RestaurantTable table) {
        String checkSql = "SELECT COUNT(*) FROM restaurant_tables WHERE table_number = ?";
        String insertSql = """
            INSERT INTO restaurant_tables (table_number, min_capacity, max_capacity, can_combine)
            VALUES (?,?,?,?)
        """;

        try (Connection conn = getConnection()) {
            // check duplicate number
            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setInt(1, table.getTableNumber());
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return false;
                    }
                }
            }

            // insert new
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, table.getTableNumber());
                ps.setInt(2, table.getMinCapacity());
                ps.setInt(3, table.getMaxCapacity());
                ps.setInt(4, table.isCanCombine() ? 1 : 0);
                ps.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Load a single table by ID
    public RestaurantTable getTableById(int tableId) {
        String sql = """
            SELECT table_id, table_number, min_capacity, max_capacity, can_combine
            FROM restaurant_tables
            WHERE table_id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tableId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id          = rs.getInt("table_id");
                    int tableNumber = rs.getInt("table_number");
                    int minCap      = rs.getInt("min_capacity");
                    int maxCap      = rs.getInt("max_capacity");
                    int canCombInt  = rs.getInt("can_combine");

                    boolean canCombine = (canCombInt == 1);

                    return new RestaurantTable(
                            id,
                            tableNumber,
                            minCap,
                            maxCap,
                            canCombine
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // not found or error
    }

    // Update table info
    public boolean updateTable(RestaurantTable table) {
        String sql = """
            UPDATE restaurant_tables
            SET table_number = ?,
                min_capacity = ?,
                max_capacity = ?,
                can_combine  = ?
            WHERE table_id   = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, table.getTableNumber());
            ps.setInt(2, table.getMinCapacity());
            ps.setInt(3, table.getMaxCapacity());
            ps.setInt(4, table.isCanCombine() ? 1 : 0);
            ps.setInt(5, table.getTableId());

            int rows = ps.executeUpdate();
            return rows == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<RestaurantTable> getAllTables() {
        String sql = """
            SELECT table_id, table_number, min_capacity, max_capacity, can_combine
            FROM restaurant_tables
            ORDER BY table_number
        """;

        List<RestaurantTable> result = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id          = rs.getInt("table_id");
                int tableNumber = rs.getInt("table_number");
                int minCap      = rs.getInt("min_capacity");
                int maxCap      = rs.getInt("max_capacity");
                boolean canComb = rs.getInt("can_combine") == 1;

                result.add(new RestaurantTable(id, tableNumber, minCap, maxCap, canComb));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}
