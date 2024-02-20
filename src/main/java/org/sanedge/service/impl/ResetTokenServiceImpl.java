package org.sanedge.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.sanedge.models.ResetToken;
import org.sanedge.models.User;
import org.sanedge.repository.ResetTokenRepository;
import org.sanedge.service.ResetTokenService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ResetTokenServiceImpl implements ResetTokenService {

    @Inject
    ResetTokenRepository resetTokenRepository;

    @Override
    public ResetToken createResetToken(User user) {
        ResetToken resetToken = new ResetToken();
        resetToken.user = user;
        resetToken.token = UUID.randomUUID().toString();
        resetToken.expiryDate = Instant.now().plus(24, ChronoUnit.HOURS); // Token expiry in 24 hours

        resetTokenRepository.persist(resetToken);

        return resetToken;
    }

    @Override
    public void deleteResetToken(Long userId) {
        resetTokenRepository.deleteByUserId(userId);
    }

    @Override
    public Optional<ResetToken> findByToken(String token) {
        return resetTokenRepository.findByToken(token);
    }

    @Override
    public void updateExpiryDate(ResetToken resetToken) {
        resetToken.expiryDate = Instant.now().plus(24, ChronoUnit.HOURS);
        resetTokenRepository.persist(resetToken);
    }
}
