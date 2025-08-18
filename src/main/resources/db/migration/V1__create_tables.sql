CREATE TABLE merchants (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    api_key VARCHAR(255) UNIQUE NOT NULL,
    created_at DATETIME NOT NULL
);

CREATE TABLE payment_links (
    id VARCHAR(36) PRIMARY KEY,
    merchant_id VARCHAR(36) NOT NULL,
    reference VARCHAR(50) UNIQUE NOT NULL,
    amount_cents BIGINT NOT NULL CHECK (amount_cents > 0),
    currency CHAR(3) NOT NULL,
    description TEXT,
    status ENUM('CREATED', 'PAID', 'CANCELLED', 'EXPIRED') NOT NULL DEFAULT 'CREATED',
    expires_at DATETIME NOT NULL,
    paid_at DATETIME,
    metadata JSON,
    FOREIGN KEY (merchant_id) REFERENCES merchants(id),
    INDEX idx_merchant_status (merchant_id, status)
);

CREATE TABLE payment_attempts (
    id VARCHAR(36) PRIMARY KEY,
    payment_link_id VARCHAR(36) NOT NULL,
    status ENUM('SUCCESS', 'FAILED') NOT NULL,
    reason TEXT,
    created_at DATETIME NOT NULL,
    idempotency_key VARCHAR(255) UNIQUE,
    FOREIGN KEY (payment_link_id) REFERENCES payment_links(id)
);