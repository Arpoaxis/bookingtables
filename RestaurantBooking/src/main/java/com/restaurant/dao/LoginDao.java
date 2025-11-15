//login dao class
package com.restaurant.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.restaurant.model.User;

public class LoginDao {

    public static User validate(String email, String password, String dbPath) {
        User user = null;
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + dbPath;

            try (Connection connection = DriverManager.getConnection(url);
            		PreparedStatement ps = connection.prepareStatement(
            			    "SELECT u.user_id, u.email, u.password, u.first_name, u.last_name, r.role_name " +
            			    "FROM users u " +
            			    "JOIN users_to_user_roles ur ON u.user_id = ur.user_id " +
            			    "JOIN user_roles r ON ur.role_id = r.role_id " +
            			    "WHERE u.email = ?"
            			);) {

                ps.setString(1, email);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String storedPassword = rs.getString("password");

                        // Compare with entered password (stored passwords are plain text in DB for this example)
                        if (storedPassword != null && storedPassword.equals(password)) {
                            String accountType = rs.getString("role_name");

                            user = new User();
                            user.setEmail(rs.getString("email"));
                            user.setPassword(password);
                            user.setAccountType(accountType);
                            user.setAccountType(rs.getString("role_name"));
                            user.setFirstName(rs.getString("first_name")); 
                            user.setLastName(rs.getString("last_name"));
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