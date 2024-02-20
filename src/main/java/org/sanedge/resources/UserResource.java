package org.sanedge.resources;

import org.sanedge.enums.ERole;
import org.sanedge.security.Secured;
import org.sanedge.security.TokenProvider;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/user")
public class UserResource {
    private final TokenProvider tokenProvider;

    @Inject
    public UserResource(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @GET
    @Secured({ ERole.ROLE_ADMIN, ERole.ROLE_USER })
    @Produces(MediaType.APPLICATION_JSON)
    public String hello(@Context SecurityContext securityContext) {
        final var principal = securityContext.getUserPrincipal().getName();

        return "Hello " + principal;
    }
}
