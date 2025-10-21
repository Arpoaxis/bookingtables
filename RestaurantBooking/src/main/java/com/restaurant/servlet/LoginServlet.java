package com.restaurant.servlet;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.restaurant.dao.LoginDao;

@WebServlet("/login")
	public class LoginServlet extends HttpServlet {
		private static final long serialVersionUID = 1L;
			protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
				 
				// Retrieve email and password from the form
				String email = request.getParameter("email");
				String password =request.getParameter("password");
				//get the path of the database
				String dbpath = getServletContext().getRealPath("/WEB-INF/database/restBooking.db");
				////Authentication with database
				if(LoginDao.validate(email, password,dbpath)){
					//if authentication is successful create a session 
					HttpSession session = request.getSession();
					session.setAttribute("email", email);
					//redirect to index page
					response.sendRedirect(request.getContextPath() + "/jsp/index.jsp");
				}else{
					//if authentication fails redirect to login page
					response.sendRedirect(request.getContextPath() + "/jsp/Login/login_page.jsp?error=invalid");
				}
		}
}