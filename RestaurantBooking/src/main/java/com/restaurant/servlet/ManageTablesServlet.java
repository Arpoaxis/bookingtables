package com.restaurant.servlet;

import com.restaurant.util.CSRFUtil;
import com.restaurant.dao.RestaurantTableDao;
import com.restaurant.model.RestaurantTable;
import com.restaurant.model.User;
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
    	//require logged-in restaurant admin
    	User user = (User) req.getSession().getAttribute("user");
    	if (user == null || user.getRestaurantId() == null) {
            req.setAttribute("error", "You must be logged in as a restaurant admin.");
            req.getRequestDispatcher("/WEB-INF/jsp/admin/manage_tables.jsp")
               .forward(req, resp);
            return;
        }
    	int restaurantId = user.getRestaurantId();
        String dbPath = getServletContext().getRealPath("/WEB-INF/database/restBooking.db");
        
        
        try {
            RestaurantTableDao dao = new RestaurantTableDao(dbPath);
            List<RestaurantTable> tables = dao.getTablesByRestaurant(restaurantId);


            req.setAttribute("tables", tables);

            // Flash message (success/error) 
            String msg = req.getParameter("msg");
            if (msg != null && !msg.isBlank()) {
                req.setAttribute("message", msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Failed to load tables: " + e.getMessage());
        }

        // Always ensure CSRF token is available
        String csrfToken = CSRFUtil.getOrCreateToken(req);
        req.setAttribute("csrf_token", csrfToken);

        req.getRequestDispatcher("/WEB-INF/jsp/admin/manage_tables.jsp")
           .forward(req, resp);
    }
}
