package com.restaurant.servlet;

import com.restaurant.dao.BookingDao;
import com.restaurant.model.Booking;
import com.restaurant.model.User;
import com.restaurant.dao.WaitlistDao;
import com.restaurant.model.WaitlistEntry;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/staff/dashboard")
public class StaffDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (role == null ||
                !(role.equalsIgnoreCase("HOST")
                        || role.equalsIgnoreCase("EMPLOYEE")
                        || role.equalsIgnoreCase("MANAGER"))) {
            // Not staff → no access
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        Integer restaurantId = currentUser.getRestaurantId();
        if (restaurantId == null) {
            request.setAttribute("error", "Your account is not associated with a restaurant.");
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp")
                   .forward(request, response);
            return;
        }

        LocalDate today = LocalDate.now();

        try {
            // Bookings for today
            BookingDao bookingDao = new BookingDao(getServletContext());
            List<Booking> todaysBookings =
                    bookingDao.findBookingsForDate(restaurantId, today);

            // Active waitlist entries
            WaitlistDao waitlistDao = new WaitlistDao(getServletContext());
            List<WaitlistEntry> waitlist =
                    waitlistDao.findActiveByRestaurant(restaurantId);

            // Put things on the request
            request.setAttribute("bookings", todaysBookings);
            request.setAttribute("waitlist", waitlist);
            request.setAttribute("today", today);
            request.setAttribute("restaurantId", restaurantId);
            request.setAttribute("user", currentUser); // handy if JSP wants it

            // Forward to staff dashboard JSP
            request.getRequestDispatcher("/WEB-INF/jsp/staff/staff_dashboard.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Could not load staff dashboard data.");
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp")
                   .forward(request, response);
        }
    }

}
