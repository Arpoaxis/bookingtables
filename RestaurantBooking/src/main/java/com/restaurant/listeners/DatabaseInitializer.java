package com.restaurant.listeners;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import com.restaurant.util.PasswordUtil;

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


                    // Hash the default admin password
                    String adminPassword = PasswordUtil.hashPassword("Admin@123");

                    stmt.executeUpdate("""
                        INSERT OR IGNORE INTO user_roles(role_name)
                        VALUES('ADMIN'),('MANAGER'),('EMPLOYEE'),('HOST'),('CUSTOMER');
                    """);

                    // Use PreparedStatement for admin user to safely insert hashed password
                    try (java.sql.PreparedStatement ps = conn.prepareStatement(
                        "INSERT OR IGNORE INTO users(user_id, username, first_name, last_name, email, password, phone_number, active) " +
                        "VALUES (1, 'admin', 'Admin', 'User', 'admin@example.com', ?, '0000000000', 1)")) {
                        ps.setString(1, adminPassword);
                        ps.executeUpdate();
                    }

                    stmt.executeUpdate("""
                        INSERT OR IGNORE INTO users_to_user_roles(user_id, role_id)
                        SELECT 1, role_id FROM user_roles WHERE role_name='ADMIN';
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
                loadRestaurantTables(ctx, "/WEB-INF/data/restaurant_tables.csv", url);
                loadBookings(ctx, "/WEB-INF/data/bookings.csv", url);
                loadWaitlists(ctx, "/WEB-INF/data/waitlists.csv", url);
                loadWaitlistTableLinks(ctx, "/WEB-INF/data/waitlist_tables_link.csv", url);

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

                        // Hash the password before storing
                        String hashedPassword = PasswordUtil.hashPassword(password);

                        ps.setString(1, username);
                        ps.setString(2, firstName);
                        ps.setString(3, lastName);
                        ps.setString(4, email);
                        ps.setString(5, hashedPassword);
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
   
    private void loadRestaurantTables(ServletContext ctx, String resourcePath, String url) throws Exception {
        try (var in = ctx.getResourceAsStream(resourcePath)) {
            if (in == null) { System.out.println("No CSV: " + resourcePath); return; }
            try (var br = new java.io.BufferedReader(new java.io.InputStreamReader(in));
                 var conn = java.sql.DriverManager.getConnection(url)) {
                conn.setAutoCommit(false);
                String line; boolean header = false;
                String sql = """
                    INSERT OR IGNORE INTO restaurant_tables(table_number, min_capacity, max_capacity, can_combine)
                    VALUES (?,?,?,?)
                """;
                try (var ps = conn.prepareStatement(sql)) {
                    while ((line = br.readLine()) != null) {
                        if (!header) { header = true; continue; }
                        String[] p = line.split(",", -1);
                        if (p.length < 4) continue;
                        ps.setInt(1, Integer.parseInt(p[0].trim())); // table_number
                        ps.setInt(2, Integer.parseInt(p[1].trim())); // min_capacity
                        ps.setInt(3, Integer.parseInt(p[2].trim())); // max_capacity
                        ps.setInt(4, Integer.parseInt(p[3].trim())); // can_combine 0/1
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                conn.commit();
            }
        }
    }
    private void loadBookings(ServletContext ctx, String resourcePath, String url) throws Exception {
        try (var in = ctx.getResourceAsStream(resourcePath)) {
            if (in == null) { System.out.println("No CSV: " + resourcePath); return; }
            try (var br = new java.io.BufferedReader(new java.io.InputStreamReader(in));
                 var conn = java.sql.DriverManager.getConnection(url)) {

                try (var s = conn.createStatement()) { s.execute("PRAGMA foreign_keys = ON;"); }
                conn.setAutoCommit(false);

                String findUser = "SELECT user_id FROM users WHERE email=?";
                String findTable = "SELECT table_id FROM restaurant_tables WHERE table_number=?";
                String insertBooking = """
                    INSERT INTO bookings(user_id, number_of_guests, booking_date, booking_time, special_requests, booking_status, created)
                    VALUES (?,?,?,?,?,?,?)
                """;
                String insertLink = "INSERT OR IGNORE INTO booking_tables(booking_id, table_id) VALUES(?,?)";
                String lastId = "SELECT last_insert_rowid()";

                try (var psUser = conn.prepareStatement(findUser);
                     var psTable = conn.prepareStatement(findTable);
                     var psInsB = conn.prepareStatement(insertBooking);
                     var psLink = conn.prepareStatement(insertLink);
                     var psLast = conn.prepareStatement(lastId)) {

                    String line; boolean header = false;
                    while ((line = br.readLine()) != null) {
                        if (!header) { header = true; continue; }
                        String[] p = line.split(",", -1);
                        if (p.length < 8) continue;

                        String email = p[0].trim().toLowerCase();
                        int guests = Integer.parseInt(p[1].trim());
                        String bDate = p[2].trim();
                        String bTime = p[3].trim();
                        String req   = p[4].trim();
                        String status= p[5].trim();
                        String created = p[6].trim();
                        int tableNumber = Integer.parseInt(p[7].trim());

                        // user_id
                        Integer userId = null;
                        psUser.setString(1, email);
                        try (var rs = psUser.executeQuery()) { if (rs.next()) userId = rs.getInt(1); }
                        if (userId == null) continue; // skip unknown user (shouldn't happen)

                        // table_id
                        Integer tableId = null;
                        psTable.setInt(1, tableNumber);
                        try (var rs = psTable.executeQuery()) { if (rs.next()) tableId = rs.getInt(1); }

                        // booking
                        psInsB.setInt(1, userId);
                        psInsB.setInt(2, guests);
                        psInsB.setString(3, bDate);
                        psInsB.setString(4, bTime);
                        psInsB.setString(5, req);
                        psInsB.setString(6, status);
                        psInsB.setString(7, created);
                        psInsB.executeUpdate();

                        // fetch booking_id and link
                        int bookingId;
                        try (var rs = psLast.executeQuery()) { rs.next(); bookingId = rs.getInt(1); }
                        if (tableId != null) {
                            psLink.setInt(1, bookingId);
                            psLink.setInt(2, tableId);
                            psLink.executeUpdate();
                        }
                    }
                }
                conn.commit();
            }
        }
    }

    private void loadWaitlists(ServletContext ctx, String resourcePath, String url) throws Exception {
        try (var in = ctx.getResourceAsStream(resourcePath)) {
            if (in == null) { System.out.println("No CSV: " + resourcePath); return; }
            try (var br = new java.io.BufferedReader(new java.io.InputStreamReader(in));
                 var conn = java.sql.DriverManager.getConnection(url)) {

                try (var s = conn.createStatement()) { s.execute("PRAGMA foreign_keys = ON;"); }
                conn.setAutoCommit(false);

                String findUser = "SELECT user_id FROM users WHERE email=?";
                String insertWL = """
                    INSERT INTO waitlists(user_id, customer_name, customer_phone_number, party_size, queue_position, status,
                                          estimated_wait_time, arrived_at, seated_at, host_id, special_requests, created)
                    VALUES (?,?,?,?,?,?,?,?,?,?,?,?)
                """;

                try (var psFind = conn.prepareStatement(findUser);
                     var psIns = conn.prepareStatement(insertWL)) {

                    String line; boolean header=false;
                    while ((line = br.readLine()) != null) {
                        if (!header) { header = true; continue; }
                        String[] p = line.split(",", -1);
                        if (p.length < 12) continue;

                        String custEmail = p[0].trim().toLowerCase();
                        String custName  = p[1].trim();
                        String custPhone = p[2].trim();
                        int party        = Integer.parseInt(p[3].trim());
                        String posStr    = p[4].trim();
                        Integer queuePos = posStr.isEmpty() ? null : Integer.valueOf(posStr);
                        String status    = p[5].trim();
                        String estWait   = p[6].trim();
                        String arrivedAt = p[7].trim();
                        String seatedAt  = p[8].trim();
                        String hostEmail = p[9].trim().toLowerCase();
                        String requests  = p[10].trim();
                        String created   = p[11].trim();

                        Integer userId = null, hostId = null;
                        if (!custEmail.isEmpty()) {
                            psFind.setString(1, custEmail);
                            try (var rs = psFind.executeQuery()) { if (rs.next()) userId = rs.getInt(1); }
                        }
                        if (!hostEmail.isEmpty()) {
                            psFind.setString(1, hostEmail);
                            try (var rs = psFind.executeQuery()) { if (rs.next()) hostId = rs.getInt(1); }
                        }

                        psIns.setObject(1, userId);
                        psIns.setString(2, custName);
                        psIns.setString(3, custPhone);
                        psIns.setInt(4, party);
                        if (queuePos == null) psIns.setNull(5, java.sql.Types.INTEGER); else psIns.setInt(5, queuePos);
                        psIns.setString(6, status);
                        psIns.setString(7, estWait);
                        psIns.setString(8, arrivedAt);
                        if (seatedAt.isEmpty()) psIns.setNull(9, java.sql.Types.VARCHAR); else psIns.setString(9, seatedAt);
                        if (hostId == null) psIns.setNull(10, java.sql.Types.INTEGER); else psIns.setInt(10, hostId);
                        psIns.setString(11, requests);
                        psIns.setString(12, created);
                        psIns.executeUpdate();
                    }
                }
                conn.commit();
            }
        }
    }

    private void loadWaitlistTableLinks(ServletContext ctx, String resourcePath, String url) throws Exception {
        try (var in = ctx.getResourceAsStream(resourcePath)) {
            if (in == null) { System.out.println("No CSV: " + resourcePath); return; }
            try (var br = new java.io.BufferedReader(new java.io.InputStreamReader(in));
                 var conn = java.sql.DriverManager.getConnection(url)) {

                try (var s = conn.createStatement()) { s.execute("PRAGMA foreign_keys = ON;"); }
                conn.setAutoCommit(false);

                String findHost = "SELECT user_id FROM users WHERE email=?";
                String findWait = """
                    SELECT waitlist_id FROM waitlists
                    WHERE arrived_at=? AND ( (? IS NULL AND host_id IS NULL) OR host_id=? )
                    LIMIT 1
                """;
                String findTable = "SELECT table_id FROM restaurant_tables WHERE table_number=?";
                String insertWT = "INSERT OR IGNORE INTO waitlist_tables(waitlist_id, table_id) VALUES(?,?)";

                try (var psHost = conn.prepareStatement(findHost);
                     var psWait = conn.prepareStatement(findWait);
                     var psTable = conn.prepareStatement(findTable);
                     var psIns = conn.prepareStatement(insertWT)) {

                    String line; boolean header=false;
                    while ((line = br.readLine()) != null) {
                        if (!header) { header = true; continue; }
                        String[] p = line.split(",", -1);
                        if (p.length < 3) continue;

                        String arrivedAt = p[0].trim();
                        String hostEmail = p[1].trim().toLowerCase();
                        int tableNumber  = Integer.parseInt(p[2].trim());

                        Integer hostId = null;
                        if (!hostEmail.isEmpty()) {
                            psHost.setString(1, hostEmail);
                            try (var rs = psHost.executeQuery()) { if (rs.next()) hostId = rs.getInt(1); }
                        }

                        // find waitlist_id
                        psWait.setString(1, arrivedAt);
                        if (hostId == null) {
                            psWait.setNull(2, java.sql.Types.INTEGER);
                            psWait.setNull(3, java.sql.Types.INTEGER);
                        } else {
                            psWait.setInt(2, hostId);
                            psWait.setInt(3, hostId);
                        }
                        Integer waitlistId = null;
                        try (var rs = psWait.executeQuery()) { if (rs.next()) waitlistId = rs.getInt(1); }
                        if (waitlistId == null) continue;

                        // table_id
                        Integer tableId = null;
                        psTable.setInt(1, tableNumber);
                        try (var rs = psTable.executeQuery()) { if (rs.next()) tableId = rs.getInt(1); }
                        if (tableId == null) continue;

                        psIns.setInt(1, waitlistId);
                        psIns.setInt(2, tableId);
                        psIns.executeUpdate();
                    }
                }
                conn.commit();
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) { /* no-op */ }
}
