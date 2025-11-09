package com.ticketing.paymentservice.config;

import com.ticketing.paymentservice.entity.Payment;
import com.ticketing.paymentservice.entity.PaymentStatus;
import com.ticketing.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final PaymentRepository paymentRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (paymentRepository.count() > 0) {
            log.info("Data already exists. Skipping seed data loading.");
            return;
        }

        log.info("Starting seed data loading for Payment Service...");
        loadPayments();
        log.info("Seed data loading completed successfully!");
    }

    private void loadPayments() throws Exception {
        log.info("Loading payments from CSV...");
        var resource = new ClassPathResource("seed-data/etsr_payments.csv");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line = reader.readLine(); // Skip header
            int count = 0;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                
                LocalDateTime createdAt = LocalDateTime.parse(fields[6], FORMATTER);

        // Don't set the primary key (payment_id) explicitly to avoid JPA treating it as a detached entity.
        // Let PostgreSQL BIGSERIAL generate the id to prevent StaleObjectStateException during seeding.
        Payment payment = Payment.builder()
            .orderId(Long.parseLong(fields[1]))
                        .amount(new BigDecimal(fields[2]))
                        .currency("INR")
                        .method(fields[3])
                        .status(PaymentStatus.valueOf(fields[4]))
                        .externalRef(fields[5])
                        .createdAt(createdAt)
                        .updatedAt(createdAt)
                        .build();

                paymentRepository.save(payment);
                count++;
            }

            log.info("Loaded {} payments", count);
        }
    }
}
