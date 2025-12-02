package com.restaurant.servlet;

import com.restaurant.dao.UserDao;
import com.restaurant.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/profile/edit")
public class ProfileEditServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/jsp/profile/edit_profile.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");

        // Form values
        String first = request.getParameter("first_name");
        String last = request.getParameter("last_name");
        String phoneStr = request.getParameter("phone_number");

        // Validate
        if (first == null || first.isBlank() || last == null || last.isBlank()) {
            session.setAttribute("error", "Name fields cannot be empty.");
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        long phone = user.getPhoneNumber();
        if (phoneStr != null && !phoneStr.isBlank()) {
            try {
                phone = Long.parseLong(phoneStr.replaceAll("[^0-9]", ""));
            } catch (NumberFormatException e) {
                session.setAttribute("error", "Invalid phone number.");
                response.sendRedirect(request.getContextPath() + "/profile");
                return;
            }
        }

        // DB update
        String dbPath = getServletContext().getRealPath("/WEB-INF/database/restBooking.db");
        UserDao dao = new UserDao(dbPath);

        boolean ok = dao.updateUserProfile(user.getUserId(), first, last, phone);

        if (ok) {
            User updated = dao.getUserById(user.getUserId());
            session.setAttribute("user", updated);
            session.setAttribute("success", "Profile updated successfully.");
        } else {
            session.setAttribute("error", "Failed to update profile.");
        }

        response.sendRedirect(request.getContextPath() + "/profile");
    }
}
