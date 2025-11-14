package com.restaurant.dao;

import java.sql.*;
import com.restaurant.util.PasswordUtil;

public class RegisterDao {

    public static String register(String username, String email, String password, String account_type,
                                  long phone_number, String first_name, String last_name, String dbpath) {

        Connection connection = null;

        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + dbpath;
            connection = DriverManager.getConnection(url);
            connection.setAutoCommit(false);

            // Check if email already exists
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT 1 FROM users WHERE email = ?")) {
                ps.setString(1, email.toLowerCase());
                try (ResultSet result = ps.executeQuery()) {
                    if (result.next()) {
                        return "EMAIL_EXIST";
                    }
                }
            }

            // Hash password before storing
            String hashedPassword = PasswordUtil.hashPassword(password);

            // Insert new user (note: includes username now)
            int userId = -1;
            String insert_user =
                    "INSERT INTO users (username, first_name, last_name, email, password, phone_number) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = connection.prepareStatement(
                    insert_user, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, username);
                ps.setString(2, first_name);
                ps.setString(3, last_name);
                ps.setString(4, email.toLowerCase());
                ps.setString(5, hashedPassword);
                ps.setLong(6, phone_number);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    connection.rollback();
                    return "FAIL";
                }

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        userId = keys.getInt(1);
                    } else {
                        connection.rollback();
                        return "FAIL";
                    }
                }
            }

            // Get role id
            int role_id = -1;
            String get_role_id = "SELECT role_id FROM user_roles WHERE role_name = ?";
            try (PreparedStatement ps = connection.prepareStatement(get_role_id)) {
                ps.setString(1, account_type.toUpperCase());
                try (ResultSet role_rs = ps.executeQuery()) {
                    if (role_rs.next()) {
                        role_id = role_rs.getInt("role_id");
                    } else {
                        connection.rollback();
                        return "FAIL";
                    }
                }
            }

            // Assign role to user
            String link_role = "INSERT INTO users_to_user_roles(user_id, role_id) VALUES (?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(link_role)) {
                ps.setInt(1, userId);
                ps.setInt(2, role_id);
                int rows = ps.executeUpdate();
                if (rows == 0) {
                    connection.rollback();
                    return "FAIL";
                }
            }

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
            if (connection != null) {
                try { connection.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}
