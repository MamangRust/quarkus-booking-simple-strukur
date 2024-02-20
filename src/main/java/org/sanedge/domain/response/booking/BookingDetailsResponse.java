package org.sanedge.domain.response.booking;

import java.time.LocalDateTime;

import org.sanedge.utils.RoomStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDetailsResponse {
    private String orderId;
    private Integer totalPerson;
    private LocalDateTime bookingTime;
    private String userEmail;
    private String roomName;
    private String roomCapacity;
    private String roomPhoto;
    private RoomStatus roomStatus;

}