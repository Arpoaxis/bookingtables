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

        // Assign restaurant ID for admin/managers
        if ("ADMIN".equalsIgnoreCase(user.getAccountType())
                || "MANAGER".equalsIgnoreCase(user.getAccountType())) {

            Integer restaurantId = null;

            if (email.endsWith("@centralgrill.com")) {
                restaurantId = 1;
            } else if (email.endsWith("@sushipalace.com")) {
                restaurantId = 2;
            } else if (email.endsWith("@pastacorner.com")) {
                restaurantId = 3;
            }

            if (restaurantId != null) {
                session.setAttribute("restaurantId", restaurantId);
            }
        }

        // Handle returnAfterLogin
        String returnUrl = (String) session.getAttribute("returnAfterLogin");
        session.removeAttribute("returnAfterLogin");

        if (returnUrl != null && !returnUrl.isBlank()) {
            response.sendRedirect(request.getContextPath() + returnUrl);
            return;
        }

       
        if ("ADMIN".equalsIgnoreCase(user.getAccountType())
                || "MANAGER".equalsIgnoreCase(user.getAccountType())) {

            response.sendRedirect(request.getContextPath() + "/admin/dashboard");

        } else {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
}
