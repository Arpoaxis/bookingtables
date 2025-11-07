package com.restaurant.servlet;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/selectAccount")
public class AccountTypeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Get the account type selected by the user
        String accountType = request.getParameter("account_type");
        
        // 2. Set the type as a request attribute so the next page can use it
        request.setAttribute("accountType", accountType);
        
        // 3. Forward the request to the registration details page
        request.getRequestDispatcher("/jsp/Login/register.jsp").forward(request, response);
    }
}