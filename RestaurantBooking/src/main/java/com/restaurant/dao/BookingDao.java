package com.restaurant.dao;

import com.restaurant.model.Booking;
import com.restaurant.util.DatabaseUtility;
import jakarta.servlet.ServletContext;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingDao {
	
	
	private final ServletContext servletContext;
	
	public BookingDao(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
	
	private Connection getConn() throws SQLException {
        return DatabaseUtility.getConnection(servletContext);
    }
	
	// 1) CREATE BOOKING
    public static void createBooking(ServletContext ctx, int userId, int restaurantId,
                                     int guests, String date, String time, String requests)
            throws Exception {

        String sql = """
                INSERT INTO bookings
                (user_id, restaurant_id, number_of_guests, booking_date, booking_time, special_requests)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseUtility.getConnection(ctx);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, restaurantId);
            ps.setInt(3, guests);
            ps.setString(4, date);
            ps.setString(5, time);
            ps.setString(6, requests);

            ps.executeUpdate();
        }
    }
    // 2) VIEW BOOKINGS
    public static List<Booking> getBookingsForUser(ServletContext ctx, int userId) throws Exception {
        List<Booking> list = new ArrayList<>();

        String sql = """
            SELECT b.booking_id, b.restaurant_id, b.number_of_guests,
                   b.booking_date, b.booking_time, b.special_requests,
                   b.booking_status, r.name AS restaurant_name
            FROM bookings b
            JOIN restaurants r ON b.restaurant_id = r.restaurant_id
            WHERE b.user_id = ?
            ORDER BY b.booking_date, b.booking_time
        """;

        try (Connection conn = DatabaseUtility.getConnection(ctx);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    Booking b = new Booking();
                    b.setBookingId(rs.getInt("booking_id"));
                    b.setRestaurantId(rs.getInt("restaurant_id"));
                    b.setRestaurantName(rs.getString("restaurant_name"));
                    b.setGuests(rs.getInt("number_of_guests"));
                    b.setDate(rs.getString("booking_date"));
                    b.setTime(rs.getString("booking_time"));
                    b.setRequests(rs.getString("special_requests"));
                    b.setStatus(rs.getString("booking_status"));

                    list.add(b);
                }
            }
        }

        return list;
    }
 // 3) CANCEL BOOKING
    public static void cancelBooking(ServletContext ctx, int bookingId, int userId) throws Exception {

        String sql = """
            UPDATE bookings
            SET booking_status = 'CANCELLED'
            WHERE booking_id = ? AND user_id = ?
        """;

        try (Connection conn = DatabaseUtility.getConnection(ctx);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    /**
     * 4) BOOKINGS FOR A RESTAURANT ON A GIVEN DATE (for staff dashboard)
     */
    public List<Booking> findBookingsForDate(int restaurantId, LocalDate date) throws SQLException {
        String sql = """
            SELECT b.*,
                   u.email AS customer_email
            FROM bookings b
            JOIN users u ON b.user_id = u.user_id
            WHERE b.restaurant_id = ?
              AND b.booking_date = ?
              AND b.booking_status <> 'CANCELLED'
            ORDER BY b.booking_time ASC, b.booking_id ASC
        """;

        List<Booking> result = new java.util.ArrayList<>();

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, restaurantId);
            ps.setString(2, date.toString()); // yyyy-MM-dd

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRowToBooking(rs));
                }
            }
        }

        return result;
    }

    private Booking mapRowToBooking(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setBookingId(rs.getInt("booking_id"));
        b.setRestaurantId(rs.getInt("restaurant_id"));
        b.setGuests(rs.getInt("number_of_guests"));
        b.setDate(rs.getString("booking_date"));    // yyyy-MM-dd
        b.setTime(rs.getString("booking_time"));    // HH:mm:ss
        b.setRequests(rs.getString("special_requests"));
        b.setStatus(rs.getString("booking_status"));

        // Optional columns – only present in some queries:
        try {
            String restaurantName = rs.getString("restaurant_name");
            if (restaurantName != null) {
                b.setRestaurantName(restaurantName);
            }
        } catch (SQLException ignored) {}

        try {
            String customerEmail = rs.getString("customer_email");
            if (customerEmail != null) {
                b.setCustomerEmail(customerEmail);
            }
        } catch (SQLException ignored) {}

        return b;
    }


}
