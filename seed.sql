-- 관리자 데이터
INSERT INTO admin (admin_code)
VALUES
('ADMIN001'),
('ADMIN002');

-- 매장 데이터
INSERT INTO store (admin_id, store_name, category)
VALUES
(1, '00 한식점', '한식'),
(2, '00 양식점', '양식');

--고객 데이터 
INSERT INTO customer (phone, password)
VALUES
('010-1111-1111', '1111'),
('010-2222-2222', '2222'),
('010-3333-3333', '3333'),
('010-4444-4444', '4444');

-- 메뉴 데이터
INSERT INTO menu (store_id, menu_name, price, is_available)
VALUES
(1, '김치찌개', 9000, true),
(1, '된장찌개', 8500, true),
(1, '제육볶음', 10000, true),
(2, '파스타', 12000, true),
(2, '리조또', 13000, true),
(2, '스테이크', 25000, false);

