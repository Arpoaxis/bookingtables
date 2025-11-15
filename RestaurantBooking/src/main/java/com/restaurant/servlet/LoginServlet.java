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

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	request.getRequestDispatcher("/WEB-INF/jsp/login/login_page.jsp").forward(request, response);
    }
    	@Override
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
        	request.getRequestDispatcher("/WEB-INF/jsp/login/login_page.jsp").forward(request, response);
        }
    }
}
        