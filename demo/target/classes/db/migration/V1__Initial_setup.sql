CREATE TABLE forex_rate (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_currency VARCHAR(3),
    target_currency VARCHAR(3),
    exchange_rate DOUBLE,
    date DATE
);
