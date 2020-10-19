CREATE TABLE IF NOT EXISTS users
(id serial primary key,
name VARCHAR(50),
email VARCHAR(50) unique,
admin BOOLEAN default FALSE ,
last_login_at timestamp,
is_active BOOLEAN default TRUE,
password VARCHAR(200) NOT NULL);

-- 插入初始用户
insert into users (name, email, admin, last_login_at, is_active, password)
VALUES ('jiesoul', 'jiesoul@gmail.com', '1', now(), '1',
        'bcrypt+sha512$195a963abf6cafb7f3c2c94bb9b8cb74$12$97de8fea05227411f019b47ba7cf59eefb678d0636628cb7');