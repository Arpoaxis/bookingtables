# Restaurant Booking & Operations — Academic Team Project

A Java/Jakarta Servlet and JSP restaurant reservations and operations web application developed as a **five-person software engineering course project in 2025**.

**Team members**
- Gerard McCallion
- David Sanchez
- Allen Tran
- Henry Le
- Hubert Nguyen

The application includes reservation and waitlist flows, restaurant staff and administrative views, user/role data, and table-management interfaces. These are **team accomplishments**; no single contributor is credited with implementing the entire application.

The accompanying [portfolio case study](https://arpoaxis.github.io/projects/restaurant-booking.html) describes verified examples, screenshots, individual contributions where supported by repository history, and known limitations.

## Technology and project layout

- **Java 17** as configured in Maven's `maven.compiler.release`
- Jakarta Servlets/JSP, Apache Tomcat, Maven (WAR packaging)
- SQLite / JDBC database access
- HTML, CSS and JavaScript interfaces
- BCrypt authentication; existing plaintext demonstration passwords migrate to hashes in local SQLite at initialization

The Maven application is under `RestaurantBooking/`:

- `pom.xml` — build configuration and dependencies
- `src/main/java/com/restaurant/` — application code
- `src/main/webapp/` — pages, styles, JSP views and application `WEB-INF/web.xml`
- `src/main/webapp/WEB-INF/data/` — fictional demonstration seed data
- `src/main/resources/email.properties.example` — **example only**, not real SMTP credentials

Local Eclipse/Tomcat workspace settings and compiled `.class` files are not intended to be version-controlled; Maven produces its own build output.

## Build outline

From a Java 17 and Maven environment:

```bash
cd RestaurantBooking
mvn clean package
```

The POM configures WAR packaging with final name `RestaurantBooking`; the expected artifact is `target/RestaurantBooking.war` if the build succeeds. Deploy through a **locally configured** compatible Tomcat server. Database paths, runtime initialization and optional email configuration may need adjustment for a new machine.

**Reproducibility note:** This README describes the checked-in project structure and intended Maven build; a clean-machine build, deployment and end-to-end test have not yet been independently verified.

## Demonstration data and safety

All committed seeded customer, employee, booking, and role data were confirmed by a project participant as **fictional**. Seed accounts and passwords are for the course demonstration only. Do not reuse their credentials for any real service.

This is an **academic prototype, not a production-ready reservation or authentication system**. The updated source removes plaintext comparison at login, converts historical demo database values to BCrypt on startup, and uses unpredictable temporary passwords for reset and account-administration flows. The fictional seed CSVs and historical Git commits still contain demo plaintext strings. The email reset remains a temporary-password approach rather than a single-use expiring token. Account verification, rate limiting, secure employee/customer onboarding, and end-to-end security testing remain future work. New customer accounts created through staff waitlist entry require a configured email reset to choose a known password. Do not host the application on the public internet with the supplied demo accounts or configurations.

`email.properties.example` contains placeholders; any working `email.properties` must remain untracked and must use privately managed credentials. Local Tomcat manager accounts and server configuration should also be created privately for each development environment.

**Git history note:** Removing local files from the current branch does **not** erase earlier public commits. If real credentials were ever used in old server configuration, they should be rotated; removing a tracked file is not equivalent to credential revocation or a history rewrite.

## Publishing and attribution

This repository is **public** for historical academic project review. It was built by a five-person team, and team permission to publish was confirmed. Source-code reuse should respect the team's contributions and the repository's existing `LICENCE` file.

The original project and its development history are preserved. This README does not claim independent clean-machine reproduction, production deployment, or complete security validation. Authentication changes require local compilation and functional tests before being considered verified.
