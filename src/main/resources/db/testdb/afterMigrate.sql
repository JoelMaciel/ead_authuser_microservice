
DELETE FROM USERS;

INSERT INTO USERS (user_id, username, email, password, full_name, user_status, user_type, phone_number, cpf, image_url)
VALUES
('5a96aa84-1f15-4333-ba60-54d99a3faccb', 'John', 'john@example.com', '123456', 'John Doe', 'ACTIVE', 'ADMIN', '+1 555-555-5555', '12345678901', 'https://example.com/image1.jpg'),
('8c6b464b-03f4-4a2d-ac59-19584ee09d4f', 'Jane', 'jane@example.com', '123456', 'Jane Smith', 'BLOCKED', 'STUDENT', '+1 666-666-6666', '23456789012', 'https://example.com/image2.jpg'),
('79ff1531-1d28-44fc-b20a-ccb4cbbe71d7', 'Alice', 'alice@example.com', '123456', 'Alice Johnson', 'ACTIVE', 'INSTRUCTOR', '+1 777-777-7777', '34567890123', 'https://example.com/image3.jpg');
