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

@WebServlet("/booking/create")
public class BookingCreateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        // Require login
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        int userId = user.getUserId();

        // Read request data
        int restaurantId = Integer.parseInt(req.getParameter("restaurantId"));
        int guests = Integer.parseInt(req.getParameter("guests"));
        String date = req.getParameter("date");
        String time = req.getParameter("time");
        String requests = req.getParameter("requests");

        try {
            // Save booking
            BookingDao.createBooking(
                    req.getServletContext(),
                    userId,
                    restaurantId,
                    guests,
                    date,
                    time,
                    requests
            );

            // Load restaurant info for email
            Restaurant r = RestaurantDao.findById(req.getServletContext(), restaurantId);

            // Send email
            EmailService.sendBookingConfirmation(
                    user.getEmail(),
                    r.getName(),
                    date,
                    time,
                    guests
            );

            // Redirect user
            resp.sendRedirect(req.getContextPath() + "/booking/confirmation");

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Failed to create booking.");
            req.getRequestDispatcher("/WEB-INF/jsp/booking/new_booking.jsp")
                    .forward(req, resp);
        }
    }
}
