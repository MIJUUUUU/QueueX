# 고객 테이블
CREATE TABLE IF NOT EXISTS customer (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    phone VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

# 관리자 테이블
CREATE TABLE IF NOT EXISTS admin (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    admin_code VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

# 매장 테이블
CREATE TABLE IF NOT EXISTS store (
    store_id INT PRIMARY KEY AUTO_INCREMENT,
    admin_id INT NOT NULL,
    store_name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    FOREIGN KEY (admin_id) REFERENCES admin(admin_id)
);

# 메뉴 테이블
CREATE TABLE IF NOT EXISTS menu (
    menu_id INT PRIMARY KEY AUTO_INCREMENT,
    store_id INT NOT NULL,
    menu_name VARCHAR(100) NOT NULL,
    price INT NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT true,
    FOREIGN KEY (store_id) REFERENCES store(store_id)
);

# 대기 테이블
CREATE TABLE IF NOT EXISTS waiting (
    waiting_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    store_id INT NOT NULL,
    waiting_number INT NOT NULL,
    people_count INT NOT NULL,
    status ENUM('WAITING', 'CALLED', 'ENTERED', 'NOSHOW', 'CANCELED') NOT NULL DEFAULT 'WAITING',
    called_at DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    FOREIGN KEY (store_id) REFERENCES store(store_id)
);

# 선주문 테이블
CREATE TABLE IF NOT EXISTS order_item (
    order_item_id INT PRIMARY KEY AUTO_INCREMENT,
    waiting_id INT NOT NULL,
    menu_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    FOREIGN KEY (waiting_id) REFERENCES waiting(waiting_id),
    FOREIGN KEY (menu_id) REFERENCES menu(menu_id)
);

# 인덱스 추가 (성능 최적화)
CREATE INDEX idx_customer_phone ON customer(phone);
CREATE INDEX idx_waiting_customer_id ON waiting(customer_id);
CREATE INDEX idx_waiting_store_id ON waiting(store_id);
CREATE INDEX idx_waiting_status ON waiting(status);
CREATE INDEX idx_order_item_waiting_id ON order_item(waiting_id);
