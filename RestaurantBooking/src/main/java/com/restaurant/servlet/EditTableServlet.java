package com.restaurant.servlet;

import com.restaurant.dao.RestaurantTableDao;
import com.restaurant.model.RestaurantTable;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/admin/edit_table")
public class EditTableServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isBlank()) {
            req.getSession().setAttribute("tableError", "Missing table ID.");
            resp.sendRedirect(req.getContextPath() + "/admin/tables");
            return;
        }

        try {
            int tableId = Integer.parseInt(idStr.trim());
            String dbPath = getServletContext().getRealPath("/WEB-INF/database/restBooking.db");
            RestaurantTableDao dao = new RestaurantTableDao(dbPath);

            RestaurantTable table = dao.getTableById(tableId);
            if (table == null) {
                req.getSession().setAttribute("tableError", "Table not found.");
                resp.sendRedirect(req.getContextPath() + "/admin/tables");
                return;
            }

            req.setAttribute("table", table);
            req.getRequestDispatcher("/WEB-INF/jsp/admin/edit_table.jsp")
               .forward(req, resp);

        } catch (NumberFormatException e) {
            req.getSession().setAttribute("tableError", "Invalid table ID.");
            resp.sendRedirect(req.getContextPath() + "/admin/tables");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr    = request.getParameter("tableId");
        String numStr   = request.getParameter("tableNumber");
        String minStr   = request.getParameter("minCapacity");
        String maxStr   = request.getParameter("maxCapacity");
        String combStr  = request.getParameter("canCombine");

        // basic validation
        if (idStr == null || numStr == null || minStr == null || maxStr == null ||
            idStr.isBlank() || numStr.isBlank() || minStr.isBlank() || maxStr.isBlank()) {

            request.setAttribute("error", "All numeric fields are required.");
            // We need the table again to repopulate the form
            try {
                int tableId = Integer.parseInt(idStr.trim());
                String dbPath = getServletContext().getRealPath("/WEB-INF/database/restBooking.db");
                RestaurantTableDao dao = new RestaurantTableDao(dbPath);
                RestaurantTable t = dao.getTableById(tableId);
                request.setAttribute("table", t);
            } catch (Exception ignored) {}
            request.getRequestDispatcher("/WEB-INF/jsp/admin/edit_table.jsp")
                   .forward(request, response);
            return;
        }

        try {
            int tableId     = Integer.parseInt(idStr.trim());
            int tableNumber = Integer.parseInt(numStr.trim());
            int minCapacity = Integer.parseInt(minStr.trim());
            int maxCapacity = Integer.parseInt(maxStr.trim());
            boolean canCombine = (combStr != null);

            // simple logical check: min <= max
            if (minCapacity > maxCapacity) {
                request.setAttribute("error", "Min capacity cannot be greater than max capacity.");
                RestaurantTable t = new RestaurantTable(tableId, tableNumber, minCapacity, maxCapacity, canCombine);
                request.setAttribute("table", t);
                request.getRequestDispatcher("/WEB-INF/jsp/admin/edit_table.jsp")
                       .forward(request, response);
                return;
            }

            String dbPath = getServletContext().getRealPath("/WEB-INF/database/restBooking.db");
            RestaurantTableDao dao = new RestaurantTableDao(dbPath);

            RestaurantTable updated = new RestaurantTable(
                    tableId,
                    tableNumber,
                    minCapacity,
                    maxCapacity,
                    canCombine
            );

            boolean success = dao.updateTable(updated);
            if (success) {
                request.getSession().setAttribute("tableMessage", "Table updated successfully.");
            } else {
                request.getSession().setAttribute("tableError", "Failed to update table.");
            }

            response.sendRedirect(request.getContextPath() + "/admin/tables");

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid numeric values.");
            request.getRequestDispatcher("/WEB-INF/jsp/admin/edit_table.jsp")
                   .forward(request, response);
        }
    }
}
