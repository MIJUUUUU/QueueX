DELETE FROM order_item;
DELETE FROM waiting;
DELETE FROM menu;
DELETE FROM store;
DELETE FROM customer;
DELETE FROM admin;

ALTER TABLE order_item AUTO_INCREMENT = 1;
ALTER TABLE waiting AUTO_INCREMENT = 1;
ALTER TABLE menu AUTO_INCREMENT = 1;
ALTER TABLE store AUTO_INCREMENT = 1;
ALTER TABLE customer AUTO_INCREMENT = 1;
ALTER TABLE admin AUTO_INCREMENT = 1;

INSERT INTO admin (admin_auth_code)
VALUES
('ADMIN001'),
('ADMIN002'),
('ADMIN003');

INSERT INTO store (admin_id, store_name, category)
VALUES
(1, '미주옥', '한식'),
(1, '로마호연', '양식'),
(1, '송이자카야', '일식'),
(2, '빵만드는 집', '카페'),
(2, '맛있는 카페', '카페');


INSERT INTO customer (phone, password)
VALUES
('010-1111-1111', '1234'),
('010-2222-2222', '1234'),
('010-3333-3333', '1234'),
('010-4444-4444', '1234'),
('010-5555-5555', '1234'),
('010-6666-6666', '1234'),
('010-7777-7777', '1234'),
('010-8888-8888', '1234'),
('010-9999-9999', '1234'),
('010-1234-5678', '1234'),
('010-2345-6789', '1234'),
('010-3456-7890', '1234'),
('010-4567-8901', '1234'),
('010-5678-9012', '1234'),
('010-6789-0123', '1234'),
('010-7890-1234', '1234'),
('010-8901-2345', '1234'),
('010-9012-3456', '1234'),
('010-1122-3344', '1234'),
('010-2233-4455', '1234'),
('010-3344-5566', '1234'),
('010-4455-6677', '1234'),
('010-5566-7788', '1234');

INSERT INTO menu (store_id, menu_name, price, is_available)
VALUES
(1, '김치찌개', 9000, true),
(1, '된장찌개', 8500, true),
(1, '제육볶음', 10000, true),
(2, '파스타', 12000, true),
(2, '리조또', 13000, true),
(2, '스테이크', 25000, false),
(3, '김치나베', 8900, true),
(3, '우동', 9000, true),
(3, '야키토리', 8900, true),
(4, '소금빵', 4500, true),
(4, '아메리카노', 4000, true),
(4, '카페라떼', 5000, true),
(5, '치즈케이크', 6500, true),
(5, '바닐라라떼', 5500, true),
(5, '콜드브루', 4800, true);

INSERT INTO waiting (customer_id, store_id, waiting_number, people_count, status, called_at)
VALUES
(1, 1, 1, 2, 'WAITING', NULL),
(2, 1, 2, 4, 'WAITING', NULL),
(3, 1, 3, 1, 'CALLED', '2026-04-07 11:30:00'),
(4, 2, 1, 2, 'ENTERED', '2026-04-07 11:10:00'),
(5, 3, 1, 4, 'WAITING', NULL),
(6, 1, 4, 3, 'WAITING', NULL),
(7, 1, 5, 2, 'NOSHOW', '2026-04-07 11:40:00'),
(8, 2, 2, 5, 'WAITING', NULL),
(9, 2, 3, 1, 'CALLED', '2026-04-07 11:45:00'),
(10, 2, 4, 2, 'CANCELED', NULL),
(11, 3, 2, 6, 'WAITING', NULL),
(12, 3, 3, 2, 'ENTERED', '2026-04-07 11:20:00'),
(13, 1, 6, 4, 'WAITING', NULL),
(14, 1, 7, 1, 'WAITING', NULL),
(15, 2, 5, 3, 'WAITING', NULL),
(16, 2, 6, 2, 'CALLED', '2026-04-07 11:50:00'),
(17, 3, 4, 5, 'WAITING', NULL),
(18, 3, 5, 2, 'NOSHOW', '2026-04-07 11:55:00'),
(19, 1, 8, 2, 'WAITING', NULL),
(20, 2, 7, 4, 'WAITING', NULL);


INSERT INTO order_item (waiting_id, menu_id, quantity)
VALUES

(1, 1, 2),
(1, 2, 1),
(2, 3, 1),
(3, 1, 1),
(4, 4, 2),
(4, 5, 1),
(5, 7, 1),
(5, 8, 2),
(6, 2, 1),
(7, 3, 2),
(8, 5, 2),
(9, 4, 1),
(10, 6, 1),
(11, 9, 1),
(12, 7, 2),
(13, 2, 1),
(14, 1, 1),
(15, 6, 1),
(16, 5, 2),
(17, 8, 2),
(18, 7, 1),
(19, 1, 1),
(20, 4, 2);
