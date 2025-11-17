package com.restaurant.dao;

import com.restaurant.model.Booking;
import com.restaurant.util.DatabaseUtility;
import jakarta.servlet.ServletContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDao {
    // 1) CREATE NEW BOOKING  (you already had this)
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
    // 2) GET ALL BOOKINGS FOR A USER 
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
}
