
DELETE FROM USERS;

INSERT INTO USERS (username, email, password, full_name, user_status, user_type, phone_number, cpf, image_url)
VALUES
('John', 'john@example.com', '123456', 'John Doe', 'ACTIVE', 'ADMIN', '+1 555-555-5555', '12345678901', 'https://example.com/image1.jpg'),
('Jane', 'jane@example.com', '123456', 'Jane Smith', 'BLOCKED', 'STUDENT', '+1 666-666-6666', '23456789012', 'https://example.com/image2.jpg'),
('Alice', 'alice@example.com', '123456', 'Alice Johnson', 'ACTIVE', 'INSTRUCTOR', '+1 777-777-7777', '34567890123', 'https://example.com/image3.jpg');
