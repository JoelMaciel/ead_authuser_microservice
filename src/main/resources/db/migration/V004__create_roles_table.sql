CREATE TABLE ROLES (
    role_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    role_name VARCHAR(30) UNIQUE NOT NULL
);