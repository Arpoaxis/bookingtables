package com.restaurant.servlet;

import com.restaurant.dao.RestaurantDao;
import com.restaurant.model.Restaurant;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/index", "/restaurants"})
public class IndexServlet extends HttpServlet {
	static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<Restaurant> restaurants =
                    RestaurantDao.findAll(getServletContext());

            request.setAttribute("restaurants", restaurants);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Unable to load restaurants.");
        }

        request.getRequestDispatcher("/WEB-INF/jsp/index.jsp")
               .forward(request, response);
    }
}