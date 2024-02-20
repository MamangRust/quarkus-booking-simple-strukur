package org.sanedge.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.sanedge.domain.request.booking.CreateBookingRequest;
import org.sanedge.domain.request.booking.UpdateBookingRequest;
import org.sanedge.domain.response.MessageResponse;
import org.sanedge.models.Booking;
import org.sanedge.models.Room;
import org.sanedge.models.User;
import org.sanedge.repository.BookingRepository;
import org.sanedge.repository.RoomRepository;
import org.sanedge.repository.UserRepository;
import org.sanedge.service.BookingMailService;
import org.sanedge.service.BookingService;
import org.sanedge.utils.RoomStatus;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final BookingMailService bookingMailService;

    @Inject
    public BookingServiceImpl(UserRepository userRepository, RoomRepository roomRepository,
            BookingRepository bookingRepository, BookingMailService bookingMailService) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.bookingMailService = bookingMailService;
    }

    @Override
    public MessageResponse findAll() {
        try {
            log.info("Fetching all bookings");
            List<Booking> bookingList = this.bookingRepository.listAll();

            log.info("Found {} bookings", bookingList.size());

            return MessageResponse.builder()
                    .message("Booking data retrieved successfully")
                    .data(bookingList)
                    .statusCode(Response.Status.OK.getStatusCode())
                    .build();
        } catch (Exception e) {
            log.error("Error fetching all bookings", e);

            return MessageResponse.builder()
                    .message("Error fetching all bookings")
                    .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                    .build();
        }
    }

    @Override
    public MessageResponse findById(Long id) {
        try {
            log.info("Fetching booking by id: {}", id);

            Booking findBooking = this.bookingRepository.findById(id);

            log.info("Found booking: {}", findBooking);

            return MessageResponse.builder().message("Success").data(findBooking)
                    .statusCode(Response.Status.OK.getStatusCode())
                    .build();
        } catch (Exception e) {
            log.error("Error fetching booking by id: {}", id, e);

            return MessageResponse.builder()
                    .message("Error fetching booking")
                    .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                    .build();
        }
    }

    @Override
    public MessageResponse createBooking(Long userId, CreateBookingRequest request) {
        try {
            log.info("Creating new booking for user {} and room {}", userId, request.getRoomId());

            User findUser = this.userRepository.findById(userId);

            Room findRoom = this.roomRepository.findById(request.getRoomId());

            if (request.getTotalPerson() >= findRoom.getRoomCapacity()) {
                return MessageResponse.builder()
                        .message("Room capacity not enough")
                        .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                        .build();
            }

            if (findRoom.getRoomStatus() == RoomStatus.BOOKING) {
                return MessageResponse.builder()
                        .message("Room is booking")
                        .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                        .build();
            }

            Booking orderBooking = new Booking();

            orderBooking.setOrderId("ORDER" + System.currentTimeMillis());
            orderBooking.setUser(findUser);
            orderBooking.setRoom(findRoom);
            orderBooking.setTotalPerson(request.getTotalPerson());
            orderBooking.setBookingTime(request.getBookingTime());
            orderBooking.setNoted(request.getNoted());

            this.bookingRepository.persist(orderBooking);

            log.info("Booking created successfully: {}", orderBooking);

            findRoom.setRoomStatus(RoomStatus.BOOKING);

            this.roomRepository.persist(findRoom);

            return MessageResponse.builder().message("Success").data(orderBooking).statusCode(200).build();
        } catch (Exception e) {
            log.error("Error creating booking for user {} and room {}", userId, request.getRoomId(), e);

            return MessageResponse.builder()
                    .message("Error creating booking")
                    .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                    .build();
        }
    }

    @Override
    public MessageResponse updateBooking(Long id, Long userId, UpdateBookingRequest request) {
        try {
            log.info("Updating booking with id: {}", id);

            User findUser = this.userRepository.findById(userId);

            Room findRoom = this.roomRepository.findById(request.getRoomId());

            Booking findBooking = this.bookingRepository.findById(id);

            if (request.getTotalPerson() >= findRoom.getRoomCapacity()) {
                return MessageResponse.builder()
                        .message("Room capacity not enough")
                        .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                        .build();
            }

            if (findRoom.getRoomStatus() == RoomStatus.BOOKING) {
                return MessageResponse.builder()
                        .message("Room is booking")
                        .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                        .build();
            }

            findBooking.setUser(findUser);
            findBooking.setRoom(findRoom);
            findBooking.setTotalPerson(request.getTotalPerson());
            findBooking.setBookingTime(request.getBookingTime());
            findBooking.setNoted(request.getNoted());

            this.bookingRepository.persist(findBooking);

            log.info("Booking updated successfully: {}", findBooking);

            return MessageResponse.builder().message("Booking updated successfully").data(findBooking)
                    .statusCode(Response.Status.OK.getStatusCode()).build();
        } catch (Exception e) {
            log.error("Error updating booking with id: {}", id, e);

            return MessageResponse.builder().message("Error updating booking")
                    .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void bookingTimeCronJob() {
        try {
            LocalDateTime dateNow = LocalDateTime.now();

            List<Booking> bookings = bookingRepository.findBookingsByBookingTime(dateNow);

            for (Booking booking : bookings) {
                bookingMailService.sendEmailBookingTime(booking.getOrderId(), booking.getUser().getEmail(),
                        booking.getCheckInTime());
                log.info("Booking time cron job sent email to: {}", booking.getUser().getEmail());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error booking time cron job", e);
        }
    }

    @Override
    public MessageResponse deleteById(Long id) {
        try {
            log.info("Deleting booking with id: {}", id);

            Booking findBooking = this.bookingRepository.findById(id);

            this.bookingRepository.delete(findBooking);

            log.info("Booking deleted successfully");

            return MessageResponse.builder().message("Success").statusCode(Response.Status.OK.getStatusCode()).build();
        } catch (Exception e) {
            log.error("Error deleting booking with id: {}", id, e);

            return MessageResponse.builder().message("Error deleting booking")
                    .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }
    }
}
