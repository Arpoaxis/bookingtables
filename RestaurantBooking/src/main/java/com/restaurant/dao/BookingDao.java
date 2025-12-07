package com.restaurant.dao;

import com.restaurant.model.Booking;
import com.restaurant.util.DatabaseUtility;
import jakarta.servlet.ServletContext;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId; 
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingDao {

	private final ServletContext servletContext;

	public BookingDao(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	private Connection getConn() throws SQLException {
		return DatabaseUtility.getConnection(servletContext);
	}

	// 1) CREATE BOOKING
	public static void createBooking(ServletContext ctx, int userId, int restaurantId, int guests, String date,
			String time, String requests) throws Exception {

		String sql = """
				INSERT INTO bookings
				(user_id, restaurant_id, number_of_guests, booking_date, booking_time, special_requests)
				VALUES (?, ?, ?, ?, ?, ?)
				""";

		try (Connection conn = DatabaseUtility.getConnection(ctx); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, userId);
			ps.setInt(2, restaurantId);
			ps.setInt(3, guests);
			ps.setString(4, date);
			ps.setString(5, time);
			ps.setString(6, requests);

			ps.executeUpdate();
		}
	}

	//CREATE BOOKING AND ASSIGN A TABLE 
    public static int createBookingWithTable(ServletContext ctx, Integer userId, int restaurantId, int guests, String date,
                                             String time, String requests, Integer tableId) throws Exception {
        String insertSql = """
                INSERT INTO bookings
                (user_id, restaurant_id, number_of_guests, booking_date, booking_time, special_requests)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        String insertLink = "INSERT INTO booking_tables (booking_id, table_id) VALUES (?, ?)";

        try (Connection conn = DatabaseUtility.getConnection(ctx)) {
            conn.setAutoCommit(false);
            try {
            	// --- FIXED OVERLAP CHECK (matches API logic) ---
            	LocalTime newStart = LocalTime.parse(time.length() == 5 ? time + ":00" : time);
            	LocalTime newEnd = newStart.plusHours(1);

            	System.out.println("[BookingDao] Checking table availability with:");
            	System.out.println(" newStart = " + newStart);
            	System.out.println(" newEnd   = " + newEnd);

            	String checkSql = """
            	    SELECT 1
            	    FROM booking_tables bt
            	    JOIN bookings b ON bt.booking_id = b.booking_id
            	    WHERE bt.table_id = ?
            	      AND b.booking_date = ?
            	      AND time(b.booking_time) < time(?)
            	      AND time(b.booking_time, '+1 hour') > time(?)
            	      AND b.booking_status <> 'CANCELLED'
            	    LIMIT 1
            	""";

            	try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
            	    psCheck.setInt(1, tableId);
            	    psCheck.setString(2, date);

        
            	    psCheck.setString(3, newEnd.toString());  
            	    psCheck.setString(4, newStart.toString()); 

            	    try (ResultSet rs = psCheck.executeQuery()) {
            	        if (rs.next()) {
            	            System.out.println("[BookingDao] Table is already booked in this time window!");
            	            throw new SQLException("TABLE_ALREADY_BOOKED");
            	        }
            	    }
            	}
                

                try (PreparedStatement ps = conn.prepareStatement(insertSql);
                     PreparedStatement psLink = conn.prepareStatement(insertLink);
                     PreparedStatement psLast = conn.prepareStatement("SELECT last_insert_rowid()")) {

                    int i = 1;
                    if (userId == null) {
                        ps.setNull(i++, Types.INTEGER);
                    } else {
                        ps.setInt(i++, userId);
                    }
                    ps.setInt(i++, restaurantId);
                    ps.setInt(i++, guests);
                    ps.setString(i++, date);
                    ps.setString(i++, time);
                    ps.setString(i++, requests);

                    ps.executeUpdate();

                    int bookingId;
                    try (ResultSet rs = psLast.executeQuery()) {
                        rs.next();
                        bookingId = rs.getInt(1);
                    }

                    if (tableId != null) {
                        psLink.setInt(1, bookingId);
                        psLink.setInt(2, tableId);
                        psLink.executeUpdate();
                    }

                    conn.commit();
                    System.out.println("[BookingDao] Booking created id=" + bookingId + " (tableId=" + tableId + ")");
                    return bookingId;
                }
            } catch (Exception ex) {
                conn.rollback();
                System.out.println("[BookingDao] createBookingWithTable failed: " + ex.getMessage());
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

	// 2) VIEW BOOKINGS FOR A USER
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

		try (Connection conn = DatabaseUtility.getConnection(ctx); PreparedStatement ps = conn.prepareStatement(sql)) {

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

		try (Connection conn = DatabaseUtility.getConnection(ctx); PreparedStatement ps = conn.prepareStatement(sql)) {

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
	                   u.email      AS customer_email,
	                   u.first_name AS customer_first_name,
	                   u.last_name  AS customer_last_name,
	                   GROUP_CONCAT(rt.table_number, ', ') AS assigned_tables
	            FROM bookings b
	            LEFT JOIN users u ON b.user_id = u.user_id
	            LEFT JOIN booking_tables bt ON b.booking_id = bt.booking_id
	            LEFT JOIN restaurant_tables rt ON bt.table_id = rt.table_id
	            WHERE b.restaurant_id = ?
	              AND b.booking_date = ?
	              AND b.booking_status <> 'CANCELLED'
	            GROUP BY b.booking_id
	            ORDER BY b.booking_time ASC, b.booking_id ASC
	            """;

	    List<Booking> result = new java.util.ArrayList<>();

	    try (Connection conn = getConn();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, restaurantId);
	        ps.setString(2, date.toString());

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
		b.setDate(rs.getString("booking_date")); // yyyy-MM-dd
		b.setTime(rs.getString("booking_time")); // HH:mm:ss
		b.setRequests(rs.getString("special_requests"));
		b.setStatus(rs.getString("booking_status"));

		// Optional columns – only present in some queries:
		try {
			String restaurantName = rs.getString("restaurant_name");
			if (restaurantName != null) {
				b.setRestaurantName(restaurantName);
			}
		} catch (SQLException ignored) {
		}

		try {
			String customerEmail = rs.getString("customer_email");
			if (customerEmail != null) {
				b.setCustomerEmail(customerEmail);
			}
		} catch (SQLException ignored) {
		}

		try {
			String first = rs.getString("customer_first_name");
			if (first != null) {
				b.setCustomerFirstName(first);
			}
		} catch (SQLException ignored) {
		}

		try {
			String last = rs.getString("customer_last_name");
			if (last != null) {
				b.setCustomerLastName(last);
			}
		} catch (SQLException ignored) {
		}
		try {
		    String assigned = rs.getString("assigned_tables");
		    if (assigned != null) {
		        b.setAssignedTables(assigned);
		    }
		} catch (SQLException ignored) {
		    // column not present in some queries – that's fine
		}

		return b;
	}

	/**
	 * 5) GET TABLE BOOKING STATUS FOR FLOOR PLAN Returns a map of table_id ->
	 * booking status for a given date. Status priority: SEATED > CONFIRMED >
	 * PENDING (if multiple bookings exist)
	 */
	public Map<Integer, String> getTableStatusMap(int restaurantId, String date) throws SQLException {
		Map<Integer, String> statusMap = new HashMap<>();

		String sql = """
				    SELECT DISTINCT bt.table_id, b.booking_status
				    FROM bookings b
				    JOIN booking_tables bt ON b.booking_id = bt.booking_id
				    WHERE b.restaurant_id = ?
				      AND b.booking_date = ?
				      AND b.booking_status IN ('PENDING', 'CONFIRMED', 'SEATED')
				    ORDER BY bt.table_id,
				             CASE b.booking_status
				               WHEN 'SEATED' THEN 1
				               WHEN 'CONFIRMED' THEN 2
				               WHEN 'PENDING' THEN 3
				               ELSE 4
				             END
				""";

		try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, restaurantId);
			ps.setString(2, date);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int tableId = rs.getInt("table_id");
					String status = rs.getString("booking_status");

					// first (highest-priority) status for each table wins
					statusMap.putIfAbsent(tableId, status);
				}
			}
		}

		return statusMap;
	}

	/**
	 * 6) GET BOOKING DETAILS FOR A SPECIFIC TABLE AND DATE Returns list of bookings
	 * for a table on a given date.
	 */
	public List<Booking> getBookingsForTable(int tableId, String date) throws SQLException {
		List<Booking> result = new ArrayList<>();

		String sql = """
				    SELECT b.*, u.email AS customer_email
				    FROM bookings b
				    JOIN booking_tables bt ON b.booking_id = bt.booking_id
				    LEFT JOIN users u ON b.user_id = u.user_id
				    WHERE bt.table_id = ?
				      AND b.booking_date = ?
				      AND b.booking_status <> 'CANCELLED'
				    ORDER BY b.booking_time ASC
				""";

		try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, tableId);
			ps.setString(2, date);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					result.add(mapRowToBooking(rs));
				}
			}
		}

		return result;
	}

	/**
	 * Create a booking row from a waitlist entry. Called when staff presses
	 * "Notify".
	 */
	public void createBookingFromWaitlist(int waitlistId) throws SQLException {
		String selectSql = """
				    SELECT restaurant_id,
				           user_id,
				           customer_name,
				           party_size,
				           special_requests
				    FROM waitlists
				    WHERE waitlist_id = ?
				""";

		String insertSql = """
				    INSERT INTO bookings(
				        user_id,
				        restaurant_id,
				        number_of_guests,
				        booking_date,
				        booking_time,
				        special_requests,
				        booking_status,
				        created
				    )
				    VALUES (?,?,?,?,?,?,?,datetime('now'))
				""";

		try (Connection conn = getConn();
				PreparedStatement psSel = conn.prepareStatement(selectSql);
				PreparedStatement psIns = conn.prepareStatement(insertSql)) {

			// 1) Read the waitlist row
			psSel.setInt(1, waitlistId);
			try (ResultSet rs = psSel.executeQuery()) {
				if (!rs.next()) {
					throw new SQLException("No waitlist entry with id=" + waitlistId);
				}

				Integer userId = rs.getObject("user_id") == null ? null : rs.getInt("user_id");
				int restaurantId = rs.getInt("restaurant_id");
				int partySize = rs.getInt("party_size");
				String customer = rs.getString("customer_name");
				String specialReqs = rs.getString("special_requests");

				// 2) Decide booking date & time
				LocalDate today = LocalDate.now(ZoneId.of("America/Los_Angeles"));

				// round current time to nearest half-hour slot 
				LocalTime now = LocalTime.now().withSecond(0).withNano(0);
				int m = now.getMinute();
				if (m < 15) {
					now = now.withMinute(0);
				} else if (m < 45) {
					now = now.withMinute(30);
				} else {
					now = now.plusHours(1).withMinute(0);
				}
				DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm:ss");

				// 3) Insert the booking
				int i = 1;
				if (userId == null) {
					psIns.setNull(i++, Types.INTEGER);
				} else {
					psIns.setInt(i++, userId);
				}

				psIns.setInt(i++, restaurantId);
				psIns.setInt(i++, partySize);
				psIns.setString(i++, today.toString());
				psIns.setString(i++, now.format(tf));

				// If there were no special requests, note that this came from waitlist
				String notes = (specialReqs == null || specialReqs.isBlank()) ? "From waitlist: " + customer
						: specialReqs;
				psIns.setString(i++, notes);

				// After notify, treat as confirmed reservation
				psIns.setString(i++, "CONFIRMED");

				psIns.executeUpdate();
			}
		}
	}

	// Get a single booking (used to check restaurant & current status)
	public Booking getBookingById(int bookingId) throws SQLException {
		String sql = """
				SELECT b.*,
				       u.email      AS customer_email,
				       u.first_name AS customer_first_name,
				       u.last_name  AS customer_last_name
				FROM bookings b
				JOIN users u ON b.user_id = u.user_id
				WHERE b.booking_id = ?
				""";

		try (Connection conn = getConn(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, bookingId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapRowToBooking(rs);
				}
			}
		}
		return null;
	}

	// Update only the booking_status field
	public void updateBookingStatus(int bookingId, String newStatus) throws SQLException {
	    String sql = """
	        UPDATE bookings
	        SET booking_status = ?
	        WHERE booking_id = ?
	    """;

	    try (Connection conn = getConn();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setString(1, newStatus);
	        ps.setInt(2, bookingId);
	        ps.executeUpdate();
	    }
	}

	// Remove all table links for a booking
	public void clearTablesForBooking(int bookingId) throws SQLException {
	    String sql = "DELETE FROM booking_tables WHERE booking_id = ?";

	    try (Connection conn = getConn();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, bookingId);
	        ps.executeUpdate();
	    }
	}

	// Assign exactly ONE table to a booking (replacing any existing)
	public void assignSingleTable(int bookingId, int tableId) throws SQLException {
	    try (Connection conn = getConn()) {
	        conn.setAutoCommit(false);
	        try (PreparedStatement del =
	                     conn.prepareStatement("DELETE FROM booking_tables WHERE booking_id = ?")) {
	            del.setInt(1, bookingId);
	            del.executeUpdate();
	        }
	        try (PreparedStatement ins =
	                     conn.prepareStatement("INSERT INTO booking_tables (booking_id, table_id) VALUES (?, ?)")) {
	            ins.setInt(1, bookingId);
	            ins.setInt(2, tableId);
	            ins.executeUpdate();
	        }
	        conn.commit();
	    }
	}

	// table_id for a given date 
	public Map<Integer, Integer> getPrimaryTableIdMapForDate(int restaurantId,
	                                                         LocalDate date) throws SQLException {
	    String sql = """
	        SELECT b.booking_id,
	               MIN(bt.table_id) AS table_id
	        FROM bookings b
	        JOIN booking_tables bt ON b.booking_id = bt.booking_id
	        WHERE b.restaurant_id = ?
	          AND b.booking_date = ?
	          AND b.booking_status <> 'CANCELLED'
	        GROUP BY b.booking_id
	    """;

	    Map<Integer, Integer> map = new HashMap<>();

	    try (Connection conn = getConn();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, restaurantId);
	        ps.setString(2, date.toString());

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                int bookingId = rs.getInt("booking_id");
	                int tableId   = rs.getInt("table_id");
	                if (!rs.wasNull()) {
	                    map.put(bookingId, tableId);
	                }
	            }
	        }
	    }
	    return map;
	}

}
