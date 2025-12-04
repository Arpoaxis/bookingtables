package com.restaurant.servlet;

import com.restaurant.dao.WaitlistDao;
import com.restaurant.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/staff/waitlist")
public class StaffWaitlistActionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        String role = user.getAccountType();

        // Only staff roles allowed
        if (!"HOST".equalsIgnoreCase(role)
                && !"MANAGER".equalsIgnoreCase(role)
                && !"ADMIN".equalsIgnoreCase(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // CSRF is handled by your CSRFFilter, we just pass the token along in the form.

        String idStr = request.getParameter("waitlistId");
        String action = request.getParameter("action");

        if (idStr == null || action == null) {
            response.sendRedirect(request.getContextPath() + "/staff/dashboard");
            return;
        }

        int waitlistId;
        try {
            waitlistId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/staff/dashboard");
            return;
        }

        String newStatus;
        switch (action) {
            case "notify" -> newStatus = "NOTIFIED";
            case "seat"   -> newStatus = "SEATED";
            case "cancel" -> newStatus = "CANCELLED";
            default -> {
                response.sendRedirect(request.getContextPath() + "/staff/dashboard");
                return;
            }
        }

        try {
            WaitlistDao dao = new WaitlistDao(getServletContext());
            dao.updateStatus(waitlistId, newStatus, user.getUserId());
            session.setAttribute("success", "Waitlist updated: " + newStatus);
        } catch (Exception ex) {
            ex.printStackTrace();
            session.setAttribute("error", "Could not update waitlist entry.");
        }

        response.sendRedirect(request.getContextPath() + "/staff/dashboard");
    }
}
