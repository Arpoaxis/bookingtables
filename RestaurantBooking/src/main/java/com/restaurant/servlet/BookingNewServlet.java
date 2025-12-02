package com.restaurant.servlet;

import com.restaurant.dao.RestaurantDao;
import com.restaurant.model.Restaurant;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/booking/new")
public class BookingNewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idStr = req.getParameter("restaurantId");
        if (idStr == null) {
            resp.sendRedirect(req.getContextPath() + "/restaurants");
            return;
        }

        try {
            int restaurantId = Integer.parseInt(idStr);
            Restaurant r = RestaurantDao.findById(req.getServletContext(), restaurantId);
            req.setAttribute("restaurant", r);
        } catch (Exception e) {
            req.setAttribute("error", "Unable to load restaurant.");
        }

        req.getRequestDispatcher("/WEB-INF/jsp/booking/new_booking.jsp")
                .forward(req, resp);
    }
}
