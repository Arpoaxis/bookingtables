package com.restaurant.servlet;

import com.restaurant.dao.BookingDao;
import com.restaurant.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/staff/bookings")
public class StaffBookingActionServlet extends HttpServlet {

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
        String idParam = request.getParameter("bookingId");

        if (action == null || idParam == null || idParam.isBlank()) {
            session.setAttribute("error", "Missing booking action or id.");
            response.sendRedirect(request.getContextPath() + "/staff/dashboard");
            return;
        }

        int bookingId;
        try {
            bookingId = Integer.parseInt(idParam);
        } catch (NumberFormatException ex) {
            session.setAttribute("error", "Invalid booking id.");
            response.sendRedirect(request.getContextPath() + "/staff/dashboard");
            return;
        }

        BookingDao bookingDao = new BookingDao(getServletContext());

        // ------------- TABLE ASSIGNMENT -------------
        if ("assignTable".equals(action)) {
            String tableIdParam = request.getParameter("tableId");

            try {
                if (tableIdParam == null || tableIdParam.isBlank()) {
                    // no selection → just clear
                    bookingDao.clearTablesForBooking(bookingId);
                    session.setAttribute("success", "Table cleared for booking.");
                } else {
                    int tableId = Integer.parseInt(tableIdParam);
                    bookingDao.assignSingleTable(bookingId, tableId);
                    session.setAttribute("success", "Table assigned.");
                }
            } catch (NumberFormatException ex) {
                session.setAttribute("error", "Invalid table id.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                session.setAttribute("error",
                        "Could not update table assignment: " + ex.getMessage());
            }

            response.sendRedirect(request.getContextPath() + "/staff/dashboard");
            return;
        }

        // ------------- STATUS CHANGES -------------
        String newStatus;
        String uiLabel;

        switch (action) {
            case "confirm":
                newStatus = "CONFIRMED";
                uiLabel   = "CONFIRMED";
                break;

            case "seat":
                newStatus = "SEATED";
                uiLabel   = "SEATED";
                break;

            case "complete":
                // Store CANCELLED (allowed by your CHECK) but treat as COMPLETED in UI
                newStatus = "CANCELLED";
                uiLabel   = "COMPLETED";
                break;

            case "cancel":
                newStatus = "CANCELLED";
                uiLabel   = "CANCELLED";
                break;

            default:
                session.setAttribute("error", "Unknown action: " + action);
                response.sendRedirect(request.getContextPath() + "/staff/dashboard");
                return;
        }

        try {
            bookingDao.updateBookingStatus(bookingId, newStatus);

            // both cancel & complete free tables
            if ("CANCELLED".equals(newStatus)) {
                bookingDao.clearTablesForBooking(bookingId);
            }

            session.setAttribute("success", "Booking updated to " + uiLabel + ".");
        } catch (SQLException ex) {
            ex.printStackTrace();
            session.setAttribute("error",
                    "Could not update booking: " + ex.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/staff/dashboard");
    }
}
