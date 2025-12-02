package com.restaurant.servlet;

import com.restaurant.dao.RestaurantDao;
import com.restaurant.model.Restaurant;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {
		private static final long serialVersionUID = 1L;
    @Override	
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String q = request.getParameter("q");
        // If query is empty, return empty results
        if (q == null || q.trim().isEmpty()) {
            request.setAttribute("results", List.of());
            request.getRequestDispatcher("/WEB-INF/jsp/search/search_results.jsp")
                   .forward(request, response);
            return;
        }
        // Perform search
        try {
            List<Restaurant> results = RestaurantDao.search(getServletContext(), q.trim());
            request.setAttribute("results", results);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("results", List.of());
        }
        // Forward to results page
        request.setAttribute("query", q);
        request.getRequestDispatcher("/WEB-INF/jsp/search/search_results.jsp")
               .forward(request, response);
    }
}
