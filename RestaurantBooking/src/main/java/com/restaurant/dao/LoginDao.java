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

            try (Connection connection = DriverManager.getConnection(url);
                 PreparedStatement ps = connection.prepareStatement(
                     "SELECT u.user_id, u.email, u.password, r.role_name " +
                     "FROM users u " +
                     "JOIN users_to_user_roles ur ON u.user_id = ur.user_id " +
                     "JOIN user_roles r ON ur.role_id = r.role_id " +
                     "WHERE u.email = ?"
                 )) {

                ps.setString(1, email);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String storedPassword = rs.getString("password");
                        String roleName = rs.getString("role_name");

                        boolean matches = false;

                        if (storedPassword != null) {
                            // If it looks like a BCrypt hash, use BCrypt
                            if (storedPassword.startsWith("$2a$")
                                    || storedPassword.startsWith("$2b$")
                                    || storedPassword.startsWith("$2y$")) {
                                matches = PasswordUtil.verifyPassword(password, storedPassword);
                            } else {
                                // Fallback: legacy plain-text comparison
                                matches = storedPassword.equals(password);
                            }
                        }

                        if (matches) {
                            user = new User();
                            user.setEmail(email);
                            // we won't keep the password in session later
                            user.setPassword(null);
                            user.setAccountType(roleName);
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
