package org.sanedge.repository;

import java.util.Optional;

import org.sanedge.models.Room;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RoomRepository implements PanacheRepositoryBase<Room, Long> {

    public Optional<Room> findByRoomName(String roomName) {
        return find("roomName", roomName).firstResultOptional();
    }
}