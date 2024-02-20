package org.sanedge.resources;

import org.sanedge.domain.request.booking.CreateBookingRequest;
import org.sanedge.domain.request.booking.UpdateBookingRequest;
import org.sanedge.domain.response.MessageResponse;
import org.sanedge.enums.ERole;
import org.sanedge.security.Secured;
import org.sanedge.service.AuthService;
import org.sanedge.service.BookingService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/bookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookingResource {

    @Inject
    BookingService bookingService;

    @Inject
    AuthService authService;

    @GET
    @Secured({ ERole.ROLE_ADMIN, ERole.ROLE_USER })
    public MessageResponse getAllBookings() {
        return bookingService.findAll();
    }

    @GET
    @Secured({ ERole.ROLE_ADMIN, ERole.ROLE_USER })
    @Path("/{id}")
    public MessageResponse getBookingById(@PathParam("id") Long id) {
        return bookingService.findById(id);
    }

    @POST
    @Secured({ ERole.ROLE_ADMIN, ERole.ROLE_USER })
    public MessageResponse createBooking(@Valid CreateBookingRequest createBookingRequest) {
        return bookingService.createBooking(authService.getCurrentUser().getId(), createBookingRequest);
    }

    @PUT
    @Secured({ ERole.ROLE_ADMIN, ERole.ROLE_USER })
    @Path("/{id}")
    public MessageResponse updateBooking(@PathParam("id") Long id,
            @Valid UpdateBookingRequest updateBookingRequest) {
        return bookingService.updateBooking(id, authService.getCurrentUser().getId(), updateBookingRequest);
    }

    @DELETE
    @Secured({ ERole.ROLE_ADMIN, ERole.ROLE_USER })
    @Path("/{id}")
    public MessageResponse deleteBookingById(@PathParam("id") Long id) {
        return bookingService.deleteById(id);
    }
}