-- Пароль: 'admin123' (зашифрован BCrypt)
INSERT INTO users (email, password, name, role) VALUES
                                                    ('admin@bank.com', '$2a$12$Q6BpZ2W9JcZ5J9Z8JcZ5J.9Z8JcZ5J9Z8JcZ5J9Z8JcZ5J9Z8JcZ5J', 'Admin', 'ADMIN'),
                                                    ('user@bank.com', '$2a$12$Q6BpZ2W9JcZ5J9Z8JcZ5J.9Z8JcZ5J9Z8JcZ5J9Z8JcZ5J9Z8JcZ5J', 'User', 'USER');

-- Карты для тестов
INSERT INTO cards (number, owner_name, expiration_date, status, balance, user_id) VALUES
                                                                                      ('4111111111111111', 'ADMIN USER', '2026-12-31', 'ACTIVE', 10000.00, 1),
                                                                                      ('5555555555554444', 'TEST USER', '2025-10-31', 'ACTIVE', 5000.00, 2);