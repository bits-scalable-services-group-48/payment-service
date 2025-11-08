package com.ticketing.paymentservice.controller;

import com.ticketing.paymentservice.dto.PaymentChargeRequest;
import com.ticketing.paymentservice.dto.PaymentChargeResponse;
import com.ticketing.paymentservice.dto.PaymentRefundRequest;
import com.ticketing.paymentservice.entity.Payment;
import com.ticketing.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/charge")
    public PaymentChargeResponse charge(
            @RequestHeader(name = "Idempotency-Key", required = true) String idempotencyKey,
            @RequestBody @Valid PaymentChargeRequest request
    ) {
        return paymentService.charge(idempotencyKey, request);
    }

    @GetMapping("/{id}")
    public Payment getPayment(@PathVariable Long id) {
        return paymentService.getPayment(id);
    }

    /**
     * Refund a payment.
     * This can be called by Order Service when an order is cancelled,
     * or by Catalog/Event Service when an event is cancelled.
     */
    @PostMapping("/{id}/refund")
    public Payment refundPayment(
            @PathVariable Long id,
            @RequestBody(required = false) PaymentRefundRequest request
    ) {
        String reason = (request != null ? request.getReason() : null);
        return paymentService.refundPayment(id, reason);
    }
}
