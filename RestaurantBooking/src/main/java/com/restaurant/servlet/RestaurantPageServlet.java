package com.restaurant.servlet;

import com.restaurant.dao.RestaurantDao;
import com.restaurant.model.Restaurant;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;

import java.io.IOException;

@WebServlet("/restaurant")
public class RestaurantPageServlet extends HttpServlet {
		private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam = req.getParameter("id");

        if (idParam == null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        int id = Integer.parseInt(idParam);

        try {
            Restaurant restaurant =
                    RestaurantDao.findById(getServletContext(), id);

            if (restaurant == null) {
                req.setAttribute("error", "Restaurant not found.");
                req.getRequestDispatcher("/WEB-INF/jsp/error.jsp")
                        .forward(req, resp);
                return;
            }

            req.setAttribute("restaurant", restaurant);
            req.getRequestDispatcher("/WEB-INF/jsp/restaurant/RestaurantPage.jsp")
                    .forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", "Unable to load restaurant.");
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp")
                    .forward(req, resp);
        }
    }
}
