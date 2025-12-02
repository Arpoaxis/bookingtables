package com.restaurant.dao;

import com.restaurant.model.User;
import java.sql.*;

public class UserDao {

    private final String dbPath;

    public UserDao(String dbPath) {
        this.dbPath = dbPath;
    }

    private Connection getConn() throws Exception {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    //  GET USER BY ID
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));   // if needed
                u.setFirstName(rs.getString("first_name"));
                u.setLastName(rs.getString("last_name"));
                u.setPhoneNumber(rs.getLong("phone_number"));

                // accountType in DB = user_roles table → 
                // We'll handle roles separately (Session already stores it)
                return u;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    //  UPDATE USER PROFILE
    public boolean updateUserProfile(int userId, String first, String last, long phone) {

        String sql = """
            UPDATE users
            SET first_name = ?, last_name = ?, phone_number = ?
            WHERE user_id = ?
        """;

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, first);
            ps.setString(2, last);
            ps.setLong(3, phone);
            ps.setInt(4, userId);

            return ps.executeUpdate() == 1;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
