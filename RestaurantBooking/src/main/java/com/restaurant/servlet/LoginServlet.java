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

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String dbpath = getServletContext().getRealPath("/WEB-INF/database/restBooking.db");
        User user = LoginDao.validate(email, password, dbpath);
        if (user != null) {
			// Prevent session fixation attack: invalidate old session and create new one
			HttpSession oldSession = request.getSession(false);
			if (oldSession != null) {
				oldSession.invalidate();
			}

			// Create new session and store user
			HttpSession session = request.getSession(true);
			session.setAttribute("user", user);

			// Set session timeout (30 minutes)
			session.setMaxInactiveInterval(30 * 60);

			// Redirect based on role
			if ("ADMIN".equalsIgnoreCase(user.getAccountType())) {
				response.sendRedirect(request.getContextPath() + "/jsp/admin/dashboard.jsp");
			} else {
				response.sendRedirect(request.getContextPath() + "/jsp/index.jsp");
			}
			return;
		}
        else {
        	request.setAttribute("error", "Invalid email or password.");
			request.getRequestDispatcher("/WEB-INF/jsp/Login/login_page.jsp").forward(request, response);
        }
    }
}
        