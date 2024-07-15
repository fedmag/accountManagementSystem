--DROP Table if exists users;
--DROP Table if exists salaries;
--DROP Table if exists permission_groups;
--DROP Table if exists user_groups;
--
--CREATE TABLE IF not exists users (
--   id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
--   email VARCHAR(255),
--   password VARCHAR(255),
--   name VARCHAR(255),
--   lastname VARCHAR(255)
--);
--
--CREATE TABLE IF not exists salaries (
--   employee_email VARCHAR(255),
--   period DATE,
--   salary BIGINT
--);
--
--DROP TABLE if EXISTS permission_groups;
--CREATE TABLE IF not exists permission_groups (
--   id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
--   code VARCHAR(255) UNIQUE,
--   name VARCHAR(255)
--);
--INSERT INTO permission_groups (code, name) VALUES('user',  'User Group');
--INSERT INTO permission_groups (code, name) VALUES('admin',  'Admin Group');
--INSERT INTO permission_groups (code, name) VALUES('accountant',  'Accountant Group');