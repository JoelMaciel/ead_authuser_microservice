
DELETE FROM USERS;
DELETE FROM USERS_ROLES;

INSERT INTO USERS (user_id, username, email, password, full_name, user_status, user_type, phone_number, cpf, image_url)
VALUES
('5a96aa84-1f15-4333-ba60-54d99a3faccb', 'John', 'john@example.com', '$2a$10$VEDbIbnejab8uGmcCg0ECe9a6hEJwWBdu3xeo8OsHeopjgnsgw3py', 'John Doe', 'ACTIVE', 'ADMIN', '+1 555-555-5555', '12345678901', 'https://example.com/image1.jpg'),
('8c6b464b-03f4-4a2d-ac59-19584ee09d4f', 'Jane', 'jane@example.com', '$2a$10$VEDbIbnejab8uGmcCg0ECe9a6hEJwWBdu3xeo8OsHeopjgnsgw3py', 'Jane Smith', 'BLOCKED', 'STUDENT', '+1 666-666-6666', '23456789012', 'https://example.com/image2.jpg'),
('79ff1531-1d28-44fc-b20a-ccb4cbbe71d7', 'Alice', 'alice@example.com', '$2a$10$VEDbIbnejab8uGmcCg0ECe9a6hEJwWBdu3xeo8OsHeopjgnsgw3py', 'Alice Johnson', 'ACTIVE', 'INSTRUCTOR', '+1 777-777-7777', '34567890123', 'https://example.com/image3.jpg');


INSERT INTO users_roles (user_id, role_id) VALUES
('5a96aa84-1f15-4333-ba60-54d99a3faccb', '24924ef3-15ca-44e7-b615-d199b8506f65'),
('8c6b464b-03f4-4a2d-ac59-19584ee09d4f', 'd831507e-37db-48f3-aab7-1a53cca2053f'),
('79ff1531-1d28-44fc-b20a-ccb4cbbe71d7', '91d720a0-a7db-4736-87a7-e1cd4ddabc2f');
