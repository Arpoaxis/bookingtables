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

@WebServlet("/booking/create")
public class BookingCreateServlet extends HttpServlet {
		private static final long serialVersionUID = 1L;
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Require login
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        Integer userId = user.getUserId();

        String ridStr = req.getParameter("restaurantId");
        String date = req.getParameter("date");
        String time = req.getParameter("time");
        String requests = req.getParameter("requests");
        String guestsStr = req.getParameter("guests");
        String tableStr = req.getParameter("tableId");
        
        Integer restaurantId = null;
        Integer guests = 1;
        Integer tableId = null;

  
        try {
            restaurantId = Integer.valueOf(ridStr);
        } catch (Exception e) {
            forwardBack(req, resp,
                    "Invalid or missing restaurant. Please open the booking page from the restaurant listing.",
                    null, null, null, null,
                    ridStr);
            return;
        }


        try {
            guests = Integer.valueOf(guestsStr);
        } catch (Exception ignored) {}

        try {
            if (tableStr != null && !tableStr.isBlank())
                tableId = Integer.valueOf(tableStr);
        } catch (Exception ignored) {}

        System.out.println("[BookingCreateServlet] tableId received = " + tableId);
        System.out.println("[BookingCreateServlet] restaurantId received = " + restaurantId);
        System.out.println("[BookingCreateServlet] date = " + date + " time = " + time);

        
        try {
            int bookingId = BookingDao.createBookingWithTable(
                    req.getServletContext(),
                    userId,
                    restaurantId,
                    guests,
                    date,
                    time,
                    requests,
                    tableId
            );

            Restaurant r = RestaurantDao.findById(req.getServletContext(), restaurantId);

            EmailService.sendBookingConfirmation(
                    user.getEmail(),
                    r.getName(),
                    date,
                    time,
                    guests
            );

            resp.sendRedirect(req.getContextPath() + "/booking/confirmation");
            return;

        } catch (SQLException e) {

            forwardBack(req, resp,
                    "Selected table is no longer available. Please choose another time or table.",
                    date, time, guests, tableId,
                    restaurantId.toString());
            return;

        } catch (Exception e) {

            forwardBack(req, resp,
                    "Failed to create booking.",
                    date, time, guests, tableId,
                    restaurantId.toString());
            return;
        }
    }

    // Helper: forward back to form with restored values
 
    private void forwardBack(HttpServletRequest req, HttpServletResponse resp,
                             String errorMsg,
                             String date, String time, Integer guests, Integer tableId,
                             String ridStr)
            throws ServletException, IOException {

        req.setAttribute("error", errorMsg);
        req.setAttribute("date", date);
        req.setAttribute("time", time);
        req.setAttribute("guests", guests);
        req.setAttribute("tableId", tableId);

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
