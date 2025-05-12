-- Add new user profile fields to users table
ALTER TABLE users
ADD COLUMN phone VARCHAR(15) NULL,
ADD COLUMN gender VARCHAR(10) NULL,
ADD COLUMN first_name VARCHAR(50) NULL,
ADD COLUMN last_name VARCHAR(50) NULL,
ADD COLUMN date_of_birth DATE NULL,
ADD COLUMN address VARCHAR(255) NULL;

-- Comment: These fields are optional, so they are allowed to be NULL