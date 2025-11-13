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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
      req.getRequestDispatcher("/WEB-INF/jsp/Login/login_page.jsp").forward(req, resp);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String dbpath = getServletContext().getRealPath("/WEB-INF/database/restBooking.db");
        User user = LoginDao.validate(email, password, dbpath);
        if (user != null) {
			// Create session and store user
        	HttpSession session = request.getSession();
        	session.setAttribute("user", user);
        	session.setAttribute("email", user.getEmail());       
        	session.setAttribute("role", user.getAccountType());

        	if ("ADMIN".equalsIgnoreCase(user.getAccountType()) || 
        	    "MANAGER".equalsIgnoreCase(user.getAccountType())) {
        	    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        	} else {
        	    response.sendRedirect(request.getContextPath() + "/");
        	}
			return;
		}
        else {
        	request.setAttribute("error", "Invalid email or password.");
			request.getRequestDispatcher("/WEB-INF/jsp/Login/login_page.jsp").forward(request, response);
        }
    }
}
        