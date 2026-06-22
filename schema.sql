-- 1. Create the database
CREATE DATABASE IF NOT EXISTS portfolio_db;

-- 2. Use the database
USE portfolio_db;

-- 3. Create the contact_messages table
CREATE TABLE IF NOT EXISTS contact_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Create the meeting_bookings table
CREATE TABLE IF NOT EXISTS meeting_bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    topic VARCHAR(255) NOT NULL,
    preferred_date VARCHAR(50),
    preferred_time VARCHAR(50),
    status VARCHAR(20) DEFAULT 'PENDING',
    booked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
