package com.restaurant.servlet;

import com.restaurant.dao.BookingDao;
import com.restaurant.dao.RestaurantTableDao;
import com.restaurant.dao.WaitlistDao;
import com.restaurant.model.Booking;
import com.restaurant.model.RestaurantTable;
import com.restaurant.model.User;
import com.restaurant.model.WaitlistEntry;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@WebServlet("/staff/dashboard")
public class StaffDashboardServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("user") == null) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		User currentUser = (User) session.getAttribute("user");
		if (currentUser == null) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		String role = (String) session.getAttribute("role");
		if (role == null || !(role.equalsIgnoreCase("HOST") || role.equalsIgnoreCase("EMPLOYEE")
				|| role.equalsIgnoreCase("MANAGER") || role.equalsIgnoreCase("ADMIN"))) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		Integer restaurantId = (Integer) session.getAttribute("restaurantId");
		if (restaurantId == null) {
			request.setAttribute("error", "You are not associated with a restaurant.");
			request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
			return;
		}

		// Today in PST
		LocalDate today = LocalDate.now(ZoneId.of("America/Los_Angeles"));
		String todayStr = today.toString();
		request.setAttribute("today", todayStr);

		try {
			BookingDao bookingDao = new BookingDao(getServletContext());
			WaitlistDao waitlistDao = new WaitlistDao(getServletContext());

			// ---- BOOKINGS FOR TODAY ----
			List<Booking> todaysBookings = bookingDao.findBookingsForDate(restaurantId, today);

			String sort = request.getParameter("sort"); // time, guests, lastName
			String dir = request.getParameter("dir"); // asc / desc
			boolean desc = "desc".equalsIgnoreCase(dir);

			Comparator<Booking> cmp = null;
			if ("time".equalsIgnoreCase(sort)) {
				cmp = Comparator.comparing(Booking::getTime, Comparator.nullsLast(String::compareTo));
			} else if ("guests".equalsIgnoreCase(sort)) {
				cmp = Comparator.comparingInt(Booking::getGuests);
			} else if ("lastName".equalsIgnoreCase(sort)) {
				cmp = Comparator.comparing(b -> Objects.toString(b.getCustomerLastName(), ""),
						String.CASE_INSENSITIVE_ORDER);
			}
			if (cmp != null) {
				if (desc) {
					cmp = cmp.reversed();
				}
				todaysBookings.sort(cmp);
			}

			// ---- ACTIVE WAITLIST ----
			List<WaitlistEntry> waitlist = waitlistDao.findActiveByRestaurant(restaurantId);

			// ---- FLOOR PLAN DATA (for embedded plan) ----
			String dbPath = getServletContext().getRealPath("/WEB-INF/database/restBooking.db");
			RestaurantTableDao tableDao = new RestaurantTableDao(dbPath);
			List<RestaurantTable> tables = tableDao.getAllTables();

			// per-table status for colouring the plan
			Map<Integer, String> tableStatusMap =
			        bookingDao.getTableStatusMap(restaurantId, todayStr);

			// booking -> (one) table mapping, for the dropdown/drag UI
			Map<Integer, Integer> bookingTableMap =
			        bookingDao.getPrimaryTableIdMapForDate(restaurantId, today);

			// ===== NEW: tableId -> surname label for mini floor plan =====
			Map<Integer, String> tableNameMap  = new HashMap<>();
			Map<Integer, Integer> tableRankMap = new HashMap<>();

			for (Booking b : todaysBookings) {
			    Integer tableId = bookingTableMap.get(b.getBookingId());
			    if (tableId == null) {
			        continue; // no table assigned
			    }

			    String status = b.getStatus();
			    if ("CANCELLED".equals(status)) {
			        continue; // don't show cancelled bookings on the table
			    }

			    // priority: SEATED > CONFIRMED > PENDING > others
			    int rank;
			    switch (status) {
			        case "SEATED":
			            rank = 1; break;
			        case "CONFIRMED":
			            rank = 2; break;
			        case "PENDING":
			            rank = 3; break;
			        default:
			            rank = 4; break;
			    }

			    Integer existingRank = tableRankMap.get(tableId);
			    if (existingRank == null || rank < existingRank) {
			        tableRankMap.put(tableId, rank);

			        String last = b.getCustomerLastName();
			        if (last == null || last.isBlank()) {
			            last = b.getCustomerFirstName();
			        }
			        tableNameMap.put(tableId, last);
			    }
			}
			// ===== END NEW BLOCK =====


			// Put things on the request for the JSP
			request.setAttribute("sort", sort);
			request.setAttribute("dir", dir);
			request.setAttribute("bookings", todaysBookings);
			request.setAttribute("waitlist", waitlist);
			request.setAttribute("restaurantId", restaurantId);
			request.setAttribute("user", currentUser);

			// floorplan attributes (reused by mini plan AND table dropdowns)
			request.setAttribute("tables", tables);
			request.setAttribute("allTables", tables); // convenience alias
			request.setAttribute("tableStatusMap", tableStatusMap);
			request.setAttribute("bookingTableMap", bookingTableMap);
			request.setAttribute("tableNameMap", tableNameMap);

			request.getRequestDispatcher("/WEB-INF/jsp/staff/staff_dashboard.jsp").forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("error", "Could not load staff dashboard data.");
			request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
		}

	}
}
