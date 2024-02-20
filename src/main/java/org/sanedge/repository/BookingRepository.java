package org.sanedge.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.sanedge.domain.response.booking.BookingDetailsResponse;
import org.sanedge.domain.response.booking.CheckOutResponse;
import org.sanedge.models.Booking;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BookingRepository implements PanacheRepositoryBase<Booking, Long> {

    public Optional<Booking> findByOrderId(String orderId) {
        return find("orderId", orderId).firstResultOptional();
    }

    public Optional<Booking> findBookingDetailsByOrderId(String orderId) {
        return find("SELECT new com.sanedge.bookinghotel.domain.response.booking.BookingDetailsResponse(" +
                "b.orderId, b.totalPerson, b.bookingTime, u.email, r.roomName, r.roomCapacity, r.photo, r.roomStatus) "
                +
                "FROM Booking b " +
                "JOIN b.user u " +
                "JOIN b.room r " +
                "WHERE b.orderId = ?1", orderId)
                .firstResultOptional();
    }

    public Optional<Booking> findCheckoutDetailsByOrderId(String orderId) {
        return find(
                "SELECT new com.sanedge.bookinghotel.domain.response.booking.CheckOutResponse(b.orderId, b.room.id, b.checkOutTime, u.email) "
                        +
                        "FROM Booking b " +
                        "JOIN b.user u " +
                        "WHERE b.orderId = ?1",
                orderId)
                .firstResultOptional();
    }

    public List<Booking> findBookingsByBookingTime(LocalDateTime dateNow) {
        return find("bookingTime", dateNow).list();
    }
}