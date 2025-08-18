INSERT INTO merchants (id, name, email, api_key, created_at)
VALUES (UUID(), 'Demo Merchant', 'demo@merchant.test', 'test_merchant_key', NOW());

SET @merchant_id = (SELECT id FROM merchants WHERE email = 'demo@merchant.test');

INSERT INTO payment_links (id, merchant_id, reference, amount_cents, currency, description, expires_at, status)
VALUES (UUID(), @merchant_id, 'PL-2025-000001', 100000, 'COP', 'Link expirado', DATE_SUB(NOW(), INTERVAL 1 DAY), 'EXPIRED');

INSERT INTO payment_links (id, merchant_id, reference, amount_cents, currency, description, expires_at)
VALUES (UUID(), @merchant_id, 'PL-2025-000002', 250000, 'COP', 'Link activo', DATE_ADD(NOW(), INTERVAL 30 MINUTE));

INSERT INTO payment_links (id, merchant_id, reference, amount_cents, currency, description, expires_at)
VALUES (UUID(), @merchant_id, 'PL-2025-000003', 500000, 'COP', 'Otro link', DATE_ADD(NOW(), INTERVAL 60 MINUTE));