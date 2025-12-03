package com.restaurant.dao;

import com.restaurant.model.User;
import com.restaurant.util.DatabaseUtility;
import jakarta.servlet.ServletContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    private final ServletContext servletContext;

    public UserDao(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    private Connection getConn() throws SQLException {
        return DatabaseUtility.getConnection(servletContext);
    }

    // ---------- Helper to map a row to User ----------
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setFirstName(rs.getString("first_name"));
        u.setLastName(rs.getString("last_name"));
        u.setEmail(rs.getString("email"));

        // phone_number is TEXT in DB, convert to long if possible
        String phoneStr = rs.getString("phone_number");
        if (phoneStr != null && !phoneStr.isBlank()) {
            try {
                long phone = Long.parseLong(phoneStr.replaceAll("[^0-9]", ""));
                u.setPhoneNumber(phone);
            } catch (NumberFormatException ignore) {
                // leave default 0 if invalid
            }
        }

        // restaurant_id may be NULL
        Object restObj = rs.getObject("restaurant_id");
        if (restObj != null) {
            u.setRestaurantId(((Number) restObj).intValue());
        }

        u.setActive(rs.getInt("active") == 1);
        
        try {
            String acctType = rs.getString("account_type");
            if (acctType != null) {
                u.setAccountType(acctType);
            }
        } catch (SQLException ignored) {
            // queries that don't select account_type just won't have this column
        }

        return u;
    }

    // ---------- GET USER BY ID ----------
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRowToUser(rs);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // ---------- UPDATE USER PROFILE ----------
    public boolean updateUserProfile(int userId, String first, String last, long phone) {

        String sql = """
            UPDATE users
            SET first_name = ?, last_name = ?, phone_number = ?
            WHERE user_id = ?
        """;

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, first);
            ps.setString(2, last);
            ps.setString(3, String.valueOf(phone)); // store as text
            ps.setInt(4, userId);

            return ps.executeUpdate() == 1;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    public void updateUserActiveStatus(int userId, boolean isActive) throws SQLException {
        String sql = "UPDATE users SET active = ? WHERE user_id = ?";

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 'active' looks like an INT 0/1 column (since you read it with rs.getInt("active") == 1)
            ps.setInt(1, isActive ? 1 : 0);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }
    
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ---------- FIND EMPLOYEES FOR A RESTAURANT ----------
    public List<User> findEmployeesByRestaurant(int restaurantId) throws SQLException {
    	String sql = """
    		    SELECT u.*, r.role_name AS account_type
    		    FROM users u
    		    JOIN users_to_user_roles ur ON u.user_id = ur.user_id
    		    JOIN user_roles r           ON ur.role_id = r.role_id
    		    WHERE u.restaurant_id = ?
    		      AND r.role_name IN ('EMPLOYEE', 'HOST', 'MANAGER')
    		    ORDER BY u.active DESC, u.last_name, u.first_name
    		""";

        List<User> list = new ArrayList<>();

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, restaurantId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToUser(rs));
                }
            }
        }

        return list;
    }
    public boolean createEmployee(int restaurantId,
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            String roleName) throws SQLException {

    			// Simple username from email prefix
    			String username = email;
    			int atIndex = email.indexOf('@');
    			if (atIndex > 0) {
    				username = email.substring(0, atIndex);
    			}

    			// Very basic default password for now
    			String defaultPassword = "changeme";

    			try (Connection conn = getConn()) {
    				conn.setAutoCommit(false);

    				try {
    					// 1) Insert into users
    					String insertUser = """
    							INSERT INTO users
    							(username, first_name, last_name, email, password,
    							phone_number, restaurant_id, active)
    							VALUES (?,?,?,?,?,?,?,1)
    							""";

    					int newUserId;

    					try (PreparedStatement ps = conn.prepareStatement(insertUser)) {
    						int idx = 1;
    						ps.setString(idx++, username);
    						ps.setString(idx++, firstName);
    						ps.setString(idx++, lastName);
    						ps.setString(idx++, email.toLowerCase());
    						ps.setString(idx++, defaultPassword);
    						ps.setString(idx++, phoneNumber);
    						ps.setInt(idx++, restaurantId);
    						ps.executeUpdate();
    					}

    					// get new user id
    					try (Statement s = conn.createStatement();
    							ResultSet rs = s.executeQuery("SELECT last_insert_rowid()")) {
    						rs.next();
    						newUserId = rs.getInt(1);
    					}

    					// 2) Link to role
    					String normalizedRole = roleName.toUpperCase();
    					Integer roleId = null;
    					try (PreparedStatement ps = conn.prepareStatement(
    							"SELECT role_id FROM user_roles WHERE role_name = ?")) {
    						ps.setString(1, normalizedRole);
    						try (ResultSet rs = ps.executeQuery()) {
    							if (rs.next()) {
    								roleId = rs.getInt(1);
    							}
    						}
    					}

    					if (roleId != null) {
    						try (PreparedStatement ps = conn.prepareStatement("""
    								    INSERT OR IGNORE INTO users_to_user_roles(user_id, role_id)
    								    VALUES (?,?)
    								""")) {
    							ps.setInt(1, newUserId);
    							ps.setInt(2, roleId);
    							ps.executeUpdate();
    						}
    					}

    					conn.commit();
    					return true;
    				} catch (SQLException ex) {
    					conn.rollback();
    					throw ex;
    				} finally {
    					conn.setAutoCommit(true);
    				}
    			}
    }
 // Reset password for a user (e.g. to a default)
    public void resetUserPassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }
    
 // Get the stored password string for a user (currently plain text).
    public String getPasswordForUser(int userId) {
        String sql = "SELECT password FROM users WHERE user_id = ?";

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    /**
     * Update basic employee info + role.
     * Note: accountType is role name: EMPLOYEE / HOST / MANAGER
     */
    public void updateEmployeeDetails(int userId,
                                      String firstName,
                                      String lastName,
                                      String email,
                                      String phoneNumber,
                                      String accountType) throws SQLException {
        try (Connection conn = getConn()) {
            conn.setAutoCommit(false);

            try {
                // 1) Update users table
                String updateUser = """
                    UPDATE users
                    SET first_name = ?, last_name = ?, email = ?, phone_number = ?
                    WHERE user_id = ?
                """;

                try (PreparedStatement ps = conn.prepareStatement(updateUser)) {
                    int idx = 1;
                    ps.setString(idx++, firstName);
                    ps.setString(idx++, lastName);
                    ps.setString(idx++, email.toLowerCase());
                    ps.setString(idx++, phoneNumber);
                    ps.setInt(idx++, userId);
                    ps.executeUpdate();
                }

                // 2) Update role in users_to_user_roles
                String normalizedRole = accountType == null ? "" : accountType.toUpperCase();

                Integer roleId = null;
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT role_id FROM user_roles WHERE role_name = ?")) {
                    ps.setString(1, normalizedRole);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            roleId = rs.getInt(1);
                        }
                    }
                }

                if (roleId != null) {
                    // remove existing roles EMPLOYEE/HOST/MANAGER and set the new one
                    try (PreparedStatement ps = conn.prepareStatement("""
                            DELETE FROM users_to_user_roles
                            WHERE user_id = ?
                              AND role_id IN (
                                SELECT role_id
                                FROM user_roles
                                WHERE role_name IN ('EMPLOYEE','HOST','MANAGER')
                              )
                        """)) {
                        ps.setInt(1, userId);
                        ps.executeUpdate();
                    }

                    try (PreparedStatement ps = conn.prepareStatement("""
                            INSERT OR IGNORE INTO users_to_user_roles(user_id, role_id)
                            VALUES (?,?)
                        """)) {
                        ps.setInt(1, userId);
                        ps.setInt(2, roleId);
                        ps.executeUpdate();
                    }
                }

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

}
