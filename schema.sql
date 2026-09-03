-- Optional: run this manually if you prefer to create the schema/table yourself
-- instead of relying on spring.jpa.hibernate.ddl-auto=update

CREATE DATABASE IF NOT EXISTS departmentdb;

USE departmentdb;

CREATE TABLE IF NOT EXISTS department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Sample data (optional)
INSERT INTO department (name) VALUES ('Human Resources');
INSERT INTO department (name) VALUES ('Finance');
