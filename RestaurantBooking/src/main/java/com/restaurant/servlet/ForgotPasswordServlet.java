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

        // Do not reveal whether this email address has an account.
        String genericMessage =
                "If an account exists and email delivery is configured, a temporary password will be sent.";

        if (user != null) {
            try {
                String temporaryPassword = PasswordUtil.generateTemporaryPassword();
                String message = """
                        Hi %s,

                        A password reset was requested for your RestaurantBooking account.

                        Temporary password: %s

                        Sign in and change your password as soon as possible.
                        If you did not request this, contact the application administrator.

                        – Restaurant Booking
                        """.formatted(user.getFirstName(), temporaryPassword);

                // Do not invalidate the old password when SMTP is missing or fails.
                // A full production workflow would use a time-limited reset token.
                boolean delivered = EmailService.sendSimpleEmail(
                        email, "RestaurantBooking password reset", message);
                if (delivered) {
                    userDao.resetUserPassword(user.getUserId(),
                            PasswordUtil.hashPassword(temporaryPassword));
                }
            } catch (Exception ex) {
                // Preserve the same browser response on account lookup/failure.
                ex.printStackTrace();
            }
        }

        session.setAttribute("success", genericMessage);
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
