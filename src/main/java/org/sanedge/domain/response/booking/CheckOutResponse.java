package org.sanedge.domain.response.booking;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckOutResponse {
    private String orderId;
    private Long roomId;
    private LocalDateTime checkOutTime;
    private String userEmail;
}
