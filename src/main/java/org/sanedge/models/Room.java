package org.sanedge.models;

import org.sanedge.utils.RoomStatus;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "rooms")
public class Room extends PanacheEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @Column(name = "room_name", nullable = false)
    private String roomName;

    @NotNull
    @Column(name = "room_capacity", nullable = false)
    private Integer roomCapacity;

    @NotNull
    @Column(name = "photo", nullable = false)
    private String photo;

    @NotNull
    @Column(name = "room_status", nullable = false, columnDefinition = "varchar(255) default 'ready'")
    @Enumerated(EnumType.STRING)
    private RoomStatus roomStatus;

    // constructors, getters, setters
}