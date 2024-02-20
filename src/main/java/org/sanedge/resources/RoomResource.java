package org.sanedge.resources;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.sanedge.domain.request.room.CreateRoomRequest;
import org.sanedge.domain.request.room.UpdateRoomRequest;
import org.sanedge.domain.response.MessageResponse;
import org.sanedge.enums.ERole;
import org.sanedge.security.Secured;
import org.sanedge.service.RoomService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/room")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    @Inject
    RoomService roomService;

    @GET
    @Secured({ ERole.ROLE_ADMIN, ERole.ROLE_USER })
    public MessageResponse getAllRoom() {
        return roomService.findAll();
    }

    @GET
    @Secured({ ERole.ROLE_ADMIN, ERole.ROLE_USER })
    @Path("/{id}")
    public MessageResponse getRoomById(@PathParam("id") Long id) {
        return roomService.findById(id);
    }

    @POST
    @Secured({ ERole.ROLE_ADMIN, ERole.ROLE_USER })
    @Path("/create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createRoom(@MultipartForm CreateRoomRequest form) {
        CreateRoomRequest createRoomRequest = new CreateRoomRequest();
        createRoomRequest.setRoomName(form.getRoomName());
        createRoomRequest.setRoomCapacity(form.getRoomCapacity());
        createRoomRequest.setFile(form.getFile());
        MessageResponse room = roomService.createRoom(createRoomRequest);
        return Response.ok(room).build();
    }

    @PUT
    @Secured({ ERole.ROLE_ADMIN, ERole.ROLE_USER })
    @Path("/update/{id}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updateRoom(@PathParam("id") Long id, @MultipartForm UpdateRoomRequest form) {
        UpdateRoomRequest updateRoomRequest = new UpdateRoomRequest();
        updateRoomRequest.setRoomName(form.getRoomName());
        updateRoomRequest.setRoomCapacity(form.getRoomCapacity());
        updateRoomRequest.setFile(form.getFile());
        MessageResponse room = roomService.updateRoom(id, updateRoomRequest);
        return Response.ok(room).build();
    }

    @DELETE
    @Secured({ ERole.ROLE_ADMIN, ERole.ROLE_USER })
    @Path("/delete/{id}")
    public Response deleteRoom(@PathParam("id") Long id) {
        MessageResponse room = roomService.deleteRoom(id);
        return Response.ok(room).build();
    }
}