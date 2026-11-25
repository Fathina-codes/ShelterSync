CREATE DATABASE sheltersync;
USE sheltersync;
CREATE TABLE shelter (
 shelter_id INT AUTO_INCREMENT PRIMARY KEY,
 name VARCHAR(100) NOT NULL,
 location VARCHAR(150) NOT NULL,
 capacity INT NOT NULL,
 occupancy INT DEFAULT 0
);
CREATE TABLE affected_person (
 person_id INT AUTO_INCREMENT PRIMARY KEY,
 name VARCHAR(100) NOT NULL,
 age INT,
 gender VARCHAR(10),
 contact VARCHAR(15),
 shelter_id INT NOT NULL,
 FOREIGN KEY (shelter_id) REFERENCES shelter(shelter_id)
);
CREATE TABLE resource (
 resource_id INT AUTO_INCREMENT PRIMARY KEY,
 name VARCHAR(100) NOT NULL,
 category VARCHAR(20) NOT NULL, -- Food / Water / Medicine / Clothing
 quantity INT NOT NULL DEFAULT 0
);
CREATE TABLE emergency_request (
 request_id INT AUTO_INCREMENT PRIMARY KEY,
 shelter_id INT NOT NULL,
 resource_id INT NOT NULL,
 quantity_needed INT NOT NULL,
 urgency VARCHAR(10) NOT NULL, -- CRITICAL / HIGH / MEDIUM / LOW
 status VARCHAR(15) DEFAULT 'PENDING',
 FOREIGN KEY (shelter_id) REFERENCES shelter(shelter_id),
 FOREIGN KEY (resource_id) REFERENCES resource(resource_id)
);
CREATE TABLE distribution (
 dist_id INT AUTO_INCREMENT PRIMARY KEY,
 shelter_id INT NOT NULL,
 resource_id INT NOT NULL,
 quantity INT NOT NULL,
 dist_date DATETIME DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY (shelter_id) REFERENCES shelter(shelter_id),
 FOREIGN KEY (resource_id) REFERENCES resource(resource_id)
);
-- View: available beds per shelter
CREATE VIEW shelter_availability AS
SELECT shelter_id, name, capacity, occupancy,
 (capacity - occupancy) AS available
FROM shelter;
-- Trigger: reduce stock after a distribution
DELIMITER //
CREATE TRIGGER trg_reduce_stock AFTER INSERT ON distribution
FOR EACH ROW
BEGIN
 UPDATE resource SET quantity = quantity - NEW.quantity
 WHERE resource_id = NEW.resource_id;
END//
DELIMITER ;

SELECT * FROM affected_person;
SELECT * FROM shelter;
SELECT * FROM resource ;
SELECT * FROM emergency_request;
SELECT * FROM distribution ;

drop table affected_person;
drop table shelter;
drop table resource ;
drop table emergency_request;
drop table distribution ;
