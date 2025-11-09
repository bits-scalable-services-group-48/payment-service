-- Payment Service Schema

CREATE TABLE IF NOT EXISTS etsr_payments (
    payment_id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'INR',
    method VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    idempotency_key VARCHAR(255) UNIQUE,
    external_ref VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_payment_order ON etsr_payments(order_id);
CREATE INDEX IF NOT EXISTS idx_payment_idempotency ON etsr_payments(idempotency_key);
CREATE INDEX IF NOT EXISTS idx_payment_status ON etsr_payments(status);
