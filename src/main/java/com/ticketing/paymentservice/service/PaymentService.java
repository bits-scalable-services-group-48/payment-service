package com.ticketing.paymentservice.service;

import com.ticketing.paymentservice.dto.PaymentChargeRequest;
import com.ticketing.paymentservice.dto.PaymentChargeResponse;
import com.ticketing.paymentservice.entity.Payment;
import com.ticketing.paymentservice.entity.PaymentStatus;
import com.ticketing.paymentservice.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentChargeResponse charge(String idempotencyKey, PaymentChargeRequest request) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("Idempotency-Key header is required");
        }

        Optional<Payment> existingOpt = paymentRepository.findByIdempotencyKey(idempotencyKey);
        if (existingOpt.isPresent()) {
            Payment existing = existingOpt.get();
            return PaymentChargeResponse.builder()
                    .paymentId(existing.getId())
                    .status(existing.getStatus().name())
                    .reference(existing.getExternalRef())
                    .build();
        }

        LocalDateTime now = LocalDateTime.now();

        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .method(request.getMethod())
                .status(PaymentStatus.PENDING)
                .idempotencyKey(idempotencyKey)
                .externalRef("PAY-" + UUID.randomUUID())
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Simulate success
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setUpdatedAt(LocalDateTime.now());

        payment = paymentRepository.save(payment);

        return PaymentChargeResponse.builder()
                .paymentId(payment.getId())
                .status(payment.getStatus().name())
                .reference(payment.getExternalRef())
                .build();
    }

    public Payment getPayment(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Payment not found: " + id));
    }

    /**
     * Mark payment as REFUNDED.
     * To be called when event/order is cancelled.
     */
    public Payment refundPayment(Long id, String reason) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Payment not found: " + id));

        // Basic business rule: only SUCCESS payments can be refunded
        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            // already refunded - idempotent
            return payment;
        }
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException(
                    "Only SUCCESS payments can be refunded. Current status = " + payment.getStatus()
            );
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setUpdatedAt(LocalDateTime.now());
        // you could also log/store the reason in a separate column if you want

        return paymentRepository.save(payment);
    }
}
