package com.restaurant.model;

public class Booking {

    private int bookingId;
    private int restaurantId;
    private String restaurantName;
    private String customerEmail;
    private int guests;
    private String date;
    private String time;
    private String requests;
    private String status;

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public int getRestaurantId() { return restaurantId; }
    public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    public int getGuests() { return guests; }
    public void setGuests(int guests) { this.guests = guests; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getRequests() { return requests; }
    public void setRequests(String requests) { this.requests = requests; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
