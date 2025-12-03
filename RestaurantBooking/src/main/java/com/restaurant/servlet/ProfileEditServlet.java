package com.restaurant.servlet;

import com.restaurant.dao.UserDao;
import com.restaurant.model.User;

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

        // Optionally, pass the user to the edit JSP
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

        String firstName = request.getParameter("first_name");
        String lastName  = request.getParameter("last_name");
        String phoneStr  = request.getParameter("phoneNumber");

        String errorMessage = null;
        long phone = 0L;

        // Basic validation
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
            request.setAttribute("first_name", firstName);
            request.setAttribute("last_name", lastName);
            request.setAttribute("phoneNumber", phoneStr);
            request.getRequestDispatcher("/WEB-INF/jsp/profile/edit_profile.jsp")
                   .forward(request, response);
            return;
        }

        try {
            UserDao userDao = new UserDao(getServletContext());
            userDao.updateUserProfile(sessionUser.getUserId(), firstName, lastName, phone);

            // Reload updated user from DB
            User updatedUser = userDao.getUserById(sessionUser.getUserId());
            session.setAttribute("user", updatedUser);

            session.setAttribute("success", "Profile updated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Failed to update profile.");
        }

        response.sendRedirect(request.getContextPath() + "/profile");
    }
}
