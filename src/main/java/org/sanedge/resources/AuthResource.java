package org.sanedge.resources;

import org.sanedge.domain.request.auth.ForgotRequest;
import org.sanedge.domain.request.auth.LoginRequest;
import org.sanedge.domain.request.auth.RegisterRequest;
import org.sanedge.domain.request.auth.ResetPasswordRequest;
import org.sanedge.domain.request.auth.TokenRefreshRequest;
import org.sanedge.domain.response.MessageResponse;
import org.sanedge.domain.response.auth.TokenRefreshResponse;
import org.sanedge.enums.ERole;
import org.sanedge.models.User;
import org.sanedge.security.Secured;
import org.sanedge.service.AuthService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    private AuthService authService;

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest loginRequest) {
        MessageResponse response = authService.login(loginRequest);
        return Response.status(response.getStatusCode()).entity(response).build();
    }

    @POST
    @Path("/register")
    public Response register(@Valid RegisterRequest registerRequest) {
        MessageResponse response = authService.register(registerRequest);
        return Response.status(response.getStatusCode()).entity(response).build();
    }

    @GET
    @Secured({ ERole.ROLE_ADMIN, ERole.ROLE_USER })
    @Path("/me")
    public Response me() {
        User response = authService.getCurrentUser();
        return Response.status(Response.Status.OK.getStatusCode()).entity(response).build();
    }

    @GET
    @Path("/verify")
    public Response verify(@PathParam("token") String token) {
        MessageResponse response = authService.verifyEmail(token);
        return Response.status(response.getStatusCode()).entity(response).build();
    }

    @POST
    @Path("/refresh")
    public Response refresh(@Valid TokenRefreshRequest request) {
        TokenRefreshResponse response = authService.refreshToken(request.getRefreshToken());
        return Response.status(Response.Status.OK.getStatusCode()).entity(response).build();
    }

    @POST
    @Path("/forgot")
    public Response forgot(@Valid ForgotRequest forgotRequest) {
        MessageResponse response = authService.forgotPassword(forgotRequest);
        return Response.status(response.getStatusCode()).entity(response).build();
    }

    @POST
    @Path("/reset")
    public Response reset(@Valid ResetPasswordRequest resetRequest) {
        MessageResponse response = authService.resetPassword(resetRequest);
        return Response.status(response.getStatusCode()).entity(response).build();
    }

    @POST
    @Path("/logout")
    public Response logout() {
        MessageResponse response = authService.logout();
        return Response.status(response.getStatusCode()).entity(response).build();
    }
}
