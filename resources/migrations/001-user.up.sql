CREATE TABLE IF NOT EXISTS users
(id serial primary key,
name VARCHAR(30),
email VARCHAR(30) unique ,
admin BOOLEAN default FALSE ,
last_login timestamp,
is_active BOOLEAN default TRUE ,
password VARCHAR(200) NOT NULL);