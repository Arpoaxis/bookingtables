//register data access object class
package com.restaurant.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.restaurant.util.PasswordUtil;

public class RegisterDao {
    private static final Logger LOGGER = Logger.getLogger(RegisterDao.class.getName());
    public static String register(String email, String password, String account_type,
                                  long phone_number, String first_name, String last_name, String dbpath) {

        
        Connection connection = null;

        try {
            Class.forName("org.sqlite.JDBC");

            // Build DB path
            String url = "jdbc:sqlite:" + dbpath;

            // Connect to DB
            connection = DriverManager.getConnection(url);
            connection.setAutoCommit(false);

            //Check if email already exists
            try (PreparedStatement ps = connection.prepareStatement("SELECT 1 FROM users WHERE email=?")) {
                ps.setString(1, email);
                try (ResultSet result = ps.executeQuery()) {
                    if (result.next()) {
                        return "EMAIL_EXIST";
                    }
                }
            }

            //Insert new user
            int userId = -1;
            String insert_user = "INSERT INTO users (username, first_name, last_name, email, password, phone_number) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = connection.prepareStatement(insert_user, PreparedStatement.RETURN_GENERATED_KEYS)) {
                // Generate username from email (part before @)
                String username = email.substring(0, email.indexOf('@'));
                // Hash the password before storing
                String hashedPassword = PasswordUtil.hashPassword(password);

                ps.setString(1, username);
                ps.setString(2, first_name);
                ps.setString(3, last_name);
                ps.setString(4, email);
                ps.setString(5, hashedPassword);
                ps.setString(6, String.valueOf(phone_number));

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    connection.rollback();
                    return "FAIL";
                }

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        userId = keys.getInt(1);
                    } else {
                    	return "FAIL";
                    }
                }
            }

            //Get role id
            int role_id = -1;
            String get_role_id="SELECT role_id FROM user_roles WHERE role_name=?";
            try (PreparedStatement ps= connection.prepareStatement(get_role_id)) {
               ps.setString(1, account_type.toUpperCase());
               try (ResultSet role_rs = ps.executeQuery()) {
				   if (role_rs.next()) {
					   role_id = role_rs.getInt("role_id");
				   } else {
					   return "FAIL";
				   }
			   }
            }
            //Assign role to user
            String link_role="INSERT INTO users_to_user_roles(user_id, role_id) VALUES (?, ?)";
            try (PreparedStatement ps= connection.prepareStatement(link_role)) {
				ps.setInt(1, userId);
				ps.setInt(2, role_id);
				int rows= ps.executeUpdate();
				if (rows==0) {
					return "FAIL";
				}
			}

            //Commit
            connection.commit();
            return "SUCCESS";

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during user registration", e);
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error rolling back transaction", ex);
                }
            }
            return "FAIL";
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "JDBC driver not found", e);
            return "FAIL";

        } finally {
            if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					LOGGER.log(Level.WARNING, "Error closing database connection", e);
				}
            }
        }
    }
}