package com.restaurant.servlet;

import com.restaurant.dao.BookingDao;
import com.restaurant.dao.RestaurantTableDao;
import com.restaurant.model.RestaurantTable;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/floor_plan")
public class FloorPlanServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Integer restaurantId = (session != null)
                ? (Integer) session.getAttribute("restaurantId")
                : null;

        if (restaurantId == null) {
            restaurantId = 1; // default
        }

        String dbPath = getServletContext()
                .getRealPath("/WEB-INF/database/restBooking.db");

        String dateParam = req.getParameter("date");
        String selectedDate = (dateParam != null && !dateParam.isEmpty())
                ? dateParam
                : java.time.LocalDate.now().toString();

        try {
            RestaurantTableDao tableDao = new RestaurantTableDao(dbPath);
            List<RestaurantTable> tables = tableDao.getAllTables();

            BookingDao bookingDao = new BookingDao(getServletContext());
            Map<Integer, String> tableStatusMap =
                    bookingDao.getTableStatusMap(restaurantId, selectedDate);

            req.setAttribute("tables", tables);
            req.setAttribute("tableStatusMap", tableStatusMap);
            req.setAttribute("selectedDate", selectedDate);
            req.setAttribute("restaurantId", restaurantId);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Could not load floor plan: " + e.getMessage());
        }

        // Decide which JSP to use
        boolean embedded = "true".equalsIgnoreCase(req.getParameter("embedded"));
        String jsp = embedded
                ? "/WEB-INF/jsp/admin/floor_plan_embed.jsp"
                : "/WEB-INF/jsp/admin/floor_plan.jsp";

        req.getRequestDispatcher(jsp).forward(req, resp);
    }
}

