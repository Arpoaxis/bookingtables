//register data access object class
package com.restaurant.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterDao {
    public static String register(String email, String password, String account_type,
                                  long phone_number, String first_name, String last_name, String dbpath) {

        ResultSet rs = null;
        Connection connection = null;

        try {
            Class.forName("org.sqlite.JDBC");

            // Build DB path
            String url = "jdbc:sqlite:" + dbpath;

            // Connect to DB
            connection = DriverManager.getConnection(url);
            connection.setAutoCommit(false);

            //Check if email already exists
            try (PreparedStatement ps = connection.prepareStatement("SELECT 1 FROM user WHERE email=?")) {
                ps.setString(1, email);
                try (ResultSet result = ps.executeQuery()) {
                    if (result.next()) {
                        return "EMAIL_EXIST";
                    }
                }
            }

            //Insert new user
            int userId = -1;
            String insert_user = "INSERT INTO user (first_name, last_name, email, password, phone_number) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement ps = connection.prepareStatement(insert_user, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, first_name);
                ps.setString(2, last_name);
                ps.setString(3, email);
                ps.setString(4, password);
                ps.setLong(5, phone_number);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    connection.rollback();
                    return "FAIL";
                }

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1);
                    }
                }
            }

            //Insert into roles table
            String insert_role = "INSERT INTO roles (user_id, account_type) VALUES (?, ?)";
            try (PreparedStatement ps_role = connection.prepareStatement(insert_role)) {
                ps_role.setInt(1, userId);
                ps_role.setString(2, account_type);
                ps_role.executeUpdate();
            }

            //Commit
            connection.commit();
            return "SUCCESS";

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return "FAIL";

        } finally {
            try {
                if (rs != null) rs.close();
                if (connection != null) connection.setAutoCommit(true);
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}