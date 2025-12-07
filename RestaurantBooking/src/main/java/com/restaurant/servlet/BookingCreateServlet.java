package com.restaurant.servlet;

import com.restaurant.dao.BookingDao;
import com.restaurant.dao.RestaurantDao;
import com.restaurant.dao.RestaurantTableDao;
import com.restaurant.model.Restaurant;
import com.restaurant.model.User;
import com.restaurant.service.EmailService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

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

        // Basic parameters
        Integer restaurantId = Integer.valueOf(req.getParameter("restaurantId"));
        String date = req.getParameter("date");
        String time = req.getParameter("time");
        String requests = req.getParameter("requests");

        int guests = 1;
        try { guests = Integer.parseInt(req.getParameter("guests")); }
        catch (Exception ignored) {}

       
        String dbPath = req.getServletContext().getRealPath("/WEB-INF/database/restBooking.db");
        RestaurantTableDao tdao = new RestaurantTableDao(dbPath);

        Integer tableId = tdao.findAvailableTable(restaurantId, date, time, guests);

        if (tableId == null) {
            forwardBack(req, resp,
                    "No tables available.",
                    date, time, guests, null,
                    restaurantId.toString());
            return;
        }

       
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

        } catch (Exception e) {

            forwardBack(req, resp,
                    "Failed to create your booking.",
                    date, time, guests, null,
                    restaurantId.toString());
            return;
        }
    }

    private void forwardBack(HttpServletRequest req, HttpServletResponse resp,
                             String errorMsg,
                             String date, String time, Integer guests, Integer tableId,
                             String ridStr)
            throws ServletException, IOException {

        req.setAttribute("error", errorMsg);
        req.setAttribute("date", date);
        req.setAttribute("time", time);
        req.setAttribute("guests", guests);

        try {
            int rid = Integer.parseInt(ridStr);
            Restaurant r = RestaurantDao.findById(req.getServletContext(), rid);
            req.setAttribute("restaurant", r);
        } catch (Exception ex) { }

        req.getRequestDispatcher("/WEB-INF/jsp/booking/new_booking.jsp")
                .forward(req, resp);
    }
}
