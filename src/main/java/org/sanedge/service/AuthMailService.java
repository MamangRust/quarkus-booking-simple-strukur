package org.sanedge.service;

public interface AuthMailService {
    void sendEmailVerify(String email, String token);

    void sendResetPasswordEmail(String email, String resetLink);

    void sendEmailForgotPassword(String email, String resetLink);
}
