package org.sanedge.service;

import org.sanedge.domain.request.booking.CreateBookingRequest;
import org.sanedge.domain.request.booking.UpdateBookingRequest;
import org.sanedge.domain.response.MessageResponse;

public interface BookingService {
    MessageResponse findAll();

    MessageResponse findById(Long id);

    MessageResponse createBooking(Long userId, CreateBookingRequest request);

    MessageResponse updateBooking(Long id, Long userId, UpdateBookingRequest request);

    MessageResponse deleteById(Long id);

}
