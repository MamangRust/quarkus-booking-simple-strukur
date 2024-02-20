package org.sanedge.service.impl;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.sanedge.models.RefreshToken;
import org.sanedge.models.User;
import org.sanedge.repository.RefreshTokenRepository;
import org.sanedge.repository.UserRepository;
import org.sanedge.service.RefreshTokenService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Inject
    UserRepository userRepository;

    @Inject
    RefreshTokenRepository refreshTokenRepository;

    // Sesuaikan dengan nilai yang sesuai dengan konfigurasi Quarkus Anda
    private Long refreshTokenDurationMs = 60000L; // Contoh: 1 menit

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public Optional<RefreshToken> findByUser(User user) {
        return refreshTokenRepository.findByUser(user);
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();

        User user = userRepository.findById(userId);
        refreshToken.user = user;
        refreshToken.expiryDate = Instant.now().plusMillis(refreshTokenDurationMs);
        refreshToken.token = UUID.randomUUID().toString();

        refreshTokenRepository.persist(refreshToken);

        return refreshToken;
    }

    @Override
    @Transactional
    public RefreshToken updateExpiryDate(RefreshToken refreshToken) {
        refreshToken.expiryDate = Instant.now().plusMillis(refreshTokenDurationMs);

        refreshTokenRepository.persist(refreshToken);

        return refreshToken;

    }

    @Override
    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.expiryDate.isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(
                    "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Override
    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId));
    }
}