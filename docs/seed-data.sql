USE society_maintenance;

-- BCrypt password for all seeded users: Password@123
INSERT INTO users (name, email, password, phone, flat_number, role)
VALUES
('Admin User', 'admin@society.test', '$2a$10$yL0rQ8SDIhVwx5/da5WdzOZkXpfQgt4wVKnhMTxggMV5nzPEhrpsa', '9000000000', 'A-000', 'ADMIN'),
('Riya Sharma', 'riya@society.test', '$2a$10$yL0rQ8SDIhVwx5/da5WdzOZkXpfQgt4wVKnhMTxggMV5nzPEhrpsa', '9000000001', 'A-302', 'RESIDENT'),
('Arjun Mehta', 'arjun@society.test', '$2a$10$yL0rQ8SDIhVwx5/da5WdzOZkXpfQgt4wVKnhMTxggMV5nzPEhrpsa', '9000000002', 'B-1104', 'RESIDENT');

INSERT INTO complaints (resident_id, category, description, priority, status, is_overdue)
VALUES
(2, 'Plumbing', 'Kitchen tap is leaking continuously.', 'HIGH', 'OPEN', FALSE),
(3, 'Electrical', 'Corridor light outside flat is flickering.', 'MEDIUM', 'IN_PROGRESS', FALSE),
(2, 'Housekeeping', 'Garbage chute needs cleaning.', 'LOW', 'RESOLVED', FALSE);

INSERT INTO complaint_history (complaint_id, status, note, updated_by_id)
VALUES
(1, 'OPEN', 'Complaint created', 2),
(2, 'OPEN', 'Complaint created', 3),
(2, 'IN_PROGRESS', 'Assigned to electrician', 1),
(3, 'OPEN', 'Complaint created', 2),
(3, 'RESOLVED', 'Housekeeping team completed cleaning', 1);

INSERT INTO notices (title, description, important, created_by_id)
VALUES
('Water Supply Maintenance', 'Water supply will be paused from 10 AM to 12 PM on Sunday.', TRUE, 1),
('Parking Sticker Renewal', 'Residents are requested to renew parking stickers by month end.', FALSE, 1);
