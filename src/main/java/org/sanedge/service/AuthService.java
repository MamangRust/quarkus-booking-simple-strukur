package org.sanedge.service;

import org.sanedge.domain.request.auth.ForgotRequest;
import org.sanedge.domain.request.auth.LoginRequest;
import org.sanedge.domain.request.auth.RegisterRequest;
import org.sanedge.domain.request.auth.ResetPasswordRequest;
import org.sanedge.domain.response.MessageResponse;
import org.sanedge.domain.response.auth.TokenRefreshResponse;
import org.sanedge.models.User;

public interface AuthService {
    public MessageResponse login(LoginRequest loginRequest);

    public MessageResponse register(RegisterRequest registerRequest);

    public TokenRefreshResponse refreshToken(String refreshToken);

    public User getCurrentUser();

    public MessageResponse logout();

    public MessageResponse forgotPassword(ForgotRequest request);

    public MessageResponse resetPassword(ResetPasswordRequest request);

    public MessageResponse verifyEmail(String token);
}
