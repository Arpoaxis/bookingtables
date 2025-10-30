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
                     "SELECT u.user_id, u.email, u.password, r.account_type " +
                     "FROM user u JOIN roles r ON u.user_id = r.user_id " +
                     "WHERE u.email = ?")) {

                ps.setString(1, email);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String storedPassword = rs.getString("password");

                        //Compare with entered password
                        if (storedPassword.equals(password)) {
                            String accountType = rs.getString("account_type");

                            // Create user object and populate it
                            user = new User();
                            user.setEmail(email);
                            user.setPassword(password);
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