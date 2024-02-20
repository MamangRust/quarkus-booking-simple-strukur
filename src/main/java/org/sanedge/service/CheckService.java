package org.sanedge.service;

import org.sanedge.domain.request.booking.CheckInRequest;
import org.sanedge.domain.response.MessageResponse;

public interface CheckService {
    MessageResponse checkInOrder(CheckInRequest request);

    MessageResponse checkOrder(String orderId);

    MessageResponse checkOutOrder(String orderId);
}
