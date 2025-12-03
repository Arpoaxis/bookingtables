package com.restaurant.servlet;

import com.restaurant.dao.UserDao;
import com.restaurant.model.User;
import com.restaurant.service.EmailService;
import com.restaurant.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/login/forgot")
public class ForgotPasswordServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/jsp/login/forgot_password.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        HttpSession session = request.getSession();

        if (email == null || email.isBlank()) {
            request.setAttribute("error", "Please enter your email address.");
            request.getRequestDispatcher("/WEB-INF/jsp/login/forgot_password.jsp")
                   .forward(request, response);
            return;
        }

        email = email.trim().toLowerCase();

        UserDao userDao = new UserDao(getServletContext());
        User user = userDao.findByEmail(email);

        if (user == null) {
            // Always show the same success message whether or not user exists
            session.setAttribute("success",
                    "If an account exists for that email, we've sent a reset email with a temporary password.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }


        // Simple reset strategy: set password to "changeme"
        String tempPassword = "changeme";

        try {
        	String tempHash = PasswordUtil.hashPassword(tempPassword);
        	userDao.resetUserPassword(user.getUserId(), tempHash);


            String subject = "Your RestaurantBooking password has been reset";
            String body = """
                Hi %s,

                Your password has been reset.

                Temporary password: %s

                Please log in and change your password as soon as possible.

                – Restaurant Booking
                """.formatted(user.getFirstName(), tempPassword);

            // Use the shared EmailService
            EmailService.sendSimpleEmail(email, subject, body);

            session.setAttribute("success",
                    "If an account exists for that email, we've sent a reset email with a temporary password.");
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute("error", "Could not reset password. Please try again later.");
            request.getRequestDispatcher("/WEB-INF/jsp/login/forgot_password.jsp")
                   .forward(request, response);
        }
    }
}
