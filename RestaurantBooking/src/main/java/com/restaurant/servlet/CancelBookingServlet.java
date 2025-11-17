package com.restaurant.servlet;

import com.restaurant.dao.BookingDao;
import com.restaurant.model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;

import java.io.IOException;

@WebServlet("/booking/cancel")
public class CancelBookingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        

        User user = (User) session.getAttribute("user");
        int bookingId = Integer.parseInt(req.getParameter("bookingId"));

        try {
            BookingDao.cancelBooking(getServletContext(), bookingId, user.getUserId());
        } catch (Exception e) {
            req.setAttribute("error", "Failed to cancel reservation.");
        }

        resp.sendRedirect(req.getContextPath() + "/booking/mine");
    }
}
