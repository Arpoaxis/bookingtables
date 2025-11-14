//login dao class
package com.restaurant.dao;
import com.restaurant.util.PasswordUtil;
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

                    	boolean matched = false;

                    	// 1) Try BCrypt (new style)
                    	if (PasswordUtil.verifyPassword(password, storedPassword)) {
                    	    matched = true;
                    	} else if (storedPassword != null && storedPassword.equals(password)) {
                    	    // 2) Backwards-compat: old plain-text match still works
                    	    matched = true;

                    	    // Optional: upgrade this user to a hashed password now
                    	    String newHash = PasswordUtil.hashPassword(password);
                    	    try (PreparedStatement up = connection.prepareStatement(
                    	            "UPDATE users SET password = ? WHERE user_id = ?")) {
                    	        up.setString(1, newHash);
                    	        up.setInt(2, rs.getInt("user_id"));
                    	        up.executeUpdate();
                    	    }
                    	}

                    	if (matched) {
                    	    String accountType = rs.getString("role_name");

                    	    user = new User();
                    	    user.setEmail(email);
                    	    // Don’t keep raw password in memory anymore:
                    	    user.setPassword(null);
                    	    user.setAccountType(accountType);
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