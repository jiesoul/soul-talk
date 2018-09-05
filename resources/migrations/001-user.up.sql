CREATE TABLE IF NOT EXISTS users
(id serial primary key,
name VARCHAR(30),
email VARCHAR(30) unique ,
admin BOOLEAN,
last_login TIME,
is_active BOOLEAN,
password VARCHAR(200) NOT NULL);