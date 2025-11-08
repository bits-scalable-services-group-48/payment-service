package com.ticketing.paymentservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRefundRequest {

    /**
     * Optional reason for refund, e.g. "EVENT_CANCELLED", "ORDER_CANCELLED"
     */
    private String reason;
}
