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

@WebServlet("/admin/manage_tables")
public class ManageTablesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1) Find the DB file in WEB-INF
        String dbPath = getServletContext().getRealPath("/WEB-INF/database/restBooking.db");

        try {
            // 2) Use your DAO to load all tables
            RestaurantTableDao dao = new RestaurantTableDao(dbPath);
            List<RestaurantTable> tables = dao.getAllTables();

            // 3) Put the list on the request
            req.setAttribute("tables", tables);

            // 4) Forward to the JSP view (under WEB-INF)
            req.getRequestDispatcher("/WEB-INF/jsp/admin/manage_tables.jsp")
               .forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            // Simple error handling for now
            req.setAttribute("error", "Failed to load tables: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/admin/manage_tables.jsp")
               .forward(req, resp);
        }
    }
}
