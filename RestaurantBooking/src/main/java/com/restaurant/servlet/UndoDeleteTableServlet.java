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

@WebServlet("/admin/undo_delete_table")
public class UndoDeleteTableServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/manage_tables");
            return;
        }

        Object obj = session.getAttribute("lastDeletedTable");
        if (!(obj instanceof RestaurantTable)) {
            session.setAttribute("tableError", "No recent delete to undo.");
            resp.sendRedirect(req.getContextPath() + "/admin/manage_tables");
            return;
        }

        RestaurantTable last = (RestaurantTable) obj;

        try {
            String dbPath = getServletContext().getRealPath("/WEB-INF/database/restBooking.db");
            RestaurantTableDao dao = new RestaurantTableDao(dbPath);

            // Re-insert the table (see DAO method below)
            boolean success = dao.reinsertTable(last);

            if (success) {
                session.removeAttribute("lastDeletedTable");
                session.setAttribute(
                        "tableMessage",
                        "Restored table " + last.getTableNumber() + "."
                );
            } else {
                session.setAttribute("tableError", "Could not restore table.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("tableError", "Error restoring table: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/manage_tables");
    }
}
