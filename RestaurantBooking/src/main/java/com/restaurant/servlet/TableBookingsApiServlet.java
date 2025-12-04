package com.restaurant.servlet;

import com.google.gson.Gson;
import com.restaurant.dao.BookingDao;
import com.restaurant.model.Booking;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/table_bookings")
public class TableBookingsApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String tableIdStr = req.getParameter("tableId");
        String date = req.getParameter("date");

        if (tableIdStr == null || date == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Missing tableId or date parameter\"}");
            return;
        }

        try {
            int tableId = Integer.parseInt(tableIdStr);

            BookingDao bookingDao = new BookingDao(getServletContext());
            List<Booking> bookings = bookingDao.getBookingsForTable(tableId, date);

            Map<String, Object> response = new HashMap<>();
            response.put("bookings", bookings);
            response.put("tableId", tableId);
            response.put("date", date);

            Gson gson = new Gson();
            String json = gson.toJson(response);

            resp.getWriter().write(json);

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid tableId\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
