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

            boolean reset = Boolean.parseBoolean(
                    String.valueOf(ctx.getInitParameter("resetDatabaseOnStartup"))
            );

            if (reset && dbFile.exists()) {
                System.out.println("Reset flag TRUE → deleting DB...");
                dbFile.delete();
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
                    // ========== TABLE CREATION ==========

                    stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS restaurants (
                            restaurant_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            name TEXT NOT NULL,
                            address TEXT,
                            phone TEXT,
                            description TEXT,
                            opening_time TEXT DEFAULT '10:00',
                            closing_time TEXT DEFAULT '22:00',
                            created TEXT NOT NULL DEFAULT(datetime('now'))
                        );
                    """);

                    stmt.executeUpdate("""
                    	    CREATE TABLE IF NOT EXISTS users (
                    	        user_id      INTEGER PRIMARY KEY AUTOINCREMENT,
                    	        username     TEXT NOT NULL,
                    	        first_name   TEXT NOT NULL,
                    	        last_name    TEXT NOT NULL,
                    	        email        TEXT NOT NULL UNIQUE,
                    	        password     TEXT,
                    	        phone_number TEXT NOT NULL UNIQUE,
                    	        restaurant_id INTEGER,
                    	        created      TEXT NOT NULL DEFAULT(datetime('now')),
                    	        active       INTEGER NOT NULL DEFAULT 1 CHECK(active IN (0,1)),
                    	        FOREIGN KEY(restaurant_id) REFERENCES restaurants(restaurant_id)
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
                            FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                            FOREIGN KEY(role_id) REFERENCES user_roles(role_id) ON DELETE CASCADE
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
                            FOREIGN KEY(permission_id) REFERENCES permissions(permission_id) ON DELETE CASCADE,
                            FOREIGN KEY(role_id) REFERENCES user_roles(role_id) ON DELETE CASCADE
                        );
                    """);

                    stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS bookings (
                            booking_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            user_id INTEGER,
                            restaurant_id INTEGER NOT NULL,
                            number_of_guests INTEGER NOT NULL CHECK(number_of_guests >= 1),
                            booking_date TEXT NOT NULL,
                            booking_time TEXT NOT NULL,
                            special_requests TEXT,
                            booking_status TEXT NOT NULL DEFAULT 'PENDING'
                                CHECK(booking_status IN ('PENDING','CONFIRMED','SEATED','CANCELLED')),
                            created TEXT NOT NULL DEFAULT(datetime('now')),
                            FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                            FOREIGN KEY(restaurant_id) REFERENCES restaurants(restaurant_id) ON DELETE CASCADE
                        );
                    """);

                    stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS restaurant_tables (
                            table_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            restaurant_id INTEGER NOT NULL,
                            table_number INTEGER NOT NULL UNIQUE,
                            min_capacity INTEGER NOT NULL CHECK(min_capacity >= 1),
                            max_capacity INTEGER NOT NULL CHECK(max_capacity >= min_capacity),
                            can_combine INTEGER NOT NULL CHECK(can_combine IN (0,1)),
                            FOREIGN KEY(restaurant_id) REFERENCES restaurants(restaurant_id)
                        );
                    """);

                    stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS booking_tables (
                            booking_id INTEGER NOT NULL,
                            table_id INTEGER NOT NULL,
                            PRIMARY KEY(booking_id, table_id),
                            FOREIGN KEY(booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
                            FOREIGN KEY(table_id) REFERENCES restaurant_tables(table_id) ON DELETE CASCADE
                        );
                    """);

                    stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS waitlists (
                            waitlist_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            restaurant_id INTEGER NOT NULL,
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
                            created TEXT NOT NULL DEFAULT(datetime('now')),
                            FOREIGN KEY(restaurant_id) REFERENCES restaurants(restaurant_id),
                            FOREIGN KEY(user_id) REFERENCES users(user_id),
                            FOREIGN KEY(host_id) REFERENCES users(user_id)
                        );
                    """);

                    stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS waitlist_tables (
                            waitlist_id INTEGER NOT NULL,
                            table_id INTEGER NOT NULL,
                            PRIMARY KEY(waitlist_id, table_id),
                            FOREIGN KEY(waitlist_id) REFERENCES waitlists(waitlist_id) ON DELETE CASCADE,
                            FOREIGN KEY(table_id) REFERENCES restaurant_tables(table_id) ON DELETE CASCADE
                        );
                    """);

                    // ========== INDEXES ==========
                    stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);");
                    stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone_number);");
                    stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_bookings_user ON bookings(user_id);");
                    stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_restaurant_tables_restaurant ON restaurant_tables(restaurant_id);");
                    stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_bookings_restaurant ON bookings(restaurant_id);");
                    stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_users_restaurant ON users(restaurant_id);");


                    // ========== DEFAULT DATA ==========
                    stmt.executeUpdate("""
                        INSERT OR IGNORE INTO user_roles(role_name)
                        VALUES ('ADMIN'), ('MANAGER'), ('EMPLOYEE'), ('HOST'), ('CUSTOMER');
                    """);

                    stmt.executeUpdate("""
                        INSERT OR IGNORE INTO restaurants
                        (restaurant_id, name, address, phone, description)
                        VALUES
                          (1, 'Central Grill', '100 Main St', '(555)000-1111', 'Modern American'),
                          (2, 'Sushi Palace', '200 Ocean Ave', '(555)222-3333', 'Fresh sushi'),
                          (3, 'Pasta Corner', '300 Olive Rd', '(555)444-5555', 'Italian dining');
                    """);

                    stmt.executeUpdate("""
                    	    INSERT OR IGNORE INTO users
                    	        (user_id, username, first_name, last_name, email, password, phone_number, restaurant_id, active)
                    	    VALUES
                    	        (1, 'admin', 'Admin', 'User', 'admin@centralgrill.com',  'admin', '1234564789', 1, 1),
                    	        (2, 'admin', 'Admin', 'User', 'admin@sushipalace.com',   'admin', '1234566789', 2, 1),
                    	        (3, 'admin', 'Admin', 'User', 'admin@pastacorner.com',   'admin', '1234556789', 3, 1);
                    	""");

                    stmt.executeUpdate("""
                    	    INSERT OR IGNORE INTO users
                    	        (username, first_name, last_name, email, password, phone_number, restaurant_id, active)
                    	    VALUES
                    	        ('cg_host',  'Central', 'Host',  'host@centralgrill.com',  'password', '5550002000', 1, 1),
                    	        ('sp_host',  'Sushi',   'Host',  'host@sushipalace.com',  'password', '5550002001', 2, 1),
                    	        ('pc_host',  'Pasta',   'Host',  'host@pastacorner.com',  'password', '5550002002', 3, 1)
                    	""");
                    stmt.executeUpdate("""
                    	    INSERT OR IGNORE INTO users_to_user_roles(user_id, role_id)
                    	    SELECT u.user_id, r.role_id
                    	    FROM users u
                    	    JOIN user_roles r ON r.role_name = 'HOST'
                    	    WHERE u.email IN (
                    	        'host@centralgrill.com',
                    	        'host@sushipalace.com',
                    	        'host@pastacorner.com'
                    	    );
                    	""");

                    stmt.executeUpdate("""
                        INSERT OR IGNORE INTO users_to_user_roles(user_id, role_id)
                        SELECT u.user_id, r.role_id
                        FROM users u
                        JOIN user_roles r ON r.role_name='ADMIN'
                        WHERE u.email IN (
                            'admin@centralgrill.com',
                            'admin@sushipalace.com',
                            'admin@pastacorner.com'
                        );
                    """);

                    conn.commit();
                } catch (Exception ex) {
                    conn.rollback();
                    throw ex;
                } finally {
                    conn.setAutoCommit(true);
                }
            }

            // Load seed CSVs
            loadUsersFromCsv(ctx, "/WEB-INF/data/users.csv", url);
            loadUserRolesFromCsv(ctx, "/WEB-INF/data/user_roles.csv", url);
            loadRestaurantTables(ctx, "/WEB-INF/data/restaurant_tables.csv", url);
            loadBookings(ctx, "/WEB-INF/data/bookings.csv", url);
            loadWaitlists(ctx, "/WEB-INF/data/waitlists.csv", url);
            loadWaitlistTableLinks(ctx, "/WEB-INF/data/waitlist_tables_link.csv", url);

            System.out.println("CSV seed data loaded successfully.");
            System.out.println("Database ready at: " + dbFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------
    // ========== CSV LOADING HELPERS ==========
    // ----------------------------------------------------

    private void loadUsersFromCsv(ServletContext ctx, String resourcePath, String url) throws Exception {
        try (var in = ctx.getResourceAsStream(resourcePath)) {

            if (in == null) {
                System.out.println("No CSV: " + resourcePath);
                return;
            }

            try (var br = new java.io.BufferedReader(new java.io.InputStreamReader(in));
                 var conn = DriverManager.getConnection(url)) {

                try (var s = conn.createStatement()) {
                    s.execute("PRAGMA foreign_keys = ON;");
                }

                conn.setAutoCommit(false);

                String sql = """
                    INSERT OR IGNORE INTO users
                        (username, first_name, last_name, email, password, phone_number, restaurant_id, created, active)
                    VALUES (?,?,?,?,?,?,?,?,?)
                """;

                try (var ps = conn.prepareStatement(sql)) {
                    String line;
                    boolean header = false;

                    while ((line = br.readLine()) != null) {
                        if (!header) { header = true; continue; }

                        String[] p = line.split(",", -1);
                        if (p.length < 8) continue;

                        String username   = p[0].trim();
                        String firstName  = p[1].trim();
                        String lastName   = p[2].trim();
                        String email      = p[3].trim().toLowerCase();
                        String password   = p[4].trim();
                        String phone      = p[5].trim();
                        String created    = p[6].trim();
                        int    activeFlag = Integer.parseInt(p[7].trim());

                        // Infer restaurant_id from email domain
                        Integer restaurantId = null;
                        if (email.endsWith("@centralgrill.com")) {
                            restaurantId = 1;
                        } else if (email.endsWith("@sushipalace.com")) {
                            restaurantId = 2;
                        } else if (email.endsWith("@pastacorner.com")) {
                            restaurantId = 3;
                        }

                        int idx = 1;
                        ps.setString(idx++, username);
                        ps.setString(idx++, firstName);
                        ps.setString(idx++, lastName);
                        ps.setString(idx++, email);
                        ps.setString(idx++, password);
                        ps.setString(idx++, phone);

                        if (restaurantId == null) {
                            ps.setNull(idx++, java.sql.Types.INTEGER);
                        } else {
                            ps.setInt(idx++, restaurantId);
                        }

                        ps.setString(idx++, created);
                        ps.setInt(idx++, activeFlag);

                        ps.addBatch();
                    }

                    ps.executeBatch();
                    conn.commit();
                }
            }
        }
    }


    private void loadUserRolesFromCsv(ServletContext ctx, String resourcePath, String url) throws Exception {
        try (var in = ctx.getResourceAsStream(resourcePath)) {

            if (in == null) {
                System.out.println("No CSV: " + resourcePath);
                return;
            }

            try (var br = new java.io.BufferedReader(new java.io.InputStreamReader(in));
                 var conn = DriverManager.getConnection(url)) {

                try (var s = conn.createStatement()) {
                    s.execute("PRAGMA foreign_keys = ON;");
                }

                conn.setAutoCommit(false);

                String line;
                boolean header = false;

                while ((line = br.readLine()) != null) {
                    if (!header) { header = true; continue; }

                    String[] p = line.split(",", -1);
                    if (p.length < 2) continue;

                    String email = p[0].trim().toLowerCase();
                    String roleName = p[1].trim().toUpperCase();

                    Integer userId = null, roleId = null;

                    try (var ps = conn.prepareStatement("SELECT user_id FROM users WHERE email=?")) {
                        ps.setString(1, email);
                        try (var rs = ps.executeQuery()) {
                            if (rs.next()) userId = rs.getInt(1);
                        }
                    }

                    try (var ps = conn.prepareStatement("SELECT role_id FROM user_roles WHERE role_name=?")) {
                        ps.setString(1, roleName);
                        try (var rs = ps.executeQuery()) {
                            if (rs.next()) roleId = rs.getInt(1);
                        }
                    }

                    if (userId != null && roleId != null) {
                        try (var ps = conn.prepareStatement("""
                                INSERT OR IGNORE INTO users_to_user_roles(user_id, role_id)
                                VALUES (?,?)
                                """)) {
                            ps.setInt(1, userId);
                            ps.setInt(2, roleId);
                            ps.executeUpdate();
                        }
                    }
                }

                conn.commit();
            }
        }
    }


    private void loadRestaurantTables(ServletContext ctx, String resourcePath, String url) throws Exception {
        try (var in = ctx.getResourceAsStream(resourcePath)) {

            if (in == null) {
                System.out.println("No CSV: " + resourcePath);
                return;
            }

            try (var br = new java.io.BufferedReader(new java.io.InputStreamReader(in));
                 var conn = DriverManager.getConnection(url)) {

                conn.setAutoCommit(false);

                String sql = """
                    INSERT OR IGNORE INTO restaurant_tables
                    (restaurant_id, table_number, min_capacity, max_capacity, can_combine)
                    VALUES (?,?,?,?,?)
                """;

                try (var ps = conn.prepareStatement(sql)) {

                    String line;
                    boolean header = false;

                    while ((line = br.readLine()) != null) {
                        if (!header) { header = true; continue; }

                        String[] p = line.split(",", -1);
                        if (p.length < 5) continue;

                        ps.setInt(1, Integer.parseInt(p[0].trim()));
                        ps.setInt(2, Integer.parseInt(p[1].trim()));
                        ps.setInt(3, Integer.parseInt(p[2].trim()));
                        ps.setInt(4, Integer.parseInt(p[3].trim()));
                        ps.setInt(5, Integer.parseInt(p[4].trim()));

                        ps.addBatch();
                    }

                    ps.executeBatch();
                    conn.commit();
                }
            }
        }
    }


    private void loadBookings(ServletContext ctx, String resourcePath, String url) throws Exception {
        try (var in = ctx.getResourceAsStream(resourcePath)) {

            if (in == null) {
                System.out.println("No CSV: " + resourcePath);
                return;
            }

            try (var br = new java.io.BufferedReader(new java.io.InputStreamReader(in));
                 var conn = DriverManager.getConnection(url)) {

                conn.setAutoCommit(false);

                String findUser = "SELECT user_id FROM users WHERE email=?";
                String findTable = "SELECT table_id FROM restaurant_tables WHERE table_number=?";

                String insertBooking = """
                    INSERT INTO bookings(
                        user_id, restaurant_id, number_of_guests,
                        booking_date, booking_time,
                        special_requests, booking_status, created)
                    VALUES (?,?,?,?,?,?,?,?)
                """;

                String insertLink = """
                    INSERT OR IGNORE INTO booking_tables(booking_id, table_id) 
                    VALUES (?,?)
                """;

                String lastId = "SELECT last_insert_rowid()";

                try (var psUser = conn.prepareStatement(findUser);
                     var psTable = conn.prepareStatement(findTable);
                     var psIns = conn.prepareStatement(insertBooking);
                     var psLink = conn.prepareStatement(insertLink);
                     var psLast = conn.prepareStatement(lastId)) {

                    String line;
                    boolean header = false;

                    while ((line = br.readLine()) != null) {
                        if (!header) { header = true; continue; }

                        String[] p = line.split(",", -1);
                        if (p.length < 9) continue;

                        String email = p[0].trim().toLowerCase();
                        int restaurantId = Integer.parseInt(p[1].trim());
                        int guests = Integer.parseInt(p[2].trim());

                        // Convert MM/dd/yyyy → yyyy-MM-dd
                        String rawDate = p[3].trim();
                        String bDateIso = rawDate;

                        try {
                            var srcFmt = java.time.format.DateTimeFormatter.ofPattern("M/d/yyyy");
                            var ld = java.time.LocalDate.parse(rawDate, srcFmt);
                            bDateIso = ld.toString();
                        } catch (Exception ignore) {
                        }

                        String bTime = p[4].trim();
                        String req = p[5].trim();
                        String status = p[6].trim();
                        String created = p[7].trim();
                        int tableNumber = Integer.parseInt(p[8].trim());

                        // user_id lookup
                        Integer userId = null;
                        psUser.setString(1, email);
                        try (var rs = psUser.executeQuery()) {
                            if (rs.next()) userId = rs.getInt(1);
                        }

                        if (userId == null) continue;

                        // table_id lookup
                        Integer tableId = null;
                        psTable.setInt(1, tableNumber);
                        try (var rs = psTable.executeQuery()) {
                            if (rs.next()) tableId = rs.getInt(1);
                        }

                        // booking insert
                        int idx = 1;
                        psIns.setInt(idx++, userId);
                        psIns.setInt(idx++, restaurantId);
                        psIns.setInt(idx++, guests);
                        psIns.setString(idx++, bDateIso);
                        psIns.setString(idx++, bTime);
                        psIns.setString(idx++, req);
                        psIns.setString(idx++, status);
                        psIns.setString(idx++, created);

                        psIns.executeUpdate();

                        // fetch booking_id
                        int bookingId;
                        try (var rs = psLast.executeQuery()) {
                            rs.next();
                            bookingId = rs.getInt(1);
                        }

                        if (tableId != null) {
                            psLink.setInt(1, bookingId);
                            psLink.setInt(2, tableId);
                            psLink.executeUpdate();
                        }
                    }

                    conn.commit();
                }
            }
        }
    }


    private void loadWaitlists(ServletContext ctx, String resourcePath, String url) throws Exception {
        try (var in = ctx.getResourceAsStream(resourcePath)) {

            if (in == null) {
                System.out.println("No CSV: " + resourcePath);
                return;
            }

            try (var br = new java.io.BufferedReader(new java.io.InputStreamReader(in));
                 var conn = DriverManager.getConnection(url)) {

                conn.setAutoCommit(false);

                String findUser = "SELECT user_id FROM users WHERE email=?";

                // FIXED: **13 placeholders** matching 13 columns
                String insertWL = """
                    INSERT INTO waitlists(
                        restaurant_id,
                        user_id,
                        customer_name,
                        customer_phone_number,
                        party_size,
                        queue_position,
                        status,
                        estimated_wait_time,
                        arrived_at,
                        seated_at,
                        host_id,
                        special_requests,
                        created
                    )
                    VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)
                """;

                try (var psFind = conn.prepareStatement(findUser);
                     var psIns = conn.prepareStatement(insertWL)) {

                    String line;
                    boolean header = false;

                    while ((line = br.readLine()) != null) {
                        if (!header) { header = true; continue; }

                        String[] p = line.split(",", -1);
                        if (p.length < 12) continue;

                        String custEmail = p[0].trim().toLowerCase();
                        String custName = p[1].trim();
                        String custPhone = p[2].trim();
                        int party = Integer.parseInt(p[3].trim());
                        String posStr = p[4].trim();
                        Integer queuePos = posStr.isEmpty() ? null : Integer.valueOf(posStr);
                        String status = p[5].trim();
                        String estWait = p[6].trim();
                        String arrivedAt = p[7].trim();
                        String seatedAt = p[8].trim();
                        String hostEmail = p[9].trim().toLowerCase();
                        String requests = p[10].trim();
                        String created = p[11].trim();

                        Integer userId = null, hostId = null;

                        if (!custEmail.isEmpty()) {
                            psFind.setString(1, custEmail);
                            try (var rs = psFind.executeQuery()) {
                                if (rs.next()) userId = rs.getInt(1);
                            }
                        }

                        if (!hostEmail.isEmpty()) {
                            psFind.setString(1, hostEmail);
                            try (var rs = psFind.executeQuery()) {
                                if (rs.next()) hostId = rs.getInt(1);
                            }
                        }

                        int idx = 1;
                        psIns.setInt(idx++, 1); // restaurant_id default

                        if (userId == null) psIns.setNull(idx++, java.sql.Types.INTEGER);
                        else psIns.setInt(idx++, userId);

                        psIns.setString(idx++, custName);
                        psIns.setString(idx++, custPhone);
                        psIns.setInt(idx++, party);

                        if (queuePos == null) psIns.setNull(idx++, java.sql.Types.INTEGER);
                        else psIns.setInt(idx++, queuePos);

                        psIns.setString(idx++, status);
                        psIns.setString(idx++, estWait);
                        psIns.setString(idx++, arrivedAt);

                        if (seatedAt.isEmpty()) psIns.setNull(idx++, java.sql.Types.VARCHAR);
                        else psIns.setString(idx++, seatedAt);

                        if (hostId == null) psIns.setNull(idx++, java.sql.Types.INTEGER);
                        else psIns.setInt(idx++, hostId);

                        psIns.setString(idx++, requests);
                        psIns.setString(idx++, created);

                        psIns.executeUpdate();
                    }

                    conn.commit();
                }
            }
        }
    }


    private void loadWaitlistTableLinks(ServletContext ctx, String resourcePath, String url) throws Exception {
        try (var in = ctx.getResourceAsStream(resourcePath)) {

            if (in == null) {
                System.out.println("No CSV: " + resourcePath);
                return;
            }

            try (var br = new java.io.BufferedReader(new java.io.InputStreamReader(in));
                 var conn = DriverManager.getConnection(url)) {

                conn.setAutoCommit(false);

                String findHost = "SELECT user_id FROM users WHERE email=?";
                String findWait = """
                    SELECT waitlist_id FROM waitlists
                    WHERE arrived_at=? AND ( (? IS NULL AND host_id IS NULL) OR host_id=? )
                    LIMIT 1
                """;
                String findTable = "SELECT table_id FROM restaurant_tables WHERE table_number=?";
                String insertWT = """
                    INSERT OR IGNORE INTO waitlist_tables(waitlist_id, table_id)
                    VALUES (?,?)
                """;

                try (var psHost = conn.prepareStatement(findHost);
                     var psWait = conn.prepareStatement(findWait);
                     var psTable = conn.prepareStatement(findTable);
                     var psIns = conn.prepareStatement(insertWT)) {

                    String line;
                    boolean header = false;

                    while ((line = br.readLine()) != null) {
                        if (!header) { header = true; continue; }

                        String[] p = line.split(",", -1);
                        if (p.length < 3) continue;

                        String arrivedAt = p[0].trim();
                        String hostEmail = p[1].trim().toLowerCase();
                        int tableNumber = Integer.parseInt(p[2].trim());

                        Integer hostId = null;

                        if (!hostEmail.isEmpty()) {
                            psHost.setString(1, hostEmail);
                            try (var rs = psHost.executeQuery()) {
                                if (rs.next()) hostId = rs.getInt(1);
                            }
                        }

                        psWait.setString(1, arrivedAt);

                        if (hostId == null) {
                            psWait.setNull(2, java.sql.Types.INTEGER);
                            psWait.setNull(3, java.sql.Types.INTEGER);
                        } else {
                            psWait.setInt(2, hostId);
                            psWait.setInt(3, hostId);
                        }

                        Integer waitlistId = null;
                        try (var rs = psWait.executeQuery()) {
                            if (rs.next()) waitlistId = rs.getInt(1);
                        }

                        if (waitlistId == null) continue;

                        Integer tableId = null;
                        psTable.setInt(1, tableNumber);

                        try (var rs = psTable.executeQuery()) {
                            if (rs.next()) tableId = rs.getInt(1);
                        }

                        if (tableId == null) continue;

                        psIns.setInt(1, waitlistId);
                        psIns.setInt(2, tableId);
                        psIns.executeUpdate();
                    }

                    conn.commit();
                }
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
