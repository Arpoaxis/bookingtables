package com.restaurant.servlet;

import com.restaurant.dao.BookingDao;
import com.restaurant.dao.WaitlistDao;
import com.restaurant.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/staff/waitlist")
public class StaffWaitlistActionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String role = user.getAccountType();
        if (role == null ||
                !(role.equalsIgnoreCase("HOST")
                  || role.equalsIgnoreCase("EMPLOYEE")
                  || role.equalsIgnoreCase("MANAGER")
                  || role.equalsIgnoreCase("ADMIN"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String action = request.getParameter("action");
        String idParam = request.getParameter("waitlistId");
        if (action == null || idParam == null) {
            response.sendRedirect(request.getContextPath() + "/staff/dashboard");
            return;
        }

        int waitlistId;
        try {
            waitlistId = Integer.parseInt(idParam);
        } catch (NumberFormatException ex) {
            session.setAttribute("error", "Invalid waitlist id.");
            response.sendRedirect(request.getContextPath() + "/staff/dashboard");
            return;
        }

        WaitlistDao waitlistDao = new WaitlistDao(getServletContext());
        BookingDao bookingDao   = new BookingDao(getServletContext());

        try {
            if ("notify".equals(action)) {
                // 1) update waitlist status
                waitlistDao.updateStatus(waitlistId, "NOTIFIED", user.getUserId());

                // 2) create a booking row
                bookingDao.createBookingFromWaitlist(waitlistId);

                session.setAttribute("success",
                        "Guest notified and booking created.");

            } else if ("seat".equals(action)) {

                waitlistDao.updateStatus(waitlistId, "SEATED", user.getUserId());
                session.setAttribute("success", "Guest marked as seated.");

            } else if ("cancel".equals(action)) {

                waitlistDao.updateStatus(waitlistId, "CANCELLED", user.getUserId());
                session.setAttribute("success", "Waitlist entry cancelled.");

            } else {
                session.setAttribute("error", "Unknown action: " + action);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            session.setAttribute("error",
                    "Could not update waitlist: " + ex.getMessage());
        }

        // Go back to the staff dashboard (shows both waitlist & bookings)
        response.sendRedirect(request.getContextPath() + "/staff/dashboard");
    }
}
