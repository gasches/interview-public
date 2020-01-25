-- PostgreSQL dialect
CREATE TABLE IF NOT EXISTS transfers (
    id            BIGSERIAL PRIMARY KEY,
    source_id     BIGINT         NOT NULL REFERENCES accounts,
    target_id     BIGINT         NOT NULL REFERENCES accounts,
    amount        DECIMAL(19, 3) NOT NULL,
    transfer_time TIMESTAMP      NOT NULL DEFAULT now()
);
