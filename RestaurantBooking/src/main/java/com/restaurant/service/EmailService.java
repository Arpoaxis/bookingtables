package com.restaurant.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailService {

    private static final String FROM_EMAIL = "your_email@gmail.com";//create email and add it here 
    private static final String PASSWORD = "your_app_password"; // get app password from email settings

    public static void sendBookingConfirmation(String to, String restaurantName, String date, String time, int guests) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Your Reservation Confirmation");

            String body = """
                Hello,

                Your reservation is confirmed.

                Restaurant: %s
                Date: %s
                Time: %s
                Guests: %d

                Thank you for booking with us!
                """.formatted(restaurantName, date, time, guests);

            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent to: " + to);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
