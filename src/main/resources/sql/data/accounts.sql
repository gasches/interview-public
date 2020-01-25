-- PostgreSQL dialect
CREATE TABLE IF NOT EXISTS accounts (
    id         BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(200) NOT NULL,
    last_name  VARCHAR(200) NOT NULL,
    balance    DECIMAL(19, 3) DEFAULT 0
);
