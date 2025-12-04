package com.restaurant.dao;

import com.restaurant.model.WaitlistEntry;
import com.restaurant.util.DatabaseUtility;
import jakarta.servlet.ServletContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WaitlistDao {

    private final ServletContext servletContext;

    public WaitlistDao(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    private Connection getConn() throws SQLException {
        return DatabaseUtility.getConnection(servletContext);
    }

    // ----------------------------------------------------------------
    // 1) Existing method: add a guest to the waitlist
    // ----------------------------------------------------------------
    /**
     * Add a guest to the waitlist for a restaurant.
     * If userId is null, this is an anonymous / non-account guest.
     */
    public void addToWaitlist(int restaurantId,
                              Integer userId,
                              String customerName,
                              String customerPhone,
                              int partySize,
                              String specialRequests,
                              Integer hostId) throws SQLException {

        String nextPosSql = """
            SELECT COALESCE(MAX(queue_position), 0) + 1
            FROM waitlists
            WHERE restaurant_id = ?
              AND status = 'WAITING'
            """;

        String insertSql = """
            INSERT INTO waitlists(
                restaurant_id,
                user_id,
                customer_name,
                customer_phone_number,
                party_size,
                queue_position,
                status,
                estimated_wait_time,
                arrived_at,
                seated_at,
                host_id,
                special_requests
            )
            VALUES (
                ?, ?, ?, ?, ?, ?,
                'WAITING',
                NULL,
                datetime('now'),
                NULL,
                ?, ?
            )
            """;

        try (Connection conn = getConn()) {
            conn.setAutoCommit(false);

            try {
                int nextPos = 1;
                try (PreparedStatement ps = conn.prepareStatement(nextPosSql)) {
                    ps.setInt(1, restaurantId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            nextPos = rs.getInt(1);
                        }
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    int i = 1;
                    ps.setInt(i++, restaurantId);

                    if (userId == null) {
                        ps.setNull(i++, Types.INTEGER);
                    } else {
                        ps.setInt(i++, userId);
                    }

                    ps.setString(i++, customerName);
                    ps.setString(i++, customerPhone);
                    ps.setInt(i++, partySize);
                    ps.setInt(i++, nextPos);

                    if (hostId == null) {
                        ps.setNull(i++, Types.INTEGER);
                    } else {
                        ps.setInt(i++, hostId);
                    }

                    ps.setString(i++, specialRequests);

                    ps.executeUpdate();
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

    // ----------------------------------------------------------------
    // 2) Helper to map a row to WaitlistEntry
    // ----------------------------------------------------------------
    private WaitlistEntry mapRow(ResultSet rs) throws SQLException {
        WaitlistEntry w = new WaitlistEntry();

        w.setWaitlistId(rs.getInt("waitlist_id"));
        w.setRestaurantId(rs.getInt("restaurant_id"));

        int userId = rs.getInt("user_id");
        if (!rs.wasNull()) w.setUserId(userId);

        w.setCustomerName(rs.getString("customer_name"));
        w.setCustomerPhoneNumber(rs.getString("customer_phone_number"));
        w.setPartySize(rs.getInt("party_size"));

        int qp = rs.getInt("queue_position");
        if (!rs.wasNull()) w.setQueuePosition(qp);

        w.setStatus(rs.getString("status"));
        w.setEstimatedWaitTime(rs.getString("estimated_wait_time"));
        w.setArrivedAt(rs.getString("arrived_at"));
        w.setSeatedAt(rs.getString("seated_at"));

        int hostId = rs.getInt("host_id");
        if (!rs.wasNull()) w.setHostId(hostId);

        w.setSpecialRequests(rs.getString("special_requests"));
        w.setCreated(rs.getString("created"));

        return w;
    }

    // ----------------------------------------------------------------
    // 3) Active waitlist rows for staff dashboard
    // ----------------------------------------------------------------
    /**
     * Return active waitlist entries (WAITING or NOTIFIED) for a restaurant,
     * ordered by queue_position then created.
     */
    public List<WaitlistEntry> findActiveByRestaurant(int restaurantId) throws SQLException {
        String sql = """
            SELECT *
            FROM waitlists
            WHERE restaurant_id = ?
              AND status IN ('WAITING','NOTIFIED')
            ORDER BY queue_position ASC, created ASC
        """;

        List<WaitlistEntry> list = new ArrayList<>();

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, restaurantId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }

        return list;
    }

    // ----------------------------------------------------------------
    // 4) Update status (Notify / Seat / Cancel)
    // ----------------------------------------------------------------
    /**
     * Update a waitlist entry status (e.g. WAITING → NOTIFIED / SEATED / CANCELLED).
     * If newStatus = SEATED, we stamp seated_at = now().
     */
    public void updateStatus(int waitlistId, String newStatus, Integer hostId) throws SQLException {
        String sql = """
            UPDATE waitlists
            SET status = ?,
                host_id = COALESCE(?, host_id),
                seated_at = CASE
                              WHEN ? = 'SEATED' THEN datetime('now')
                              ELSE seated_at
                            END
            WHERE waitlist_id = ?
        """;

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);

            if (hostId == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, hostId);
            }

            ps.setString(3, newStatus);
            ps.setInt(4, waitlistId);

            ps.executeUpdate();
        }
    }
}
