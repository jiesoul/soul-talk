CREATE TABLE IF NOT EXISTS role
(
    id   serial primary key,
    name varchar(20) unique,
    note varchar(200) default ''
);

INSERT INTO role (name, note) VALUES ('admin', '超级管理员');