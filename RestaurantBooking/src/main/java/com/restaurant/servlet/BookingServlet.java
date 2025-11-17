package com.restaurant.servlet;

import com.restaurant.dao.RestaurantDao;
import com.restaurant.dao.BookingDao;
import com.restaurant.model.Restaurant;
import com.restaurant.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet({"/booking/new", "/booking/create"})
public class BookingServlet extends HttpServlet {
		private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idStr = req.getParameter("restaurantId");
        if (idStr == null) {
            resp.sendRedirect(req.getContextPath() + "/restaurants");
            return;
        }

        int restaurantId = Integer.parseInt(idStr);

        try {
            Restaurant r = RestaurantDao.findById(req.getServletContext(), restaurantId);
            req.setAttribute("restaurant", r);
        } catch (Exception e) {
            req.setAttribute("error", "Unable to load restaurant.");
        }

        req.getRequestDispatcher("/WEB-INF/jsp/booking/new_booking.jsp")
                .forward(req, resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // User must be logged in
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Retrieve logged-in user
        User user = (User) session.getAttribute("user");
        int userId = user.getUserId(); 

        int restaurantId = Integer.parseInt(req.getParameter("restaurantId"));
        int guests = Integer.parseInt(req.getParameter("guests"));
        String date = req.getParameter("date");
        String time = req.getParameter("time");
        String requestsText = req.getParameter("requests");

        try {
            BookingDao.createBooking(
                    req.getServletContext(),
                    userId,
                    restaurantId,
                    guests,
                    date,
                    time,
                    requestsText
            );

            resp.sendRedirect(req.getContextPath() + "/booking/confirmation");
        } catch (Exception e) {
            req.setAttribute("error", "Failed to create booking.");
            req.getRequestDispatcher("/WEB-INF/jsp/booking/new_booking.jsp")
                    .forward(req, resp);
        }
    }
}
