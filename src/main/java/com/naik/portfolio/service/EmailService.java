package com.naik.portfolio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Email notification service.
 * Sends email alerts to the portfolio owner when a new contact message
 * or meeting booking is received.
 */
@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${portfolio.notification.email:narasimhanaik591@gmail.com}")
    private String notificationEmail;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${portfolio.backend.base-url:https://portfolio-backend-d47l.onrender.com}")
    private String backendBaseUrl;

    /**
     * Sends an email notification asynchronously.
     * If mail sender is not configured, logs and skips silently.
     */
    @Async
    public void sendNotification(String subject, String body) {
        if (mailSender == null || fromEmail == null || fromEmail.isEmpty()) {
            System.out.println("[EmailService] Mail not configured — skipping notification: " + subject);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(notificationEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            System.out.println("[EmailService] ✅ Notification sent: " + subject);
        } catch (Exception e) {
            System.err.println("[EmailService] ❌ Failed to send email: " + e.getMessage());
        }
    }

    /**
     * Notify owner about a new contact message.
     */
    @Async
    public void notifyNewContactMessage(String name, String email, String messageText) {
        String subject = "📬 New Portfolio Contact: " + name;
        String body = String.format(
            "New contact form submission received!\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "Name: %s\n" +
            "Email: %s\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "Message:\n%s\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "View all messages: %s/admin\n" +
            "API: %s/api/contact",
            name, email, messageText, backendBaseUrl, backendBaseUrl
        );
        sendNotification(subject, body);
    }

    /**
     * Notify owner about a new meeting booking.
     */
    @Async
    public void notifyNewMeetingBooking(String name, String email, String topic, String date, String time) {
        String startGCal = date.replace("-", "") + "T" + time.replace(":", "") + "00";
        String endGCal = startGCal;
        try {
            String[] timeParts = time.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int min = Integer.parseInt(timeParts[1]);
            min += 30;
            if (min >= 60) {
                min -= 60;
                hour += 1;
                if (hour >= 24) {
                    hour = 0;
                }
            }
            String endHour = String.format("%02d", hour);
            String endMin = String.format("%02d", min);
            endGCal = date.replace("-", "") + "T" + endHour + endMin + "00";
        } catch (Exception e) {
            // fallback
        }

        String gcalUrl = "";
        try {
            gcalUrl = "https://calendar.google.com/calendar/render?action=TEMPLATE" +
                "&text=" + java.net.URLEncoder.encode("Meeting with " + name + ": " + topic, java.nio.charset.StandardCharsets.UTF_8) +
                "&dates=" + startGCal + "/" + endGCal +
                "&details=" + java.net.URLEncoder.encode("Hi " + name + ",\n\nThanks for booking a meeting. Topic: " + topic + "\n\nThis meeting is scheduled to use Google Meet. Please save this event to your calendar to automatically generate the Google Meet link and invite both of us.", java.nio.charset.StandardCharsets.UTF_8) +
                "&add=" + java.net.URLEncoder.encode("narasimhanaik591@gmail.com," + email, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            gcalUrl = "https://calendar.google.com/calendar/render?action=TEMPLATE";
        }

        String subject = "📅 New Meeting Booking: " + name + " — " + topic;
        String body = String.format(
            "New meeting booking received!\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "Name: %s\n" +
            "Email: %s\n" +
            "Topic: %s\n" +
            "Preferred Date: %s\n" +
            "Preferred Time: %s\n" +
            "Status: PENDING\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "Add to Google Calendar & automatically generate Google Meet link:\n" +
            "%s\n\n" +
            "View all bookings: %s/admin\n" +
            "API: %s/api/meetings",
            name, email, topic, date, time, gcalUrl, backendBaseUrl, backendBaseUrl
        );
        sendNotification(subject, body);
    }
}
