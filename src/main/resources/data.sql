INSERT INTO currencies (id, code, name, unit, billing_currency)
VALUES (1, 'USD', 'US Dollar', 1, false);
INSERT INTO currencies (id, code, name, unit, billing_currency)
VALUES (2, 'EUR', 'Euro', 1, false);
INSERT INTO currencies (id, code, name, unit, billing_currency)
VALUES (3, 'CHF', 'Swiss Franc', 1, false);
INSERT INTO currencies (id, code, name, unit, billing_currency)
VALUES (4, 'RUB', 'Russian ruble', 100, false);
INSERT INTO currencies (id, code, name, unit, billing_currency)
VALUES (5, 'CZK', 'Czech koruna', 100, false);
INSERT INTO currencies (id, code, name, unit, billing_currency)
VALUES (6, 'GBP', 'Pound sterling', 1, false);
INSERT INTO currencies (id, code, name, unit, billing_currency)
VALUES (7, 'PLN', 'Polish zloty', 1, true);
INSERT INTO currency_rates (id, active, average_price, created_at, publication_date, purchase_price, sell_price,
                            currency_id)
VALUES (1, 1, 1, '2020-02-25 16:03:40', '2020-02-25 16:03:40', 1, 1, 7);
INSERT INTO roles (id, role)
VALUES (1, 'ROLE_USER');
INSERT INTO roles (id, role)
VALUES (2, 'ROLE_OWNER');
INSERT INTO users (id, active, created_at, first_name, last_name, password, user_name) VALUES (1, false, '2020-02-24 20:05:17.903107000', 'Exchange', 'Owner', '$2a$10$E0w5a8z1UNCz4KAqW7W3M.odEDQkxXYI.gaL27RtdGyYUA8FKT5M.', 'exchange');
INSERT INTO users_roles (user_id, role_id) VALUES (1, 2);
INSERT INTO transactions (id, amount, created_at, currency_rate, currency_id, user_id) VALUES (1, 5000.00, '2020-02-25 16:03:40', 3.6907, 1, 1);
INSERT INTO transactions (id, amount, created_at, currency_rate, currency_id, user_id) VALUES (2, 5000.00, '2020-02-25 16:03:40', 3.9596, 2, 1);
INSERT INTO transactions (id, amount, created_at, currency_rate, currency_id, user_id) VALUES (3, 5000.00, '2020-02-25 16:03:40', 3.8238, 3, 1);
INSERT INTO transactions (id, amount, created_at, currency_rate, currency_id, user_id) VALUES (4, 500000.00, '2020-02-25 16:03:40', 7.0380, 4, 1);
INSERT INTO transactions (id, amount, created_at, currency_rate, currency_id, user_id) VALUES (5, 500000.00, '2020-02-25 16:03:40', 14.3227, 5, 1);
INSERT INTO transactions (id, amount, created_at, currency_rate, currency_id, user_id) VALUES (6, 5000.00, '2020-02-25 16:03:40', 5.7251, 6, 1);
INSERT INTO transactions (id, amount, created_at, currency_rate, currency_id, user_id) VALUES (7, 50000.00, '2020-02-25 16:03:40', 5.7251, 7, 1);
