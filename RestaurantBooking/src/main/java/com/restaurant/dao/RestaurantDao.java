package com.restaurant.dao;

import com.restaurant.model.Restaurant;
import com.restaurant.util.DatabaseUtility;
import jakarta.servlet.ServletContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantDao {

    // 1) LIST ALL RESTAURANTS
    public static List<Restaurant> findAll(ServletContext ctx) throws SQLException {
        List<Restaurant> list = new ArrayList<>();

        String sql = """
            SELECT restaurant_id, name, address, phone, description
            FROM restaurants
            ORDER BY name
        """;

        try (Connection conn = DatabaseUtility.getConnection(ctx);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Restaurant r = new Restaurant();
                r.setRestaurantId(rs.getInt("restaurant_id"));
                r.setName(rs.getString("name"));
                r.setAddress(rs.getString("address"));
                r.setPhone(rs.getString("phone"));
                r.setDescription(rs.getString("description"));
                list.add(r);
            }
        }

        return list;
    }


    // 2) FIND RESTAURANT BY ID
    public static Restaurant findById(ServletContext ctx, int id) throws SQLException {
        Restaurant r = null;

        String sql = """
            SELECT restaurant_id, name, address, phone, description
            FROM restaurants
            WHERE restaurant_id = ?
        """;

        try (Connection conn = DatabaseUtility.getConnection(ctx);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    r = new Restaurant();
                    r.setRestaurantId(rs.getInt("restaurant_id"));
                    r.setName(rs.getString("name"));
                    r.setAddress(rs.getString("address"));
                    r.setPhone(rs.getString("phone"));
                    r.setDescription(rs.getString("description"));
                }
            }
        }

        return r;
    }


    // 3) SEARCH RESTAURANTS
    public static List<Restaurant> search(ServletContext ctx, String query) throws SQLException {
        List<Restaurant> list = new ArrayList<>();

        String sql = """
            SELECT restaurant_id, name, address, phone, description
            FROM restaurants
            WHERE name LIKE ?
               OR address LIKE ?
               OR description LIKE ?
            ORDER BY name
        """;

        try (Connection conn = DatabaseUtility.getConnection(ctx);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String q = "%" + query + "%";
            ps.setString(1, q);
            ps.setString(2, q);
            ps.setString(3, q);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Restaurant r = new Restaurant();
                    r.setRestaurantId(rs.getInt("restaurant_id"));
                    r.setName(rs.getString("name"));
                    r.setAddress(rs.getString("address"));
                    r.setPhone(rs.getString("phone"));
                    r.setDescription(rs.getString("description"));
                    list.add(r);
                }
                
            }
        }

        return list;
    }
    
}
