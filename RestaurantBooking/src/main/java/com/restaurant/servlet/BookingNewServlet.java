package com.restaurant.servlet;

import com.restaurant.dao.RestaurantDao;
import com.restaurant.model.Restaurant;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;


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

        // Today for the date min/value
        LocalDate today = LocalDate.now(ZoneId.of("America/Los_Angeles"));
        req.setAttribute("today", today.toString());

        // Build the list of time slots (11:30–22:00 every 15 min)
        List<String> timeSlots = new ArrayList<>();
        LocalTime start = LocalTime.of(11, 30);
        LocalTime end   = LocalTime.of(22, 0);

        for (LocalTime t = start; !t.isAfter(end); t = t.plusMinutes(15)) {
            // Store as "HH:mm" (e.g. "17:45")
            timeSlots.add(t.toString());
        }
        req.setAttribute("timeSlots", timeSlots);

        req.getRequestDispatcher("/WEB-INF/jsp/booking/new_booking.jsp")
           .forward(req, resp);
    }
}
