package com.restaurant.servlet;

import com.restaurant.dao.RestaurantTableDao;
import com.restaurant.model.RestaurantTable;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/floor_plan")
public class FloorPlanServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String dbPath = getServletContext()
                .getRealPath("/WEB-INF/database/restBooking.db");

        try {
            RestaurantTableDao dao = new RestaurantTableDao(dbPath);
            List<RestaurantTable> tables = dao.getAllTables();

            // For now we just show all tables as “available”
            req.setAttribute("tables", tables);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error",
                    "Could not load floor plan: " + e.getMessage());
        }

        req.getRequestDispatcher("/WEB-INF/jsp/admin/floor_plan.jsp")
           .forward(req, resp);
    }
}
