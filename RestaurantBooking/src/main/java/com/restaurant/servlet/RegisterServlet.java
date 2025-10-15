package com.restaurant.servlet;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.restaurant.dao.RegisterDao;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/Login/register.jsp").forward(request, response);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException{
    	
    	
    	//get username, password and confirm password from the form
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String dbpath = getServletContext().getRealPath("/WEB-INF/database/restBooking.db");
       
        // Check for password mismatch first
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match.");
            request.getRequestDispatcher("/jsp/Login/register.jsp").forward(request, response);
            return;
        }

        // Call RegisterDao.register and handle status
        String status = RegisterDao.register(username, password, dbpath);
        if ("USERNAME_EXISTS".equals(status)) {
            request.setAttribute("error", "Username already exists.");
            request.getRequestDispatcher("/jsp/Login/register.jsp").forward(request, response);
        } else if ("SUCCESS".equals(status)) {
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            response.sendRedirect(request.getContextPath() + "/jsp/Login/login_page.jsp");
        } else {
            request.setAttribute("error", "Registration failed. Please try again.");
            request.getRequestDispatcher("/jsp/Login/register.jsp").forward(request, response);
        }
    }
}