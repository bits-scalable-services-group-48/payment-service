package com.ticketing.paymentservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentChargeResponse {

    private Long paymentId;
    private String status;       // PENDING, SUCCESS, FAILED, REFUNDED
    private String reference;    // external_ref or mock id
}
