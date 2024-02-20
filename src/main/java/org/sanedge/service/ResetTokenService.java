package org.sanedge.service;

import java.util.Optional;

import org.sanedge.models.ResetToken;
import org.sanedge.models.User;

public interface ResetTokenService {
    ResetToken createResetToken(User user);

    void deleteResetToken(Long userId);

    Optional<ResetToken> findByToken(String token);

    void updateExpiryDate(ResetToken resetToken);
}
