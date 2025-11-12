//login dao class
package com.restaurant.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.restaurant.model.User;
import com.restaurant.util.PasswordUtil;

public class LoginDao {
    private static final Logger LOGGER = Logger.getLogger(LoginDao.class.getName());

    public static User validate(String email, String password, String dbPath) {
        User user = null;
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + dbPath;

            try (Connection connection = DriverManager.getConnection(url);
                 PreparedStatement ps = connection.prepareStatement(
                     // Fixed SQL: correct table names, aliases and spacing
                     "SELECT u.user_id, u.email, u.password, r.role_name " +
                     "FROM users u " +
                     "JOIN users_to_user_roles ur ON u.user_id = ur.user_id " +
                     "JOIN user_roles r ON ur.role_id = r.role_id " +
                     "WHERE u.email = ?"
                 )) {

                ps.setString(1, email);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String storedHashedPassword = rs.getString("password");

                        // Verify the entered password against the stored hashed password using BCrypt
                        if (storedHashedPassword != null && PasswordUtil.verifyPassword(password, storedHashedPassword)) {
                            String accountType = rs.getString("role_name");

                            // Create user object and populate it
                            user = new User();
                            user.setEmail(email);
                            user.setPassword(null); // Don't store password in session
                            user.setAccountType(accountType);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during login validation", e);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "JDBC driver not found", e);
        }

        return user; // null means invalid credentials
    }
}