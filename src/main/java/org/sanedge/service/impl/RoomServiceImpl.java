package org.sanedge.service.impl;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.sanedge.domain.request.room.CreateRoomRequest;
import org.sanedge.domain.request.room.UpdateRoomRequest;
import org.sanedge.domain.response.MessageResponse;
import org.sanedge.domain.response.room.RoomResponse;
import org.sanedge.mapper.RoomMapper;
import org.sanedge.models.Room;
import org.sanedge.repository.RoomRepository;
import org.sanedge.service.RoomService;
import org.sanedge.utils.RoomStatus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final FileServiceImpl fileService;
    private final FolderServiceImpl folderService;

    @Inject
    public RoomServiceImpl(RoomRepository roomRepository, FileServiceImpl fileService,
            FolderServiceImpl folderService) {
        this.roomRepository = roomRepository;
        this.fileService = fileService;
        this.folderService = folderService;
    }

    @Override
    public MessageResponse findAll() {
        try {
            List<Room> roomList = roomRepository.listAll();
            return MessageResponse.builder()
                    .message("Success")
                    .data(RoomMapper.toRoomResponseList(roomList))
                    .statusCode(400)
                    .build();
        } catch (Exception e) {
            log.error("Error occurred while fetching rooms", e);
            throw new WebApplicationException("Error occurred while fetching rooms", e, 400);
        }
    }

    @Override
    public MessageResponse findById(Long id) {
        try {
            Optional<Room> room = roomRepository.findByIdOptional(id);
            if (room.isPresent()) {
                return MessageResponse.builder()
                        .message("Success")
                        .data(RoomMapper.toRoomResponse(room.get()))
                        .statusCode(200)
                        .build();
            } else {
                return MessageResponse.builder()
                        .message("Room not found")
                        .statusCode(400)
                        .build();
            }
        } catch (Exception e) {
            log.error("Error occurred while fetching room by ID", e);
            throw new WebApplicationException("Error occurred while fetching room by ID", e,
                    400);
        }
    }

    @Override
    public MessageResponse createRoom(CreateRoomRequest createRoomRequest) {
        try {
            InputPart myFile = createRoomRequest.getFile().getFormDataMap().get("file").get(0);

            Room room = new Room();
            room.setRoomName(createRoomRequest.getRoomName());
            room.setRoomCapacity(createRoomRequest.getRoomCapacity());
            room.setRoomStatus(RoomStatus.READY);

            if (roomRepository.findByRoomName(createRoomRequest.getRoomName()).isPresent()) {
                return MessageResponse.builder()
                        .message("Room name already exists")
                        .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                        .build();
            }

            if (myFile != null) {
                String folderPath = folderService.createFolder(createRoomRequest.getRoomName());

                if (folderPath != null) {
                    String fileName = myFile.getFileName();

                    String filePath = folderPath + File.separator + fileName;

                    String createdFilePath = fileService.createFileImage(myFile, filePath);

                    if (createdFilePath != null) {
                        room.setPhoto(createdFilePath);
                        roomRepository.persist(room);

                        RoomResponse mapper = RoomMapper.toRoomResponse(room);

                        return MessageResponse.builder()
                                .message("Room created successfully")
                                .data(mapper)
                                .statusCode(Response.Status.OK.getStatusCode())
                                .build();
                    } else {
                        System.err.println("Failed to create the file");
                        return MessageResponse.builder()
                                .message("Failed to create the file")
                                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                                .build();
                    }
                } else {
                    System.err.println("Failed to create the folder");
                    return MessageResponse.builder()
                            .message("Failed to create the folder")
                            .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                            .build();
                }
            }
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred", e);
            throw new WebApplicationException("Unexpected error occurred", e, Status.INTERNAL_SERVER_ERROR);
        }

        return MessageResponse.builder()
                .message("No file provided")
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .build();
    }

    @Override
    public MessageResponse updateRoom(Long id, UpdateRoomRequest request) {
        try {
            Room existingRoom = roomRepository.findById(id);

            String newRoomName = request.getRoomName();
            if (!existingRoom.getRoomName().equals(newRoomName)) {
                if (roomRepository.findByRoomName(newRoomName).isPresent()) {
                    return MessageResponse.builder()
                            .message("Room name already exists")
                            .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                            .build();
                }
            }

            existingRoom.setRoomName(newRoomName);
            existingRoom.setRoomCapacity(request.getRoomCapacity());

            Map<String, List<InputPart>> uploadForm = request.getFile().getFormDataMap();
            List<InputPart> inputParts = uploadForm.get("file");

            if (inputParts != null && !inputParts.isEmpty()) {
                InputPart inputPart = inputParts.get(0);
                String fileName = inputPart.getFileName();
                String folderPath = folderService.createFolder(newRoomName);

                if (folderPath != null) {
                    String filePath = folderPath + File.separator + fileName;
                    String createFilePath = fileService.createFileImage(inputPart, filePath);

                    existingRoom.setPhoto(createFilePath);
                } else {
                    throw new WebApplicationException("Failed to create the folder", Status.INTERNAL_SERVER_ERROR);
                }
            }

            roomRepository.persist(existingRoom);

            RoomResponse roomResponse = RoomMapper.toRoomResponse(existingRoom);
            return MessageResponse.builder()
                    .message("Room updated successfully")
                    .data(roomResponse)
                    .statusCode(Response.Status.OK.getStatusCode())
                    .build();
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred", e);
            throw new WebApplicationException("Unexpected error occurred", e, Status.INTERNAL_SERVER_ERROR);
        }
    }

    public MessageResponse deleteRoom(Long id) {
        Room room = this.roomRepository.findById(id);
        try {

            roomRepository.delete(room);

            return MessageResponse.builder()
                    .message("Success")
                    .statusCode(Response.Status.OK.getStatusCode())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return MessageResponse.builder()
                    .message("Error occurred while deleting room")
                    .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                    .build();
        }
    }

}
