INSERT INTO exchange.currencies (id, code, name, unit) VALUES (1, 'USD', 'US Dollar', 1);
INSERT INTO exchange.currencies (id, code, name, unit) VALUES (2, 'EUR', 'Euro', 1);
INSERT INTO exchange.currencies (id, code, name, unit) VALUES (3, 'CHF', 'Swiss Franc', 1);
INSERT INTO exchange.currencies (id, code, name, unit) VALUES (4, 'RUB', 'Russian ruble', 100);
INSERT INTO exchange.currencies (id, code, name, unit) VALUES (5, 'CZK', 'Czech koruna', 100);
INSERT INTO exchange.currencies (id, code, name, unit) VALUES (6, 'GBP', 'Pound sterling', 1);
INSERT INTO exchange.users (id, active, created_at, first_name, last_name, password, user_name) VALUES (1, false, '2020-02-24 20:05:17.903107000', 'Exchange', 'Owner', '$2a$10$E0w5a8z1UNCz4KAqW7W3M.odEDQkxXYI.gaL27RtdGyYUA8FKT5M.', 'exchange');
INSERT INTO exchange.users (id, active, created_at, first_name, last_name, password, user_name) VALUES (2, true, '2020-02-24 20:05:17.903107000', 'Krzysztof', 'Mucha', '$2a$10$E0w5a8z1UNCz4KAqW7W3M.odEDQkxXYI.gaL27RtdGyYUA8FKT5M.', 'kmucha');
INSERT INTO exchange.transactions (id, amount, created_at, currency_rate, currency_id, user_id) VALUES (1, 50000.00, '2020-02-25 16:03:40', 3.6907, 1, 1);
INSERT INTO exchange.transactions (id, amount, created_at, currency_rate, currency_id, user_id) VALUES (2, 50000.00, '2020-02-25 16:03:40', 3.9596, 2, 1);
INSERT INTO exchange.transactions (id, amount, created_at, currency_rate, currency_id, user_id) VALUES (3, 50000.00, '2020-02-25 16:03:40', 3.8238, 3, 1);
INSERT INTO exchange.transactions (id, amount, created_at, currency_rate, currency_id, user_id) VALUES (4, 5000000.00, '2020-02-25 16:03:40', 7.0380, 4, 1);
INSERT INTO exchange.transactions (id, amount, created_at, currency_rate, currency_id, user_id) VALUES (5, 5000000.00, '2020-02-25 16:03:40', 14.3227, 5, 1);
INSERT INTO exchange.transactions (id, amount, created_at, currency_rate, currency_id, user_id) VALUES (6, 50000.00, '2020-02-25 16:03:40', 5.7251, 6, 1);
INSERT INTO exchange.transactions (id, amount, created_at, currency_rate, currency_id, user_id) VALUES (1, 50000.00, '2020-02-25 16:03:40', 3.6907, 1, 2);
INSERT INTO exchange.transactions (id, amount, created_at, currency_rate, currency_id, user_id) VALUES (2, 50000.00, '2020-02-25 16:03:40', 3.9596, 2, 2);
INSERT INTO exchange.transactions (id, amount, created_at, currency_rate, currency_id, user_id) VALUES (3, 50000.00, '2020-02-25 16:03:40', 3.8238, 3, 2);
INSERT INTO exchange.transactions (id, amount, created_at, currency_rate, currency_id, user_id) VALUES (4, 5000000.00, '2020-02-25 16:03:40', 7.0380, 4, 2);
INSERT INTO exchange.transactions (id, amount, created_at, currency_rate, currency_id, user_id) VALUES (5, 5000000.00, '2020-02-25 16:03:40', 14.3227, 5, 2);
INSERT INTO exchange.transactions (id, amount, created_at, currency_rate, currency_id, user_id) VALUES (6, 50000.00, '2020-02-25 16:03:40', 5.7251, 6, 2);