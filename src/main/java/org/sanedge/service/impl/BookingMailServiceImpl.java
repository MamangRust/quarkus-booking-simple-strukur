package org.sanedge.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.sanedge.service.BookingMailService;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class BookingMailServiceImpl implements BookingMailService {

    @Inject
    Mailer mailer;

    @Inject
    Template checkInTemplate;

    @Inject
    Template checkOutTemplate;

    @Inject
    Template bookingTimeTemplate;

    @Override
    public void sendEmailCheckIn(String orderId, String email, String formattedDate) {
        try {
            log.info("Sending check-in email for orderId: {} to email: {}", orderId, email);

            TemplateInstance templateInstance = checkInTemplate.data("orderId", orderId).data("formattedDate",
                    formattedDate);
            sendMail(email, "Check-In Confirmation", templateInstance);

            log.info("Check-in email sent successfully to: {}", email);

        } catch (Exception e) {
            throw new RuntimeException("Error sending check-in email", e);
        }
    }

    @Override
    public void sendEmailCheckOut(String orderId, String email, String formattedDate) {
        try {
            log.info("Sending check-out email for orderId: {} to email: {}", orderId, email);

            TemplateInstance templateInstance = checkOutTemplate.data("orderId", orderId).data("formattedDate",
                    formattedDate);
            sendMail(email, "Check-Out Confirmation", templateInstance);

            log.info("Check-out email sent successfully to: {}", email);

        } catch (Exception e) {
            throw new RuntimeException("Error sending check-out email", e);
        }
    }

    @Override
    public void sendEmailBookingTime(String orderId, String email, LocalDateTime localDateTime) {
        try {
            log.info("Sending booking time notification email for orderId: {} to email: {}", orderId, email);

            TemplateInstance templateInstance = bookingTimeTemplate.data("orderId", orderId).data("formattedDate",
                    localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            sendMail(email, "Booking Time Notification", templateInstance);

            log.info("Booking time notification email sent successfully to: {}", email);

        } catch (Exception e) {
            throw new RuntimeException("Error sending booking time notification email", e);
        }
    }

    private void sendMail(String to, String subject, TemplateInstance templateInstance) {
        String body = templateInstance.render();

        ArrayList<String> tos = new ArrayList<>();
        tos.add(to);

        Mail mail = new Mail()
                .setSubject(subject)
                .setTo(tos)
                .setHtml(body);

        mailer.send(mail);
    }
}