package com.restaurant.model;

public class Booking {

	private int bookingId;
	private int restaurantId;
	private String restaurantName;
	private String customerEmail;
	private String customerFirstName;
	private String customerLastName;
	private int guests;
	private String date;
	private String time;
	private String requests;
	private String status;
	private String assignedTables;

	public int getBookingId() {
		return bookingId;
	}

	public void setBookingId(int bookingId) {
		this.bookingId = bookingId;
	}

	public int getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(int restaurantId) {
		this.restaurantId = restaurantId;
	}

	public String getRestaurantName() {
		return restaurantName;
	}

	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getCustomerFirstName() {
		return customerFirstName;
	}

	public void setCustomerFirstName(String customerFirstName) {
		this.customerFirstName = customerFirstName;
	}

	public String getCustomerLastName() {
		return customerLastName;
	}

	public void setCustomerLastName(String customerLastName) {
		this.customerLastName = customerLastName;
	}

	public int getGuests() {
		return guests;
	}

	public void setGuests(int guests) {
		this.guests = guests;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getRequests() {
		return requests;
	}

	public void setRequests(String requests) {
		this.requests = requests;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAssignedTables() {
		return assignedTables;
	}

	public void setAssignedTables(String assignedTables) {
		this.assignedTables = assignedTables;
	}

	public String getCustomerFullName() {
		String f = customerFirstName == null ? "" : customerFirstName;
		String l = customerLastName == null ? "" : customerLastName;
		return (f + " " + l).trim();
	}

	public String getDisplayTime() {
		if (time == null || time.isBlank()) {
			return "";
		}
		try {
			// time is stored as HH:mm or HH:mm:ss
			String raw = time;
			if (raw.length() > 5) {
				raw = raw.substring(0, 5); // HH:mm
			}
			java.time.LocalTime lt = java.time.LocalTime.parse(raw);
			java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("h:mm a");
			return lt.format(fmt);
		} catch (Exception e) {
			// Fallback to whatever is stored
			return time;
		}
	}

}
