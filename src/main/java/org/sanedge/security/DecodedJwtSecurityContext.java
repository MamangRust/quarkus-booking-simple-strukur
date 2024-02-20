package org.sanedge.security;

import java.security.Principal;

import org.sanedge.enums.ERole;

import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.ws.rs.core.SecurityContext;

public class DecodedJwtSecurityContext implements SecurityContext {
    private final DecodedJWT decodedJWT;
    private final TokenProvider tokenProvider;

    public DecodedJwtSecurityContext(DecodedJWT decodedJWT, TokenProvider tokenProvider) {
        this.decodedJWT = decodedJWT;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Principal getUserPrincipal() {
        return decodedJWT::getSubject;
    }

    @Override
    public boolean isUserInRole(String role) {
        ERole[] tokenRoles = tokenProvider.extractRoles(decodedJWT);

        for (ERole tokenRole : tokenRoles) {
            if (role.equals(tokenRole.name())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public String getAuthenticationScheme() {
        return null;
    }
}
