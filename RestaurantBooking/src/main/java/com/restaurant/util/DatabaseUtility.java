package com.restaurant.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.file.Paths;
import jakarta.servlet.ServletContext;

import java.sql.ResultSet;

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

        System.out.println("SQLite URL: " + url);

        Connection conn = DriverManager.getConnection(url);


        try (Statement s = conn.createStatement()) {

            s.execute("PRAGMA journal_mode=WAL");    
            s.execute("PRAGMA busy_timeout=5000");  
            
            //Debugging: print journal_mode
            try (ResultSet rs = s.executeQuery("PRAGMA journal_mode")) {
                if (rs.next()) {
                    System.out.println("=== SQLite journal_mode = " + rs.getString(1));
                }
            }
        }

        return conn;
    }

}
