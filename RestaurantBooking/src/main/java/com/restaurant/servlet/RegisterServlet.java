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
        request.getRequestDispatcher("/WEB-INF/jsp/Login/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //Get form parameters
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        //String accountType = request.getParameter("account_type");
        String accountType="Customer";
        String phone = request.getParameter("PhoneNumber");
        String firstName = request.getParameter("FirstName");
        String lastName = request.getParameter("LastName");

        String dbPath = getServletContext().getRealPath("/WEB-INF/database/restBooking.db");

        //Validate account type
        if (accountType == null || accountType.isEmpty()) {
            sendError(request, response, "Please select an account type.");
            return;
        }

        //Check password match
        if (password == null || !password.equals(confirmPassword)) {
            sendError(request, response, "Passwords do not match.");
            return;
        }

        //Validate password strength
        if (!PasswordUtil.isPasswordStrong(password)) {
            sendError(request, response, PasswordUtil.getPasswordRequirements());
            return;
        }

        //Validate email format
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            sendError(request, response, "Invalid email format.");
            return;
        }

        //Validate phone number
        long phoneNumber;
        try {
            phoneNumber = Long.parseLong(phone);
        } catch (NumberFormatException e) {
            sendError(request, response, "Invalid phone number format.");
            return;
        }

        //Call DAO to handle registration
        String status = RegisterDao.register(email, password, accountType, phoneNumber, firstName, lastName, dbPath);

        switch (status) {
            case "EMAIL_EXIST":
                sendError(request, response, "Email already exists.");
                break;

            case "SUCCESS":
                HttpSession session = request.getSession();
                session.setAttribute("email", email);
                response.sendRedirect(request.getContextPath() + "/jsp/Login/login_page.jsp");
                break;

            default:
                sendError(request, response, "Registration failed. Please try again.");
                break;
        }
    }

    //forward error message
    private void sendError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("/WEB-INF/jsp/Login/register.jsp").forward(request, response);
    }
}