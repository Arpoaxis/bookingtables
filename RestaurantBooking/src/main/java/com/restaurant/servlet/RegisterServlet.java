package com.restaurant.servlet;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.restaurant.dao.RegisterDao;
import com.restaurant.util.PasswordUtil;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/login/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get form parameters
        String firstName   = request.getParameter("firstName");
        String lastName    = request.getParameter("lastName");
        String username    = request.getParameter("username");
        String email       = request.getParameter("email");
        String phone       = request.getParameter("phoneNumber");
        String password    = request.getParameter("password");
        String confirmPass = request.getParameter("confirmPassword");
        String accountType = "CUSTOMER"; // default role

        String dbPath = getServletContext().getRealPath("/WEB-INF/database/restBooking.db");

        // Check required fields
        if (isBlank(firstName) || isBlank(lastName) || isBlank(username) ||
            isBlank(email) || isBlank(phone) || isBlank(password) || isBlank(confirmPass)) {
            sendError(request, response, "All fields are required.");
            return;
        }

        // Password match check
        if (!password.equals(confirmPass)) {
            sendError(request, response, "Passwords do not match.");
            return;
        }

        // Password strength
        if (!PasswordUtil.isPasswordStrong(password)) {
            sendError(request, response, PasswordUtil.getPasswordRequirements());
            return;
        }

        // Validate email format
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            sendError(request, response, "Invalid email format.");
            return;
        }

        // Validate phone number
        long phoneNumber;
        try {
            phoneNumber = Long.parseLong(phone);
        } catch (NumberFormatException e) {
            sendError(request, response, "Invalid phone number format.");
            return;
        }

        // Attempt registration
        String status = RegisterDao.register(
                username, email, password, accountType,
                phoneNumber, firstName, lastName, dbPath);

        switch (status) {
            case "EMAIL_EXIST":
                sendError(request, response, "Email already exists.");
                break;

            case "SUCCESS":
                HttpSession session = request.getSession();
                session.setAttribute("email", email);
                response.sendRedirect(request.getContextPath() + "/login");
                break;

            default:
                sendError(request, response, "Registration failed. Please try again.");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private void sendError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("/WEB-INF/jsp/login/register.jsp").forward(request, response);
    }
}
