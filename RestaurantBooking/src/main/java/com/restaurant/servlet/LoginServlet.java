package com.restaurant.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.restaurant.dao.LoginDao;
import com.restaurant.model.User;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/jsp/login/login_page.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        String dbpath = getServletContext()
                .getRealPath("/WEB-INF/database/restBooking.db");

        // Authenticate
        User user = LoginDao.validate(email, password, dbpath);

        if (user == null) {
            System.out.println("[LoginServlet] authentication FAILED for '" + email + "'");
            request.setAttribute("error", "Invalid email or password.");
            request.getRequestDispatcher("/WEB-INF/jsp/login/login_page.jsp")
                   .forward(request, response);
            return;
        }

        System.out.println("[LoginServlet] authentication SUCCESS for '"
                + email + "' role='" + user.getAccountType() + "'");

        // Never keep raw password in session
        user.setPassword(null);

        // Session fixation protection
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) oldSession.invalidate();
        HttpSession session = request.getSession(true);

        // Basic session attributes
        session.setAttribute("user", user);
        session.setAttribute("email", user.getEmail());
        session.setAttribute("role", user.getAccountType());
        session.setAttribute("userId", user.getUserId());

        // Prefer restaurant_id from DB (User model)
        Integer restaurantId = user.getRestaurantId();

        // Optional fallback for very old admin seeds with no restaurant_id set
        if (restaurantId == null &&
            ("ADMIN".equalsIgnoreCase(user.getAccountType())
          || "MANAGER".equalsIgnoreCase(user.getAccountType()))) {

            if (email.endsWith("@centralgrill.com")) {
                restaurantId = 1;
            } else if (email.endsWith("@sushipalace.com")) {
                restaurantId = 2;
            } else if (email.endsWith("@pastacorner.com")) {
                restaurantId = 3;
            }

            // Also update the user object so the rest of the app sees it
            if (restaurantId != null) {
                user.setRestaurantId(restaurantId);
            }
        }

        if (restaurantId != null) {
            session.setAttribute("restaurantId", restaurantId);
        }

        // Handle returnAfterLogin
        String returnUrl = (String) session.getAttribute("returnAfterLogin");
        session.removeAttribute("returnAfterLogin");

        if (returnUrl != null && !returnUrl.isBlank()) {
            response.sendRedirect(request.getContextPath() + returnUrl);
            return;
        }

        // Final redirect by role
        String role = user.getAccountType();
        String ctx = request.getContextPath();

        if ("ADMIN".equalsIgnoreCase(role)
                || "MANAGER".equalsIgnoreCase(role)) {

            // Admin / manager → admin dashboard
            response.sendRedirect(ctx + "/admin/dashboard");

        } else if ("HOST".equalsIgnoreCase(role)
                || "EMPLOYEE".equalsIgnoreCase(role)) {

            // Staff → staff dashboard
            response.sendRedirect(ctx + "/staff/dashboard");

        } else {
            // CUSTOMER or any other role → public home
            response.sendRedirect(ctx + "/");
        }
    }
}
