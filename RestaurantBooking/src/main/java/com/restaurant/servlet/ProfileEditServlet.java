package com.restaurant.servlet;

import com.restaurant.dao.UserDao;
import com.restaurant.model.User;
import com.restaurant.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/profile/edit")
public class ProfileEditServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        request.setAttribute("user", user);

        request.getRequestDispatcher("/WEB-INF/jsp/profile/edit_profile.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int userId = sessionUser.getUserId();

        String firstName = request.getParameter("first_name");
        String lastName  = request.getParameter("last_name");
        String phoneStr  = request.getParameter("phoneNumber");

        String errorMessage = null;
        long phone = 0L;

        // ---------- Basic profile validation ----------
        if (firstName == null || firstName.trim().isEmpty()) {
            errorMessage = "First name is required.";
        } else if (lastName == null || lastName.trim().isEmpty()) {
            errorMessage = "Last name is required.";
        } else if (phoneStr == null || phoneStr.trim().isEmpty()) {
            errorMessage = "Phone number is required.";
        } else {
            try {
                phone = Long.parseLong(phoneStr.replaceAll("\\D", "")); // strip non-digits
            } catch (NumberFormatException e) {
                errorMessage = "Invalid phone number.";
            }
        }

        if (errorMessage != null) {
            request.setAttribute("error", errorMessage);
            request.setAttribute("user", sessionUser);
            request.getRequestDispatcher("/WEB-INF/jsp/profile/edit_profile.jsp")
                   .forward(request, response);
            return;
        }

        try {
            UserDao userDao = new UserDao(getServletContext());

            // ---------- 1) Update basic profile info ----------
            boolean updated = userDao.updateUserProfile(userId, firstName, lastName, phone);
            if (!updated) {
                request.setAttribute("error", "Failed to update profile.");
                request.setAttribute("user", sessionUser);
                request.getRequestDispatcher("/WEB-INF/jsp/profile/edit_profile.jsp")
                       .forward(request, response);
                return;
            }

            // ---------- 2) Optional password change ----------
            String currentPassword = request.getParameter("current_password");
            String newPassword     = request.getParameter("new_password");
            String confirmPassword = request.getParameter("confirm_password");

            boolean wantsPasswordChange =
                    (currentPassword != null && !currentPassword.isBlank()) ||
                    (newPassword != null && !newPassword.isBlank()) ||
                    (confirmPassword != null && !confirmPassword.isBlank());

            if (wantsPasswordChange) {

                // All 3 fields must be filled
                if (currentPassword == null || currentPassword.isBlank() ||
                    newPassword == null   || newPassword.isBlank()   ||
                    confirmPassword == null || confirmPassword.isBlank()) {

                    request.setAttribute("error",
                            "To change your password, fill in current, new, and confirmation fields.");
                    request.setAttribute("user", sessionUser);
                    request.getRequestDispatcher("/WEB-INF/jsp/profile/edit_profile.jsp")
                           .forward(request, response);
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    request.setAttribute("error",
                            "New password and confirmation do not match.");
                    request.setAttribute("user", sessionUser);
                    request.getRequestDispatcher("/WEB-INF/jsp/profile/edit_profile.jsp")
                           .forward(request, response);
                    return;
                }

                // Use your PasswordUtil strength rules
                if (!PasswordUtil.isPasswordStrong(newPassword)) {
                    request.setAttribute("error", PasswordUtil.getPasswordRequirements());
                    request.setAttribute("user", sessionUser);
                    request.getRequestDispatcher("/WEB-INF/jsp/profile/edit_profile.jsp")
                           .forward(request, response);
                    return;
                }

                // Check current password against stored hash
                String storedHash = userDao.getPasswordForUser(userId);
                if (storedHash == null ||
                        !PasswordUtil.verifyPassword(currentPassword, storedHash)) {
                    request.setAttribute("error", "Current password is incorrect.");
                    request.setAttribute("user", sessionUser);
                    request.getRequestDispatcher("/WEB-INF/jsp/profile/edit_profile.jsp")
                           .forward(request, response);
                    return;
                }

                // Hash new password and store it
                String newHash = PasswordUtil.hashPassword(newPassword);
                userDao.resetUserPassword(userId, newHash);
            }

            // ---------- 3) Reload user and update session ----------
            User updatedUser = userDao.getUserById(userId);
            session.setAttribute("user", updatedUser);

            String msg = wantsPasswordChange
                    ? "Profile updated and password changed."
                    : "Profile updated successfully.";
            session.setAttribute("success", msg);

            response.sendRedirect(request.getContextPath() + "/profile");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to update profile.");
            request.setAttribute("user", sessionUser);
            request.getRequestDispatcher("/WEB-INF/jsp/profile/edit_profile.jsp")
                   .forward(request, response);
        }
    }
}
