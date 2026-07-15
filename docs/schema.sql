CREATE DATABASE IF NOT EXISTS society_maintenance;
USE society_maintenance;

CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  phone VARCHAR(255),
  flat_number VARCHAR(255),
  profile_photo_url VARCHAR(255),
  role ENUM('ADMIN','RESIDENT') NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  password_reset_token VARCHAR(255) UNIQUE,
  password_reset_expires_at TIMESTAMP NULL
);

CREATE TABLE complaints (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  resident_id BIGINT NOT NULL,
  category VARCHAR(255) NOT NULL,
  description TEXT NOT NULL,
  photo_url VARCHAR(500),
  assigned_technician VARCHAR(255),
  priority ENUM('LOW','MEDIUM','HIGH') NOT NULL DEFAULT 'MEDIUM',
  status ENUM('OPEN','IN_PROGRESS','RESOLVED') NOT NULL DEFAULT 'OPEN',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  closed_at TIMESTAMP NULL,
  is_overdue BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_complaints_resident FOREIGN KEY (resident_id) REFERENCES users(id)
);

CREATE TABLE complaint_history (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  complaint_id BIGINT NOT NULL,
  status ENUM('OPEN','IN_PROGRESS','RESOLVED') NOT NULL,
  note TEXT,
  updated_by_id BIGINT NOT NULL,
  timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_history_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(id),
  CONSTRAINT fk_history_user FOREIGN KEY (updated_by_id) REFERENCES users(id)
);

CREATE TABLE notices (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  description TEXT NOT NULL,
  important BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by_id BIGINT NOT NULL,
  CONSTRAINT fk_notices_user FOREIGN KEY (created_by_id) REFERENCES users(id)
);

CREATE TABLE notifications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  message TEXT NOT NULL,
  is_read BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id)
);
