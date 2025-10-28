package com.restaurant.servlet;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.restaurant.dao.RegisterDao;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
        String account_type = request.getParameter("account_type");
        String phone = request.getParameter("PhoneNumber");
        String first_name = request.getParameter("FirstName");
        String last_name = request.getParameter("LastName");
        
        String dbpath = getServletContext().getRealPath("/WEB-INF/database/restBooking.db");
        //check account type
        if (account_type == null || account_type.isEmpty()) {
			request.setAttribute("error", "Please select an account type.");
			request.getRequestDispatcher("/jsp/Login/register.jsp").forward(request, response);
			return;
		}
        // Check for password mismatch first
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match.");
            request.getRequestDispatcher("/jsp/Login/register.jsp").forward(request, response);
            return;
        }
        
        // Validate email format
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" 
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher= pattern.matcher(email);
        if (!matcher.matches()) {
			request.setAttribute("error", "Invalid email format.");
			request.getRequestDispatcher("/jsp/Login/register.jsp").forward(request, response);
			return;
		}
        
        long phone_number;
		try {
			phone_number = Long.parseLong(phone);
		} catch (NumberFormatException e) {
			sendError(request, response, "Invalid phone number format.");
			return;
		}
        
        // Call RegisterDao.register and handle status
        String status = RegisterDao.register(email, password,account_type,
        		phone_number,first_name,last_name,dbpath);
        
        if ("EMAIL_EXIST".equals(status)) {
            request.setAttribute("error", "Email already exists.");
            request.getRequestDispatcher("/jsp/Login/register.jsp").forward(request, response);
        } else if ("SUCCESS".equals(status)) {
            HttpSession session = request.getSession();
            session.setAttribute("email", email);
            response.sendRedirect(request.getContextPath() + "/jsp/Login/login_page.jsp");
        } else {
            request.setAttribute("error", "Registration failed. Please try again.");
            request.getRequestDispatcher("/jsp/Login/register.jsp").forward(request, response);
        }
    }
	private void sendError(HttpServletRequest request, HttpServletResponse response, String string) {
		request.setAttribute("error", string);
		try {
			request.getRequestDispatcher("/jsp/Login/register.jsp").forward(request, response);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
		
	}
}