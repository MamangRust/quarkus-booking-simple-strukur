package org.sanedge.service.impl;

import java.util.ArrayList;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.sanedge.service.AuthMailService;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthMailServiceImpl implements AuthMailService {

    @Inject
    Mailer mailClient;

    @Inject
    Template verifyEmailTemplate;

    @Inject
    Template resetPasswordTemplate;

    @Inject
    Template forgotPasswordTemplate;

    @ConfigProperty(name = "quarkus.mailer.username")
    String senderEmail;

    @Override
    public void sendEmailVerify(String email, String token) {
        String subject = "Verify Email";
        TemplateInstance templateInstance = verifyEmailTemplate.data("token", token);
        sendMail(email, subject, templateInstance);
    }

    @Override
    public void sendResetPasswordEmail(String email, String resetLink) {
        String subject = "Reset Your Password";
        TemplateInstance templateInstance = resetPasswordTemplate.data("resetLink", resetLink);
        sendMail(email, subject, templateInstance);
    }

    @Override
    public void sendEmailForgotPassword(String email, String resetLink) {
        String subject = "Forgot Password";
        TemplateInstance templateInstance = forgotPasswordTemplate.data("resetLink", resetLink);
        sendMail(email, subject, templateInstance);
    }

    private void sendMail(String to, String subject, TemplateInstance templateInstance) {
        String body = templateInstance.render();

        ArrayList<String> tos = new ArrayList<>();

        tos.add(to);

        Mail mail = new Mail()
                .setFrom(senderEmail)
                .setSubject(subject)
                .setTo(tos)
                .setHtml(body);

        mailClient.send(mail);
    }
}