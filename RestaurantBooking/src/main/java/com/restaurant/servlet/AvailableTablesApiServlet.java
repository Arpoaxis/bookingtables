package com.restaurant.servlet;

import com.restaurant.dao.RestaurantTableDao;
import com.restaurant.model.RestaurantTable;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/available-tables")
public class AvailableTablesApiServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String ridStr    = req.getParameter("restaurantId");
        String date      = req.getParameter("date");
        String time      = req.getParameter("time");
        String guestsStr = req.getParameter("guests");

        int restaurantId;
        int guests;

        try {
            restaurantId = Integer.parseInt(ridStr);
            guests       = Integer.parseInt(guestsStr);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad parameters");
            return;
        }

        // Same DB path you use elsewhere
        String dbPath = req.getServletContext()
                           .getRealPath("/WEB-INF/database/restBooking.db");
        RestaurantTableDao dao = new RestaurantTableDao(dbPath);

        List<RestaurantTable> tables;
        try {
            tables = dao.getAvailableTables(restaurantId, date, time, guests);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < tables.size(); i++) {
            RestaurantTable t = tables.get(i);
            if (i > 0) sb.append(',');

            sb.append('{')
              .append("\"tableId\":").append(t.getTableId()).append(',')
              .append("\"tableNumber\":").append(t.getTableNumber()).append(',')
              .append("\"minCapacity\":").append(t.getMinCapacity()).append(',')
              .append("\"maxCapacity\":").append(t.getMaxCapacity()).append(',')
              .append("\"canCombine\":").append(t.isCanCombine() ? "true" : "false")
              .append('}');
        }
        sb.append(']');

        resp.getWriter().write(sb.toString());
    }
}
