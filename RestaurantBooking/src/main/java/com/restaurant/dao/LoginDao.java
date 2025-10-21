//login dao class
package com.restaurant.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDao {
	public static boolean validate(String email, String password, String dbPath) {
		boolean status =false;
		try {
			Class.forName("org.sqlite.JDBC");
			//get the path of the database
			String url = "jdbc:sqlite:" + dbPath;
			//create a connection to the database
			try (Connection connection = DriverManager.getConnection(url);
				//create a prepared statement
				PreparedStatement ps = connection.prepareStatement("select * from users where email=? and password=?")) {
				ps.setString(1, email);
				ps.setString(2, password);
				try (ResultSet resultSet = ps.executeQuery()){
					//user is valid
					status = resultSet.next();
					}
			
			}
		} catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
		return status;
	}
}