//register data access object class
package com.restaurant.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterDao{
	public static String register(String email, String password,String account_type,
			long phone_number,String first_name,String last_name, String dbpath) {
		try {
			
			Class.forName("org.sqlite.JDBC");
			//get the path of the database
			String url = "jdbc:sqlite:" + dbpath;
			//create connection to the database
			try(Connection connection = DriverManager.getConnection(url)){
				
				// Check if username exists
				try(PreparedStatement ps = connection.prepareStatement(
					"select 1 from users where email=?")){
					ps.setString(1, email);
					try(ResultSet rs = ps.executeQuery()){
						if(rs.next()) {
							return "EMAIL_EXISTS";//email already exists
						}
					}
				}
				
				
				// Insert new user
				try(PreparedStatement ps = connection.prepareStatement(
						"INSERT INTO users (email, password, "
						+ "account_type, phone_number, first_name, last_name) "
						+ "VALUES (?, ?, ?, ?, ?, ?)")){//insert values into users table
					ps.setString(1, email);//set email
					ps.setString(2, password);//set password
					ps.setLong(3, phone_number);//set phone number
					ps.setString(4, first_name);//set first name
					ps.setString(5, last_name);//set last name
					
					int rowsAffected = ps.executeUpdate();
					if(rowsAffected > 0) {//changes made
						return "SUCCESS";
					}
				}
			}
		} catch(SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return "FAIL";
	}
}