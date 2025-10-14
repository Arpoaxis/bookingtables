//register data access object class
package com.restaurant.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterDao{
	public static String register(String username, String password, String dbpath) {
		try {
			Class.forName("org.sqlite.JDBC");
			//get the path of the database
			String url = "jdbc:sqlite:" + dbpath;
			//create connection to the database
			try(Connection connection = DriverManager.getConnection(url)){
				// Check if username exists
				try(PreparedStatement ps = connection.prepareStatement(
					"select 1 from users where username=?")){
					ps.setString(1, username);
					try(ResultSet rs = ps.executeQuery()){
						if(rs.next()) {
							return "USERNAME_EXISTS";
						}
					}
				}
				// Insert new user
				try(PreparedStatement ps = connection.prepareStatement(
					"insert into users(username,password) values(?,?)")){//insert values into users table
					ps.setString(1, username);//set username
					ps.setString(2, password);//set password
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