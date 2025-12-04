package com.restaurant.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.restaurant.model.User;
import com.restaurant.util.PasswordUtil;

public class LoginDao {

    public static User validate(String email, String password, String dbPath) {
        User user = null;

        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + dbPath;

            String sql = """
                SELECT u.user_id,
                       u.email,
                       u.password,
                       u.first_name,
                       u.last_name,
                       u.phone_number,
                       u.restaurant_id,
                       u.active,
                       COALESCE(r.role_name, 'CUSTOMER') AS role_name
                FROM users u
                LEFT JOIN users_to_user_roles ur ON u.user_id = ur.user_id
                LEFT JOIN user_roles r          ON ur.role_id = r.role_id
                WHERE LOWER(u.email) = LOWER(?)
                LIMIT 1
            """;

            try (Connection connection = DriverManager.getConnection(url);
                 PreparedStatement ps = connection.prepareStatement(sql)) {

                ps.setString(1, email);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String storedPassword = rs.getString("password");
                        String roleName       = rs.getString("role_name");

                        boolean matches = false;

                        if (storedPassword != null) {
                            // If it looks like a BCrypt hash, use BCrypt
                            if (storedPassword.startsWith("$2a$") ||
                                storedPassword.startsWith("$2b$") ||
                                storedPassword.startsWith("$2y$")) {

                                matches = PasswordUtil.verifyPassword(password, storedPassword);
                            } else {
                                // Legacy plain text match
                                matches = storedPassword.equals(password);
                            }
                        }

                        if (matches) {
                            user = new User();
                            user.setUserId(rs.getInt("user_id"));
                            user.setEmail(rs.getString("email"));
                            user.setFirstName(rs.getString("first_name"));
                            user.setLastName(rs.getString("last_name"));
                            user.setPassword(null); // never keep raw password
                            user.setAccountType(roleName != null ? roleName : "CUSTOMER");

                            // Map restaurant_id if present
                            Object restObj = rs.getObject("restaurant_id");
                            if (restObj != null) {
                                user.setRestaurantId(((Number) restObj).intValue());
                            }

                            // Map phone / active if you care
                            String phoneStr = rs.getString("phone_number");
                            if (phoneStr != null && !phoneStr.isBlank()) {
                                try {
                                    long phone = Long.parseLong(phoneStr.replaceAll("\\D", ""));
                                    user.setPhoneNumber(phone);
                                } catch (NumberFormatException ignore) {
                                }
                            }

                            user.setActive(rs.getInt("active") == 1);
                        }
                    }
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return user; // null means invalid credentials
    }
}
