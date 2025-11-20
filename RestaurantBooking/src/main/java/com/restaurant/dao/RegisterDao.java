package com.restaurant.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.restaurant.util.PasswordUtil;

public class RegisterDao {

    public static String register(String username,
                                  String email,
                                  String password,
                                  String accountType,
                                  long phoneNumber,
                                  String firstName,
                                  String lastName,
                                  String dbPath) {

        Connection connection = null;

        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + dbPath;
            connection = DriverManager.getConnection(url);
            connection.setAutoCommit(false);

            // 1) Check if email already exists
            String checkEmail = "SELECT 1 FROM users WHERE email = ?";
            try (PreparedStatement ps = connection.prepareStatement(checkEmail)) {
                ps.setString(1, email.toLowerCase());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return "EMAIL_EXIST";
                    }
                }
            }

            // 2) Hash password
            String hashedPassword = PasswordUtil.hashPassword(password);
            password = null;

            // 3) Insert user
            int userId = -1;
            String insertUser = """
                INSERT INTO users (username, first_name, last_name, email, password, phone_number)
                VALUES (?, ?, ?, ?, ?, ?)
            """;

            try (PreparedStatement ps = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, username);
                ps.setString(2, firstName);
                ps.setString(3, lastName);
                ps.setString(4, email.toLowerCase());
                ps.setString(5, hashedPassword);
                ps.setLong(6, phoneNumber);

                int rows = ps.executeUpdate();
                if (rows == 0) {
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

            // 4) Get role ID
            int roleId = -1;
            String getRoleId = "SELECT role_id FROM user_roles WHERE role_name = ?";

            try (PreparedStatement ps = connection.prepareStatement(getRoleId)) {
                ps.setString(1, accountType.toUpperCase());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        roleId = rs.getInt("role_id");
                    } else {
                        connection.rollback();
                        return "FAIL";
                    }
                }
            }

            // 5) Link user to role
            String linkRole = "INSERT INTO users_to_user_roles (user_id, role_id) VALUES (?, ?)";

            try (PreparedStatement ps = connection.prepareStatement(linkRole)) {
                ps.setInt(1, userId);
                ps.setInt(2, roleId);

                if (ps.executeUpdate() == 0) {
                    connection.rollback();
                    return "FAIL";
                }
            }

            // 6) Commit all
            connection.commit();
            return "SUCCESS";

        } catch (Exception e) {
            e.printStackTrace();

            if (connection != null) {
                try { connection.rollback(); } catch (SQLException ignored) {}
            }

            return "FAIL";

        } finally {
            if (connection != null) {
                try { connection.close(); } catch (SQLException ignored) {}
            }
        }
    }
}
