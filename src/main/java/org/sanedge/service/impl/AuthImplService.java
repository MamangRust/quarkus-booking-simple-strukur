package org.sanedge.service.impl;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.sanedge.domain.request.auth.ForgotRequest;
import org.sanedge.domain.request.auth.LoginRequest;
import org.sanedge.domain.request.auth.RegisterRequest;
import org.sanedge.domain.request.auth.ResetPasswordRequest;
import org.sanedge.domain.response.MessageResponse;
import org.sanedge.domain.response.auth.AuthResponse;
import org.sanedge.domain.response.auth.TokenRefreshResponse;
import org.sanedge.enums.ERole;
import org.sanedge.models.RefreshToken;
import org.sanedge.models.ResetToken;
import org.sanedge.models.Role;
import org.sanedge.models.User;
import org.sanedge.repository.RoleRepository;
import org.sanedge.repository.UserRepository;
import org.sanedge.security.HashProvider;
import org.sanedge.security.JwtTokenProvider;
import org.sanedge.service.AuthMailService;
import org.sanedge.service.AuthService;
import org.sanedge.service.RefreshTokenService;
import org.sanedge.service.ResetTokenService;
import org.sanedge.utils.RandomString;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthImplService implements AuthService {
    private final AuthMailService authMailService;
    private final HashProvider hashProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenService refreshTokenService;
    private final ResetTokenService resetTokenService;
    private final SecurityIdentity securityIdentity;

    public AuthImplService(HashProvider hashProvider,
            JwtTokenProvider jwtTokenProvider, AuthMailService authMailService, UserRepository userRepository,
            RoleRepository roleRepository, RefreshTokenService refreshTokenService, SecurityIdentity securityIdentity,
            ResetTokenService resetTokenService) {

        this.hashProvider = hashProvider;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authMailService = authMailService;
        this.refreshTokenService = refreshTokenService;
        this.securityIdentity = securityIdentity;
        this.resetTokenService = resetTokenService;
    }

    @Override
    public MessageResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!hashProvider.checkPassword(loginRequest.getPassword(), user.password)) {
            throw new RuntimeException("Invalid password");
        }
        ERole primaryRole = user.getRoles().stream().findFirst()
                .map(Role::toERole)
                .orElseThrow(() -> new RuntimeException("User has no roles"));

        String token = jwtTokenProvider.createUserToken(user.getUsername(), primaryRole);

        String refrehToken = jwtTokenProvider.createRefreshToken(user.getUsername(), "refresh-token");

        Optional<RefreshToken> existingRefreshToken = refreshTokenService.findByUser(user);

        if (existingRefreshToken.isPresent()) {
            refreshTokenService.updateExpiryDate(existingRefreshToken.get());
        } else {
            refreshTokenService.deleteByUserId(user.getId());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
            existingRefreshToken = Optional.of(refreshToken);
        }

        AuthResponse authResponse = AuthResponse.builder().access_token(token).refresh_token(refrehToken)
                .expiresAt(token)
                .username(user.getUsername()).build();

        return MessageResponse.builder().message("Login success").data(authResponse).statusCode(200).build();

    }

    @Override
    public MessageResponse register(RegisterRequest registerRequest) {
        User user = new User();

        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());

        String hash = hashProvider.hashPassword(registerRequest.getPassword());

        System.out.println("Hash: " + hash);

        user.setPassword(hash);

        Set<String> strRoles = registerRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found. Admin"));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found. MOD"));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found. USER"));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        user.setVerified(true);

        String token = RandomString.generateRandomString(50);

        user.setVerificationCode(token);

        userRepository.createUser(user);

        authMailService.sendEmailVerify(registerRequest.getEmail(), token);

        return MessageResponse.builder().message("Register success").data(user).statusCode(200).build();
    }

    @Override
    public TokenRefreshResponse refreshToken(String refreshToken) {
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    ERole primaryRole = user.getRoles().stream().findFirst()
                            .map(Role::toERole)
                            .orElseThrow(() -> new RuntimeException("User has no roles"));

                    String token = jwtTokenProvider.createUserToken(user.getUsername(), primaryRole);

                    return new TokenRefreshResponse(token, refreshToken);
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not found"));
    }

    public User getCurrentUser() {
        String username = securityIdentity.getPrincipal().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public MessageResponse logout() {
        refreshTokenService.deleteByUserId(getCurrentUser().getId());

        return MessageResponse.builder().message("Logout success").statusCode(200).build();
    }

    @Override
    public MessageResponse forgotPassword(ForgotRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ResetToken resetToken = resetTokenService.createResetToken(user);

        String resetLink = "http://localhost:8080/api/auth/reset-password?token=" + resetToken.getToken();
        authMailService.sendResetPasswordEmail(user.getEmail(), resetLink);

        return MessageResponse.builder().message("Successs send email").statusCode(200).build();
    }

    @Override
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        ResetToken resetToken = resetTokenService.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            return MessageResponse.builder().message("Reset token has expired.").statusCode(400).build();
        }

        User user = resetToken.getUser();
        user.setPassword(hashProvider.hashPassword(request.getPassword()));

        userRepository.persist(user);

        resetTokenService.deleteResetToken(user.getId());

        return MessageResponse.builder().message("Password reset successfully.").statusCode(200).build();
    }

    @Override
    public MessageResponse verifyEmail(String token) {
        Optional<User> optionalUser = userRepository.findByVerificationCode(token);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setVerified(true);
            userRepository.persist(user);

            return MessageResponse.builder().message("Success verify email").statusCode(200).build();
        } else {
            return MessageResponse.builder().message("Verification code not found").statusCode(404).build();
        }
    }
}
