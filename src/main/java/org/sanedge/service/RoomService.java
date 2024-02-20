package org.sanedge.service;

import org.sanedge.domain.request.room.CreateRoomRequest;
import org.sanedge.domain.request.room.UpdateRoomRequest;
import org.sanedge.domain.response.MessageResponse;

public interface RoomService {
    MessageResponse findAll();

    MessageResponse findById(Long id);

    MessageResponse createRoom(CreateRoomRequest createRoomRequest);

    MessageResponse updateRoom(Long id, UpdateRoomRequest request);

    MessageResponse deleteRoom(Long id);
}
