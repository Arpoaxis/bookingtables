package com.restaurant.servlet;

import com.restaurant.dao.BookingDao;
import com.restaurant.dao.RestaurantDao;
import com.restaurant.model.Restaurant;
import com.restaurant.model.User;
import com.restaurant.service.EmailService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

@WebServlet("/booking/create")
public class BookingCreateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Must be logged in
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        Integer userId = user.getUserId();

        String ridStr    = req.getParameter("restaurantId");
        String dateStr   = req.getParameter("date");
        String timeStr   = req.getParameter("time");
        String guestsStr = req.getParameter("guests");
        String requests  = req.getParameter("requests");
        String tableStr  = req.getParameter("tableId");

        Integer restaurantId = null;
        Integer guests = 1;
        Integer tableId = null;

        // ----- restaurant id -----
        try {
            restaurantId = Integer.valueOf(ridStr);
        } catch (Exception e) {
            forwardBack(req, resp,
                    "Invalid or missing restaurant. Please open the booking page from the restaurant listing.",
                    dateStr, timeStr, guests, tableId, ridStr);
            return;
        }

        // ----- guests -----
        try {
            guests = Integer.valueOf(guestsStr);
        } catch (Exception ignored) { }

        // ----- optional table id -----
        try {
            if (tableStr != null && !tableStr.isBlank()) {
                tableId = Integer.valueOf(tableStr);
            }
        } catch (Exception ignored) { }

        // ====== VALIDATE DATE (not in the past) ======
        LocalDate today = LocalDate.now(ZoneId.of("America/Los_Angeles"));
        LocalDate bookingDate;
        try {
            // HTML <input type="date"> sends yyyy-MM-dd
            bookingDate = LocalDate.parse(dateStr);
        } catch (Exception e) {
            forwardBack(req, resp,
                    "Please choose a valid date.",
                    dateStr, timeStr, guests, tableId, ridStr);
            return;
        }

        if (bookingDate.isBefore(today)) {
            forwardBack(req, resp,
                    "Please choose a date that is not in the past.",
                    dateStr, timeStr, guests, tableId, ridStr);
            return;
        }

        // ====== VALIDATE TIME (11:30–22:00, 15-minute intervals) ======
        LocalTime bookingTime;
        try {
            // HTML <input type="time"> sends HH:mm or HH:mm:ss (24-hour)
            bookingTime = LocalTime.parse(timeStr);
        } catch (DateTimeParseException e) {
            forwardBack(req, resp,
                    "Please choose a valid time.",
                    dateStr, timeStr, guests, tableId, ridStr);
            return;
        }

        LocalTime open  = LocalTime.of(11, 30); // 11:30
        LocalTime close = LocalTime.of(22, 0);  // 22:00

        if (bookingTime.isBefore(open) || bookingTime.isAfter(close)) {
            forwardBack(req, resp,
                    "Please choose a time between 11:30 AM and 10:00 PM.",
                    dateStr, timeStr, guests, tableId, ridStr);
            return;
        }

        long minutesFromOpen = java.time.Duration.between(open, bookingTime).toMinutes();
        if (minutesFromOpen % 15 != 0) {
            forwardBack(req, resp,
                    "Please choose a time in 15-minute intervals.",
                    dateStr, timeStr, guests, tableId, ridStr);
            return;
        }

        // ====== CREATE BOOKING ======
        try {
            int bookingId = BookingDao.createBookingWithTable(
                    req.getServletContext(),
                    userId,
                    restaurantId,
                    guests,
                    bookingDate.toString(), // yyyy-MM-dd
                    timeStr,                // keep original HH:mm string
                    requests,
                    tableId
            );

            Restaurant r = RestaurantDao.findById(req.getServletContext(), restaurantId);

            EmailService.sendBookingConfirmation(
                    user.getEmail(),
                    r.getName(),
                    bookingDate.toString(),
                    timeStr,
                    guests
            );

            resp.sendRedirect(req.getContextPath() + "/booking/confirmation");
        } catch (SQLException e) {
            e.printStackTrace();
            forwardBack(req, resp,
                    "Selected table is no longer available. Please choose another time or table.",
                    dateStr, timeStr, guests, tableId, ridStr);
        } catch (Exception e) {
            e.printStackTrace();
            forwardBack(req, resp,
                    "Failed to create booking.",
                    dateStr, timeStr, guests, tableId, ridStr);
        }
    }

    private void forwardBack(HttpServletRequest req, HttpServletResponse resp,
                             String errorMsg,
                             String date, String time, Integer guests, Integer tableId,
                             String ridStr)
            throws ServletException, IOException {

        req.setAttribute("error",  errorMsg);
        req.setAttribute("date",   date);
        req.setAttribute("time",   time);
        req.setAttribute("guests", guests);
        req.setAttribute("tableId", tableId);

        // re-supply "today" so the JSP min attribute still works
        LocalDate today = LocalDate.now(ZoneId.of("America/Los_Angeles"));
        req.setAttribute("today", today.toString());

        try {
            if (ridStr != null && !ridStr.isBlank()) {
                int rid = Integer.parseInt(ridStr);
                Restaurant r = RestaurantDao.findById(req.getServletContext(), rid);
                req.setAttribute("restaurant", r);
            }
        } catch (Exception ex) {
            System.out.println("Restaurant reload failed: " + ex.getMessage());
        }

        req.getRequestDispatcher("/WEB-INF/jsp/booking/new_booking.jsp")
           .forward(req, resp);
    }
}
