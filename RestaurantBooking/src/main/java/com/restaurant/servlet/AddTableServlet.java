package com.restaurant.servlet;

import com.restaurant.dao.RestaurantTableDao;
import com.restaurant.model.RestaurantTable;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/admin/add_table")
public class AddTableServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Just show the JSP form
        request.getRequestDispatcher("/WEB-INF/jsp/admin/add_table.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tableNumStr   = request.getParameter("tableNumber");
        String minStr        = request.getParameter("minCapacity");
        String maxStr        = request.getParameter("maxCapacity");
        String canCombineStr = request.getParameter("canCombine");

        // 1) Basic required-field validation
        if (tableNumStr == null || minStr == null || maxStr == null ||
            tableNumStr.isEmpty() || minStr.isEmpty() || maxStr.isEmpty()) {

            request.setAttribute("error", "All numeric fields are required.");
            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/admin/add_table.jsp");
            rd.forward(request, response);
            return;
        }

        try {
            int tableNumber = Integer.parseInt(tableNumStr.trim());
            int minCapacity = Integer.parseInt(minStr.trim());
            int maxCapacity = Integer.parseInt(maxStr.trim());
            boolean canCombine = (canCombineStr != null);

            // 2) Build model – uses the 4-arg constructor we added
            RestaurantTable table =
                    new RestaurantTable(tableNumber, minCapacity, maxCapacity, canCombine);

            // 3) Call DAO
            String dbPath = getServletContext().getRealPath("/WEB-INF/database/restBooking.db");
            RestaurantTableDao dao = new RestaurantTableDao(dbPath);

            boolean success = dao.addTable(table);
            if (success) {
                request.getSession().setAttribute("tableMessage", "Table added successfully!");
            } else {
                request.getSession().setAttribute(
                        "tableError",
                        "Failed to add table. Check if the table number already exists."
                );
            }

            // 4) Redirect to avoid form resubmission
            response.sendRedirect(request.getContextPath() + "/jsp/admin/add_table.jsp");
            return;

        } catch (NumberFormatException e) {
            // Input was not numeric
            request.setAttribute("error", "Invalid input. Please enter valid numbers.");
            RequestDispatcher rd =
                    request.getRequestDispatcher("/WEB-INF/jsp/admin/add_table.jsp");
            rd.forward(request, response);
        }
    }
}
