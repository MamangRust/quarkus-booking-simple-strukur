package org.sanedge.resources;

import org.sanedge.domain.request.booking.CheckInRequest;
import org.sanedge.domain.response.MessageResponse;
import org.sanedge.enums.ERole;
import org.sanedge.security.Secured;
import org.sanedge.service.CheckService;

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

@Path("/check-in")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CheckInResource {

    @Inject
    CheckService checkService;

    @POST
    @Secured({ ERole.ROLE_ADMIN, ERole.ROLE_USER })
    @Path("/order")
    public Response checkInOrder(@Valid CheckInRequest request) {
        MessageResponse response = checkService.checkInOrder(request);

        return Response.status(response.getStatusCode()).entity(response).build();
    }

    @GET
    @Secured({ ERole.ROLE_ADMIN, ERole.ROLE_USER })
    @Path("/order/{orderId}")
    public Response checkOrder(@PathParam("orderId") String orderId) {
        MessageResponse response = checkService.checkOrder(orderId);

        return Response.status(response.getStatusCode()).entity(response).build();
    }

    @POST
    @Secured({ ERole.ROLE_ADMIN, ERole.ROLE_USER })
    @Path("/order/{orderId}/checkout")
    public Response checkOutOrder(@PathParam("orderId") String orderId) {
        MessageResponse response = checkService.checkOutOrder(orderId);

        return Response.status(response.getStatusCode()).entity(response).build();
    }
}
