package com.restaurant.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/booking/start")
public class BookingStartServlet extends HttpServlet {
		private static final long serialVersionUID = 1L;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int restaurantId = Integer.parseInt(req.getParameter("restaurantId"));

        HttpSession session = req.getSession(false);

        // Not logged in → redirect to login WITH message + return URL
        if (session == null || session.getAttribute("user") == null) {

            session = req.getSession(true);
            session.setAttribute("loginMessage", "You must log in before making a reservation.");
            session.setAttribute("returnAfterLogin", "/booking/new?restaurantId=" + restaurantId);

            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Logged in → go directly to booking form
        resp.sendRedirect(req.getContextPath() + "/booking/new?restaurantId=" + restaurantId);
    }
}
