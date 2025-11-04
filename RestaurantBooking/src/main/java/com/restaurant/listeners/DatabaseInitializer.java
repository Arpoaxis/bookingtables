package com.restaurant.listeners;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@WebListener
public class DatabaseInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        try {
            String basePath = ctx.getRealPath("/");
            File dbFolder = new File(basePath, "WEB-INF/database");
            if (!dbFolder.exists()) {
                boolean made = dbFolder.mkdirs();
                System.out.println("Created database folder: " + made);
            }

            File dbFile = new File(dbFolder, "restBooking.db");

            // Toggle via web.xml
            boolean reset = Boolean.parseBoolean(
                String.valueOf(ctx.getInitParameter("resetDatabaseOnStartup"))
            );

            if (reset && dbFile.exists()) {
                System.out.println("Reset flag is TRUE. Deleting DB: " + dbFile.getAbsolutePath());
                if (!dbFile.delete()) {
                    System.out.println("Warning: could not delete existing database file.");
                }
            }

            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            System.out.println("SQLite URL: " + url);

            try (Connection conn = DriverManager.getConnection(url);
                 Statement stmt = conn.createStatement()) {

                
                stmt.execute("PRAGMA foreign_keys = ON;");
                stmt.execute("PRAGMA journal_mode = WAL;");

                conn.setAutoCommit(false);
                try {
                    
                    stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS users (
                            user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            username TEXT NOT NULL,
                            first_name TEXT NOT NULL,
                            last_name  TEXT NOT NULL,
                            email TEXT NOT NULL UNIQUE,
                            password TEXT,
                            phone_number TEXT NOT NULL UNIQUE,
                            created TEXT NOT NULL DEFAULT (datetime('now')),
                            active  INTEGER NOT NULL DEFAULT 1 CHECK(active IN (0,1))
                        );
                    """);

                    stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS user_roles (
                            role_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            role_name TEXT NOT NULL UNIQUE
                        );
                    """);

                    stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS users_to_user_roles (
                            user_id INTEGER NOT NULL,
                            role_id INTEGER NOT NULL,
                            PRIMARY KEY(user_id, role_id),
                            FOREIGN KEY(user_id) REFERENCES users(user_id)
                                ON DELETE CASCADE ON UPDATE NO ACTION,
                            FOREIGN KEY(role_id) REFERENCES user_roles(role_id)
                                ON DELETE CASCADE ON UPDATE NO ACTION
                        );
                    """);

                    stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS permissions (
                            permission_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            permission_name TEXT NOT NULL UNIQUE
                        );
                    """);

                    stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS role_permissions (
                            permission_id INTEGER NOT NULL,
                            role_id INTEGER NOT NULL,
                            PRIMARY KEY(permission_id, role_id),
                            FOREIGN KEY(permission_id) REFERENCES permissions(permission_id)
                                ON DELETE CASCADE ON UPDATE NO ACTION,
                            FOREIGN KEY(role_id) REFERENCES user_roles(role_id)
                                ON DELETE CASCADE ON UPDATE NO ACTION
                        );
                    """);

                    stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS bookings (
                            booking_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            user_id INTEGER,
                            number_of_guests INTEGER NOT NULL CHECK(number_of_guests >= 1),
                            booking_date TEXT NOT NULL,
                            booking_time TEXT NOT NULL,
                            special_requests TEXT,
                            booking_status TEXT NOT NULL DEFAULT 'PENDING'
                                CHECK(booking_status IN ('PENDING','CONFIRMED','SEATED','CANCELLED')),
                            created TEXT NOT NULL DEFAULT (datetime('now')),
                            FOREIGN KEY(user_id) REFERENCES users(user_id)
                                ON DELETE CASCADE ON UPDATE NO ACTION
                        );
                    """);

                    stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS restaurant_tables (
                            table_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            table_number  INTEGER NOT NULL UNIQUE,
                            min_capacity  INTEGER NOT NULL CHECK(min_capacity >= 1),
                            max_capacity  INTEGER NOT NULL CHECK(max_capacity >= min_capacity),
                            can_combine   INTEGER NOT NULL CHECK(can_combine IN (0,1))
                        );
                    """);

                    stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS booking_tables (
                            booking_id INTEGER NOT NULL,
                            table_id   INTEGER NOT NULL,
                            PRIMARY KEY(booking_id, table_id),
                            FOREIGN KEY(booking_id) REFERENCES bookings(booking_id)
                                ON DELETE CASCADE ON UPDATE NO ACTION,
                            FOREIGN KEY(table_id)   REFERENCES restaurant_tables(table_id)
                                ON DELETE CASCADE ON UPDATE NO ACTION
                        );
                    """);

                    stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS waitlists (
                            waitlist_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            user_id INTEGER,
                            customer_name TEXT,
                            customer_phone_number TEXT,
                            party_size INTEGER NOT NULL CHECK(party_size >= 1),
                            queue_position INTEGER,
                            status TEXT NOT NULL DEFAULT 'WAITING'
                                CHECK(status IN ('WAITING','NOTIFIED','SEATED','CANCELLED')),
                            estimated_wait_time TEXT,
                            arrived_at TEXT,
                            seated_at TEXT,
                            host_id INTEGER,
                            special_requests TEXT,
                            created TEXT NOT NULL DEFAULT (datetime('now')),
                            FOREIGN KEY(user_id) REFERENCES users(user_id)
                                ON DELETE CASCADE ON UPDATE NO ACTION,
                            FOREIGN KEY(host_id) REFERENCES users(user_id)
                                ON DELETE CASCADE ON UPDATE NO ACTION
                        );
                    """);

                    stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS waitlist_tables (
                            waitlist_id INTEGER NOT NULL,
                            table_id    INTEGER NOT NULL,
                            PRIMARY KEY(waitlist_id, table_id),
                            FOREIGN KEY(waitlist_id) REFERENCES waitlists(waitlist_id)
                                ON DELETE CASCADE ON UPDATE NO ACTION,
                            FOREIGN KEY(table_id)    REFERENCES restaurant_tables(table_id)
                                ON DELETE CASCADE ON UPDATE NO ACTION
                        );
                    """);

                    
                    stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_users_email  ON users(email);");
                    stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_users_phone  ON users(phone_number);");
                    stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_bookings_user ON bookings(user_id);");
                    stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_booking_tables_booking ON booking_tables(booking_id);");
                    stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_booking_tables_table   ON booking_tables(table_id);");
                    stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_waitlists_user ON waitlists(user_id);");
                    stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_waitlists_host ON waitlists(host_id);");

                    
                    stmt.executeUpdate("""
                        INSERT OR IGNORE INTO users(user_id, username, first_name, last_name, email, password, phone_number, active)
                        VALUES (1, 'admin', 'Admin', 'User', 'admin@example.com', 'admin', '0000000000', 1);
                    """);
                    
                    stmt.executeUpdate("""
                        INSERT OR IGNORE INTO users_to_user_roles(user_id, role_id)
                        SELECT 1, role_id FROM user_roles WHERE role_name='ADMIN';
                    """);

                    stmt.executeUpdate("""
                    		  INSERT OR IGNORE INTO user_roles(role_name)
                    		  VALUES('ADMIN'),('MANAGER'),('EMPLOYEE'),('HOST'),('CUSTOMER');
                    		""");
                    
                    conn.commit();
                } catch (Exception ex) {
                    conn.rollback();
                    throw ex;
                } finally {
                    conn.setAutoCommit(true);
                }
            }
            try {
                loadUsersFromCsv(sce.getServletContext(), "/WEB-INF/data/users.csv", url);
                loadUserRolesFromCsv(sce.getServletContext(), "/WEB-INF/data/user_roles.csv", url);
                System.out.println("✅ CSV seed data loaded.");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.out.println("Database ready at: " + dbFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    private void loadUsersFromCsv(jakarta.servlet.ServletContext ctx, String resourcePath, String url) throws Exception {
        try (java.io.InputStream in = ctx.getResourceAsStream(resourcePath)) {
            if (in == null) {
                System.out.println("No CSV found at " + resourcePath + " (skipping user seed).");
                return;
            }
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(in));
                 java.sql.Connection conn = java.sql.DriverManager.getConnection(url)) {

                // honor FKs during insert
                try (java.sql.Statement s = conn.createStatement()) {
                    s.execute("PRAGMA foreign_keys = ON;");
                }

                conn.setAutoCommit(false);
                String line;
                boolean headerSkipped = false;

                String sql = """
                    INSERT OR IGNORE INTO users
                    (username, first_name, last_name, email, password, phone_number, created, active)
                    VALUES (?,?,?,?,?,?,?,?)
                """;
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                    while ((line = br.readLine()) != null) {
                        // skip header
                        if (!headerSkipped) { headerSkipped = true; continue; }
                        // split CSV line; your sample has no quoted commas
                        String[] p = line.split(",", -1);
                        if (p.length < 8) continue;

                        String username   = p[0].trim();
                        String firstName  = p[1].trim();
                        String lastName   = p[2].trim();
                        String email      = p[3].trim().toLowerCase();
                        String password   = p[4].trim();
                        String phone      = p[5].trim();
                        String created    = p[6].trim();              // e.g. "2025-03-20 17:18:24"
                        int active        = Integer.parseInt(p[7].trim()); // 0 or 1

                        ps.setString(1, username);
                        ps.setString(2, firstName);
                        ps.setString(3, lastName);
                        ps.setString(4, email);
                        ps.setString(5, password);  // TODO: hash later
                        ps.setString(6, phone);
                        ps.setString(7, created);
                        ps.setInt(8, active);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                } catch (Exception ex) {
                    conn.rollback();
                    throw ex;
                } finally {
                    conn.commit();
                    conn.setAutoCommit(true);
                }
            }
        }
    }
    private void loadUserRolesFromCsv(jakarta.servlet.ServletContext ctx, String resourcePath, String url) throws Exception {
        try (java.io.InputStream in = ctx.getResourceAsStream(resourcePath)) {
            if (in == null) {
                System.out.println("No CSV found at " + resourcePath + " (skipping user_roles seed).");
                return;
            }
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(in));
                 java.sql.Connection conn = java.sql.DriverManager.getConnection(url)) {

                try (java.sql.Statement s = conn.createStatement()) {
                    s.execute("PRAGMA foreign_keys = ON;");
                }

                conn.setAutoCommit(false);
                String line;
                boolean headerSkipped = false;

                while ((line = br.readLine()) != null) {
                    if (!headerSkipped) { headerSkipped = true; continue; }
                    String[] p = line.split(",", -1);
                    if (p.length < 2) continue;

                    String email = p[0].trim().toLowerCase();
                    String roleName = p[1].trim().toUpperCase();

                    // look up IDs
                    Integer userId = null, roleId = null;
                    try (var ps = conn.prepareStatement("SELECT user_id FROM users WHERE email=?")) {
                        ps.setString(1, email);
                        try (var rs = ps.executeQuery()) { if (rs.next()) userId = rs.getInt(1); }
                    }
                    try (var ps = conn.prepareStatement("SELECT role_id FROM user_roles WHERE role_name=?")) {
                        ps.setString(1, roleName);
                        try (var rs = ps.executeQuery()) { if (rs.next()) roleId = rs.getInt(1); }
                    }

                    if (userId != null && roleId != null) {
                        try (var ps = conn.prepareStatement("""
                            INSERT OR IGNORE INTO users_to_user_roles(user_id, role_id) VALUES(?,?)
                        """)) {
                            ps.setInt(1, userId);
                            ps.setInt(2, roleId);
                            ps.executeUpdate();
                        }
                    }
                }
                conn.commit();
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) { /* no-op */ }
}
