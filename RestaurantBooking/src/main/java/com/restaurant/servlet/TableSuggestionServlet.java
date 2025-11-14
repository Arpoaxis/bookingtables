package com.restaurant.servlet;

import com.restaurant.dao.RestaurantTableDao;
import com.restaurant.model.RestaurantTable;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@WebServlet("/admin/table_planner")
public class TableSuggestionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private RestaurantTableDao buildDao(HttpServletRequest req) {
        String dbPath = getServletContext().getRealPath("/WEB-INF/database/restBooking.db");
        return new RestaurantTableDao(dbPath);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        RestaurantTableDao dao = buildDao(req);
        List<RestaurantTable> allTables = dao.getAllTables();
        req.setAttribute("tables", allTables);

        req.getRequestDispatcher("/WEB-INF/jsp/admin/table_planner.jsp")
           .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String sizeStr = req.getParameter("partySize");
        RestaurantTableDao dao = buildDao(req);
        List<RestaurantTable> allTables = dao.getAllTables();
        req.setAttribute("tables", allTables); // always show full list

        if (sizeStr == null || sizeStr.isBlank()) {
            req.setAttribute("error", "Please enter a party size.");
            req.getRequestDispatcher("/WEB-INF/jsp/admin/table_planner.jsp")
               .forward(req, resp);
            return;
        }

        try {
            int partySize = Integer.parseInt(sizeStr.trim());
            if (partySize <= 0) {
                req.setAttribute("error", "Party size must be at least 1.");
            } else {
                List<RestaurantTable> suggestion = suggestTables(allTables, partySize);
                req.setAttribute("partySize", partySize);
                req.setAttribute("suggestedTables", suggestion);
            }
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Party size must be a number.");
        }

        req.getRequestDispatcher("/WEB-INF/jsp/admin/table_planner.jsp")
           .forward(req, resp);
    }

    /**
     * Simple greedy suggestion:
     *  1. Try to find a single table where min <= partySize <= max.
     *  2. If not, sort combinable tables by max_capacity ascending
     *     and add until total >= partySize.
     */
    private List<RestaurantTable> suggestTables(List<RestaurantTable> allTables, int partySize) {
        List<RestaurantTable> result = new ArrayList<>();

        // 1) Perfect single table?
        for (RestaurantTable t : allTables) {
            if (partySize >= t.getMinCapacity() && partySize <= t.getMaxCapacity()) {
                result.add(t);
                return result;
            }
        }

        // 2) Need combination – only tables that can_combine = true
        List<RestaurantTable> combinable = new ArrayList<>();
        for (RestaurantTable t : allTables) {
            if (t.isCanCombine()) {
                combinable.add(t);
            }
        }

        combinable.sort(Comparator.comparingInt(RestaurantTable::getMaxCapacity));

        int total = 0;
        for (RestaurantTable t : combinable) {
            result.add(t);
            total += t.getMaxCapacity();
            if (total >= partySize) {
                break;
            }
        }

        // If even all combinable tables can't seat them, still return what we have (manager sees limitation)
        return result;
    }
}
