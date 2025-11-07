package com.restaurant.servlet;

import com.restaurant.dao.RestaurantTableDao;
import com.restaurant.model.RestaurantTable;
import java.io.IOException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/add_table")
public class AddTableServlet extends HttpServlet {
		private static final long serialVersionUID = 1L;
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    	
    	 String tableNumStr = request.getParameter("tableNumber");
         String minStr = request.getParameter("minCapacity");
         String maxStr = request.getParameter("maxCapacity");
         String canCombineStr = request.getParameter("canCombine");
         
         if (tableNumStr == null || minStr == null || maxStr == null ||
			 tableNumStr.isEmpty() || minStr.isEmpty() || maxStr.isEmpty()) {
			 request.setAttribute("error", "All numeric fields are required.");
			 RequestDispatcher rd = request.getRequestDispatcher("/jsp/admin/add_table.jsp");
			 rd.forward(request, response);
			 return;
		 }
         
        try {
	        int tableNumber = Integer.parseInt(request.getParameter("tableNumber"));
	        int minCapacity = Integer.parseInt(request.getParameter("minCapacity"));
	        int maxCapacity = Integer.parseInt(request.getParameter("maxCapacity"));
	        boolean canCombine = (canCombineStr != null);
	        
	        
	        // Build model
	        RestaurantTable table = new RestaurantTable(tableNumber, minCapacity, maxCapacity, canCombine);
	
	        // Get DB path and call DAO
	        String dbPath = getServletContext().getRealPath("/WEB-INF/database/restBooking.db");
	        RestaurantTableDao dao = new RestaurantTableDao(dbPath);
	
	        boolean success = dao.addTable(table);
	        if (success) {
	            request.getSession().setAttribute("tableMessage", "Table added successfully!");
	        } else {
	            request.getSession().setAttribute("tableError", "Failed to add table.Check if the table number already exists.");
	        }
		} catch (NumberFormatException e) {
			request.setAttribute("error", "Invalid input. Please enter valid numbers.");
		}
	        response.sendRedirect(request.getContextPath() + "/jsp/admin/add_table.jsp");
		}
    }