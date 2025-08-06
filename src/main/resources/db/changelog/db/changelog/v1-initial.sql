-- Создание таблицы users
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('USER', 'ADMIN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Создание таблицы cards
CREATE TABLE cards (
    id BIGSERIAL PRIMARY KEY,
    number VARCHAR(16) NOT NULL UNIQUE,
    owner_name VARCHAR(100) NOT NULL,
    expiration_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'BLOCKED', 'EXPIRED')),
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    user_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Создание таблицы transactions
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    source_card_id BIGINT NOT NULL REFERENCES cards(id),
    target_card_id BIGINT NOT NULL REFERENCES cards(id),
    amount DECIMAL(15, 2) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);