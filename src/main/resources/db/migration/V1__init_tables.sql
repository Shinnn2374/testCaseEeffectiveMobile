CREATE TABLE IF NOT EXISTS _user (
    id BIGINT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS cards (
    id BIGINT PRIMARY KEY,
    card_number BYTEA NOT NULL,
    owner_name VARCHAR(255) NOT NULL,
    expiration_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    balance DECIMAL(15, 2) NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES _user(id)
);

CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT PRIMARY KEY,
    source_card_id BIGINT NOT NULL,
    target_card_id BIGINT NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    description VARCHAR(255),
    FOREIGN KEY (source_card_id) REFERENCES cards(id),
    FOREIGN KEY (target_card_id) REFERENCES cards(id)
);

CREATE INDEX idx_cards_user_id ON cards(user_id);
CREATE INDEX idx_transactions_source_card_id ON transactions(source_card_id);
CREATE INDEX idx_transactions_target_card_id ON transactions(target_card_id);