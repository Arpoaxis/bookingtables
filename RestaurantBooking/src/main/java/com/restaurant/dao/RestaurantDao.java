package com.restaurant.dao;

import com.restaurant.model.Restaurant;
import com.restaurant.util.DatabaseUtility;
import jakarta.servlet.ServletContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantDao {

    public static List<Restaurant> findAll(ServletContext ctx) throws SQLException {
        List<Restaurant> list = new ArrayList<>();
        try (Connection conn = DatabaseUtility.getConnection(ctx);
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT restaurant_id, name, address, phone, description FROM restaurants ORDER BY name")) {

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

    public static Restaurant findById(ServletContext ctx, int id) throws SQLException {
        Restaurant r = null;
        try (Connection conn = DatabaseUtility.getConnection(ctx);
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT restaurant_id, name, address, phone, description FROM restaurants WHERE restaurant_id=?")) {

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
}
