package com.restaurant.listeners;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
@WebListener
public class DatabaseInitializer implements ServletContextListener{
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
        	String basePath = sce.getServletContext().getRealPath("/");
            File dbFolder = new File(basePath, "WEB-INF/database");
            if (!dbFolder.exists()) {
                boolean made = dbFolder.mkdirs();
                System.out.println("Created database folder: " + made);
            }

            File dbFile = new File(dbFolder, "restBooking.db");

            // Optional: rebuild database each run
            if (dbFile.exists()) {
                dbFile.delete();
                System.out.println("Deleted old database: " + dbFile.getAbsolutePath());
            }

            Class.forName("org.sqlite.JDBC");
            String dbPath = dbFile.getAbsolutePath();
            String url = "jdbc:sqlite:" + dbPath;
            System.out.println("SQLite URL: " + url);

            try (Connection conn = DriverManager.getConnection(url);
                 Statement stmt = conn.createStatement()) {

            	//USERS TABLE
            	stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS users (
                            user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            username TEXT NOT NULL,
                            first_name TEXT NOT NULL,
                            last_name TEXT NOT NULL,
                            email TEXT NOT NULL UNIQUE,
                            password TEXT,
                            phone_number TEXT NOT NULL UNIQUE,
                            created TEXT,
                            active INTEGER
                        );
                    """);
            	//USER ROLES TABLE
            	stmt.executeUpdate("""
            			CREATE TABLE IF NOT EXISTS user_roles (
            				role_id INTEGER PRIMARY KEY AUTOINCREMENT,
            				role_name TEXT NOT NULL UNIQUE
            			);
            		""");
            	//USERS TO USER ROLES TABLE
                stmt.executeUpdate("""
                		CREATE TABLE IF NOT EXISTS users_to_user_roles (
                			user_id INTEGER,
                			role_id INTEGER,
                			PRIMARY KEY(user_id, role_id),
                			FOREIGN KEY(user_id) REFERENCES users(user_id)
                			ON DELETE CASCADE ON UPDATE NO ACTION,
                			FOREIGN KEY(role_id) REFERENCES user_roles(role_id)
                			ON DELETE CASCADE ON UPDATE NO ACTION
                		);
                	""");
                //PERMISSIONS TABLE
                stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS permissions (
                            permission_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            permission_name TEXT NOT NULL UNIQUE
                         );
                	""");
                // ROLE PERMISSIONS TABLE
                stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS role_permissions (
                            permission_id INTEGER,
                            role_id INTEGER,
                            PRIMARY KEY(permission_id, role_id),
                            FOREIGN KEY(permission_id) REFERENCES permissions(permission_id)
                            ON DELETE CASCADE ON UPDATE NO ACTION,
                            FOREIGN KEY(role_id) REFERENCES user_roles(role_id)
                            ON DELETE CASCADE ON UPDATE NO ACTION
                        );
                    """);
                //BOOKINGS TABLE
                stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS bookings (
                            booking_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            user_id INTEGER,      
                            number_of_guests INTEGER NOT NULL,
                            booking_date TEXT NOT NULL,
                            booking_time TEXT NOT NULL,
                            special_requests TEXT,
                            booking_status TEXT,
                            FOREIGN KEY(user_id) REFERENCES users(user_id)
                		    ON DELETE CASCADE ON UPDATE NO ACTION
                        );
                    """);
                
                //RESTAURANT TABLES TABLE
                stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS restaurant_tables (
                            table_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            table_number INTEGER NOT NULL UNIQUE,      
                            min_capacity INTEGER NOT NULL,
                            max_capacity INTEGER NOT NULL,
                            can_combine INTEGER NOT NULL
                        );
                    """);
                // BOOKINGS TABLE TABLE
                stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS booking_tables (
                            booking_id INTEGER,
                            table_id INTEGER,
                            PRIMARY KEY(booking_id, table_id),
                            FOREIGN KEY(booking_id) REFERENCES bookings(booking_id)
                            ON DELETE CASCADE ON UPDATE NO ACTION,
                            FOREIGN KEY(table_id) REFERENCES restaurant_tables(table_id)
                            ON DELETE CASCADE ON UPDATE NO ACTION
                        );
                    """);
                //WAITLISTS TABLE
                stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS waitlists (
                            waitlist_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            user_id INTEGER,
                            customer_name TEXT,
                            customer_phone_number TEXT,
                            party_size INTEGER,
                            queue_position INTEGER,
                            status TEXT,
                            estimated_wait_time TEXT,
                            arrived_at TEXT,
                            seated_at TEXT,
                            host_id INTEGER,
                            special_resuests TEXT,
                            FOREIGN KEY(user_id) REFERENCES users(user_id)
                		    ON DELETE CASCADE ON UPDATE NO ACTION,
                		    FOREIGN KEY(host_id) REFERENCES users(user_id)
                		    ON DELETE CASCADE ON UPDATE NO ACTION
                        );
                    """);
             // BOOKINGS WAITLISTS_TABLES TABLE
                stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS waitlists_tables (
                            waitlist_id INTEGER,
                            table_id INTEGER,
                            PRIMARY KEY(waitlist_id, table_id),
                            FOREIGN KEY(waitlist_id) REFERENCES waitlists(waitlist_id)
                            ON DELETE CASCADE ON UPDATE NO ACTION,
                            FOREIGN KEY(table_id) REFERENCES restaurant_tables(table_id)
                            ON DELETE CASCADE ON UPDATE NO ACTION
                        );
                    """);
                
                

            }

            System.out.println("Database recreated successfully at: " + dbPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // nothing to clean up
    }
}
