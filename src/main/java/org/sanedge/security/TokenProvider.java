package org.sanedge.security;

import org.sanedge.enums.ERole;

import com.auth0.jwt.interfaces.DecodedJWT;

public interface TokenProvider {

    String createUserToken(String subject, ERole role);

    String createRefreshToken(String subject, String audience);

    DecodedJWT verify(String token);

    ERole[] extractRoles(DecodedJWT decodedJWT);
}
