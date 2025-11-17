package com.restaurant.servlet;

import com.restaurant.dao.BookingDao;
import com.restaurant.model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;

import java.io.IOException;

@WebServlet("/booking/mine")
public class MyReservationsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");

        try {
            req.setAttribute("bookings",
                BookingDao.getBookingsForUser(getServletContext(), user.getUserId())
            );
        } catch (Exception e) {
            req.setAttribute("error", "Unable to load reservations.");
        }

        req.getRequestDispatcher("/WEB-INF/jsp/booking/my_reservations.jsp")
            .forward(req, resp);
    }
}
