package com.restaurant.servlet;

import com.restaurant.dao.RestaurantTableDao;
import com.restaurant.model.RestaurantTable;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/admin/delete_table")
public class DeleteTableServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

 // DeleteTableServlet.java
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idStr = req.getParameter("tableId");
        if (idStr == null || idStr.isBlank()) {
            req.getSession().setAttribute("tableError", "Missing table id.");
            resp.sendRedirect(req.getContextPath() + "/admin/manage_tables");
            return;
        }

        try {
            int tableId = Integer.parseInt(idStr);
            String dbPath = getServletContext().getRealPath("/WEB-INF/database/restBooking.db");
            RestaurantTableDao dao = new RestaurantTableDao(dbPath);

            // 1) Load the table we’re about to delete
            RestaurantTable toDelete = dao.getTableById(tableId);

            boolean success = dao.deleteTable(tableId);

            if (success && toDelete != null) {
                // 2) Store last deleted table in session for undo
                req.getSession().setAttribute("lastDeletedTable", toDelete);
                req.getSession().setAttribute("tableMessage",
                        "Table " + toDelete.getTableNumber() + " deleted. You can undo below.");
            } else {
                req.getSession().setAttribute("tableError", "Could not delete table.");
            }

        } catch (NumberFormatException e) {
            req.getSession().setAttribute("tableError", "Invalid table id.");
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("tableError", "Error deleting table: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/manage_tables");
    }

    
}
