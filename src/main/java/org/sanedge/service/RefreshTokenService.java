package org.sanedge.service;

import java.util.Optional;

import org.sanedge.models.RefreshToken;
import org.sanedge.models.User;

public interface RefreshTokenService {
    Optional<RefreshToken> findByToken(String token);

    RefreshToken createRefreshToken(Long userId);

    RefreshToken verifyExpiration(RefreshToken token);

    Optional<RefreshToken> findByUser(User user);

    RefreshToken updateExpiryDate(RefreshToken refreshToken);

    int deleteByUserId(Long userId);
}
