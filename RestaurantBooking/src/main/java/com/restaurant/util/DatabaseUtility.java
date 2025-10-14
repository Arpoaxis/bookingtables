package com.restaurant.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.nio.file.Paths;
import jakarta.servlet.ServletContext;

public class DatabaseUtility {
	
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection(ServletContext context) throws SQLException {
    	String basePath = context.getRealPath("/");
        String dbPath = Paths.get(basePath, "WEB-INF", "database", "restBooking.db").toString();
        String url = "jdbc:sqlite:" + dbPath;
      	
        return DriverManager.getConnection(url);
    }
}
