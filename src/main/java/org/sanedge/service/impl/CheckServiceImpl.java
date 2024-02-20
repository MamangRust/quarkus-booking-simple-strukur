package org.sanedge.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.sanedge.domain.request.booking.CheckInRequest;
import org.sanedge.domain.response.MessageResponse;
import org.sanedge.models.Booking;
import org.sanedge.models.Room;
import org.sanedge.repository.BookingRepository;
import org.sanedge.repository.RoomRepository;
import org.sanedge.service.BookingMailService;
import org.sanedge.service.CheckService;
import org.sanedge.utils.RoomStatus;

import com.google.inject.Inject;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class CheckServiceImpl implements CheckService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final BookingMailService bookingMailService;

    @Inject
    public CheckServiceImpl(BookingRepository bookingRepository, RoomRepository roomRepository,
            BookingMailService bookingMailService) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.bookingMailService = bookingMailService;
    }

    public MessageResponse checkInOrder(CheckInRequest request) {
        try {
            log.info("Checking in order with orderId: {}", request.getOrderId());

            Booking findOrder = this.bookingRepository.findByOrderId(request.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Not found order"));

            findOrder.setCheckInTime(request.getCheckInTime());

            this.bookingRepository.persist(findOrder);

            log.info("Order checked in successfully: {}", findOrder);

            bookingMailService.sendEmailCheckIn(findOrder.getOrderId(), findOrder.getUser().getEmail(),
                    findOrder.getCheckInTime().toString());

            return MessageResponse.builder().message("Success").statusCode(200).build();

        } catch (Exception e) {
            log.error("Error checking in order", e);
            return MessageResponse.builder().message("Error checking in order").statusCode(500).build();
        }
    }

    public MessageResponse checkOrder(String orderId) {
        try {
            log.info("Checking order details for orderId: {}", orderId);

            Optional<Booking> booking = this.bookingRepository.findBookingDetailsByOrderId(orderId);

            log.info("Found order details: {}", booking);

            return MessageResponse.builder().message("Your detail booking order information").data(booking)
                    .statusCode(200)
                    .build();

        } catch (Exception e) {
            log.error("Error checking order details", e);
            return MessageResponse.builder().message("Error checking order details").statusCode(500).build();
        }
    }

    public MessageResponse checkOutOrder(String orderId) {
        Booking checkIn = this.bookingRepository.findCheckoutDetailsByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Not found order"));

        log.info("Found check out details: {}", checkIn);

        if (checkIn.getCheckOutTime() != null) {
            return MessageResponse.builder().message("Already checked out").statusCode(400).build();
        }

        Booking booking = this.bookingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Not found order"));

        Room room = this.roomRepository.findById(checkIn.getRoom().getId());

        booking.setCheckOutTime(LocalDateTime.now());

        room.setRoomStatus(RoomStatus.READY);

        this.bookingRepository.persist(booking);
        this.roomRepository.persist(room);

        bookingMailService.sendEmailCheckOut(booking.getOrderId(), booking.getUser().getEmail(),
                booking.getCheckOutTime().toString());

        return MessageResponse.builder().message("Check out room successfuly").data(checkIn).statusCode(200).build();
    }
}
