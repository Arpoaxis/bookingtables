package com.restaurant.dao;

import com.restaurant.model.RestaurantTable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class RestaurantTableDao {

    private String dbUrl;

    public RestaurantTableDao(String dbPath) {
        this.dbUrl = "jdbc:sqlite:" + dbPath;
    }

    public boolean addTable(RestaurantTable table) {
        String sql = "INSERT INTO restaurant_tables (table_number, min_capacity, max_capacity, can_combine) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, table.getTableNumber());
            ps.setInt(2, table.getMinCapacity());
            ps.setInt(3, table.getMaxCapacity());
            ps.setInt(4, table.isCanCombine() ? 1 : 0);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

