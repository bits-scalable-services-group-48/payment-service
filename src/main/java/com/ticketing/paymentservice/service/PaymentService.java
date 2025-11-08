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

        // 1) Idempotency: if we already processed this key, return same result
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

        // 2) Create payment in PENDING state
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .method(request.getMethod())
                .status(PaymentStatus.PENDING)
                .idempotencyKey(idempotencyKey)
                .externalRef("PAY-" + UUID.randomUUID()) // mock gateway ref
                .createdAt(now)
                .updatedAt(now)
                .build();

        // --- Simulate success --- //
        // For now, simply mark as SUCCESS immediately.
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
}
