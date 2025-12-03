package com.restaurant.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EmailService {

    private static String FROM_EMAIL;
    private static String PASSWORD;

    // Load creds from classpath: email.properties
    static {
        try (InputStream in = EmailService.class.getClassLoader()
                                               .getResourceAsStream("email.properties")) {
            if (in != null) {
                Properties cfg = new Properties();
                cfg.load(in);
                FROM_EMAIL = cfg.getProperty("smtp.user");
                PASSWORD   = cfg.getProperty("smtp.pass");
                System.out.println("[EmailService] Loaded SMTP config for user: " + FROM_EMAIL);
            } else {
                System.out.println("[EmailService] WARNING: email.properties not found on classpath.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[EmailService] ERROR: Failed to load email.properties");
        }
    }

    private static Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });
    }

    // Generic helper we can reuse for any email
    public static void sendSimpleEmail(String to, String subject, String body) {
        if (FROM_EMAIL == null || PASSWORD == null) {
            System.out.println("[EmailService] Email not configured (missing email.properties); skipping send.");
            return;
        }

        Session session = createSession();

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("[EmailService] Email sent to: " + to);

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("[EmailService] Failed to send email to: " + to);
        }
    }

    // Existing booking-specific method now delegates to the generic one
    public static void sendBookingConfirmation(String to,
                                               String restaurantName,
                                               String date,
                                               String time,
                                               int guests) {
        String subject = "Your Reservation Confirmation";

        String body = """
            Hello,

            Your reservation is confirmed.

            Restaurant: %s
            Date: %s
            Time: %s
            Guests: %d

            Thank you for booking with us!
            """.formatted(restaurantName, date, time, guests);

        sendSimpleEmail(to, subject, body);
    }
}
