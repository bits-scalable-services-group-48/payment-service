package com.ticketing.paymentservice.controller;

import com.ticketing.paymentservice.dto.PaymentChargeRequest;
import com.ticketing.paymentservice.dto.PaymentChargeResponse;
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

    /**
     * Process a payment charge.
     * Requires header: Idempotency-Key
     */
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
}
