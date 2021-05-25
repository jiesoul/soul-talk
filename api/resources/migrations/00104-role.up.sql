CREATE TABLE IF NOT EXISTS role
(
    id   serial primary key,
    create_at timestamp default now(),
    create_by int default 0,
    update_at timestamp default now(),
    update_by int default 0,
    name varchar(20) unique,
    note varchar(200) default ''
);

INSERT INTO role (name, note) VALUES ('admin', '超级管理员');