package com.restaurant.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.RequestDispatcher;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.restaurant.util.DatabaseUtility;
import com.restaurant.model.RestaurantTable;

@WebServlet("/admin/tables")
public class ViewTablesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Simple access check: require a logged-in user
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<RestaurantTable> tables = new ArrayList<>();

        try (Connection conn = DatabaseUtility.getConnection(getServletContext())) {

            String sql = """
                SELECT table_id, table_number, min_capacity, max_capacity, can_combine
                FROM restaurant_tables
                ORDER BY table_number
            """;

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    int tableId      = rs.getInt("table_id");
                    int tableNumber  = rs.getInt("table_number");
                    int minCapacity  = rs.getInt("min_capacity");
                    int maxCapacity  = rs.getInt("max_capacity");
                    int canCombineDb = rs.getInt("can_combine");

                    boolean canCombine = (canCombineDb == 1);

                    RestaurantTable t = new RestaurantTable(
                            tableId,
                            tableNumber,
                            minCapacity,
                            maxCapacity,
                            canCombine
                    );
                    tables.add(t);
                }
            }

            // Put the list into the request
            req.setAttribute("tables", tables);

        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("tableError", "Unable to load restaurant tables.");
        }

        // Forward to JSP view
        RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/jsp/admin/view_tables.jsp");
        rd.forward(req, resp);
    }
}
