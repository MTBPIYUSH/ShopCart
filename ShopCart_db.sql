-- Create the database
CREATE DATABASE IF NOT EXISTS shopcartdb;
USE shopcartdb;

-- Create users table
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);

-- Create products table
CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0
);

-- Create cart table
CREATE TABLE cart (
    cart_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    UNIQUE KEY unique_user_product (user_id, product_id)
);

-- Create orders table
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Create order_details table
CREATE TABLE order_details (
    order_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price_at_time DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- Insert sample electronic gadgets
INSERT INTO products (name, description, price, stock) VALUES
('Smartphone X', 'Latest flagship smartphone with 5G capability', 9999.00, 50),
('Laptop Pro', '15-inch laptop with high-performance specs', 29999.00, 30),
('Wireless Earbuds', 'True wireless earbuds with noise cancellation', 599.00, 100),
('Smart Watch', 'Fitness tracking and notifications', 1999.00, 75),
('Tablet Ultra', '10-inch tablet with retina display', 7999.00, 40),
('Gaming Console', 'Next-gen gaming console with 4K support', 15999.00, 25),
('Bluetooth Speaker', 'Portable speaker with 20-hour battery life', 999.00, 60),
('Action Camera', 'Waterproof 4K action camera', 8999.00, 45),
('Wireless Mouse', 'Ergonomic wireless mouse with long battery life', 799.00, 150),
('Power Bank', '20000mAh portable charger with fast charging', 1299.00, 200);

ALTER TABLE users ADD budget DOUBLE DEFAULT 0.0;

CREATE TABLE feedback (
    feedback_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    email VARCHAR(255),
    feedback TEXT,
    feedback_date DATETIME,
    FOREIGN KEY (user_id)
        REFERENCES users (user_id)
);

CREATE TABLE saved_cards (
    card_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    card_number VARCHAR(16),
    card_holder VARCHAR(100),
    expiry_date VARCHAR(5),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

SELECT * FROM users;
select * from order_details;
SELECT * FROM saved_cards;
SELECT * FROM feedback;