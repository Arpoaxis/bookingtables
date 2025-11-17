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
    	request.getRequestDispatcher("/WEB-INF/jsp/login/login_page.jsp").forward(request, response);

    }
    
    	@Override
    	protected void doPost(HttpServletRequest request, HttpServletResponse response)
    	throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        String dbpath = getServletContext().getRealPath("/WEB-INF/database/restBooking.db");

        
        User user = LoginDao.validate(email, password, dbpath);
        
        if (user == null) {
            System.out.println("[LoginServlet] authentication FAILED for email='" + email + "'");
            
        	request.setAttribute("error", "Invalid email or password.");
			request.getRequestDispatcher("/WEB-INF/jsp/login/login_page.jsp").forward(request, response);
			return;
		}

        System.out.println("[LoginServlet] authentication SUCCESS for email='" + email + "' role='" + user.getAccountType() + "'");
        // Extra safety: make sure password isn't stored in session
        user.setPassword(null);
        // Session fixation protection:
        
        // 1) kill any old session
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        // 2) create a brand-new session
        HttpSession session = request.getSession(true);
        
        session.setAttribute("user", user);
        session.setAttribute("email", user.getEmail());
        session.setAttribute("role", user.getAccountType());
        session.setAttribute("userId", user.getUserId()); 

        String returnUrl = (String)session.getAttribute("returnAfterLogin");
        session.removeAttribute("returnAfterLogin");

        if (returnUrl != null) {
            response.sendRedirect(request.getContextPath() + returnUrl);
            return;
        }

        // Default redirects
        if ("ADMIN".equalsIgnoreCase(user.getAccountType())
                || "MANAGER".equalsIgnoreCase(user.getAccountType())) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
}