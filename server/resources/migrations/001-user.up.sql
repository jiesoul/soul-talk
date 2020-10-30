CREATE TABLE IF NOT EXISTS users
(id serial primary key,
name VARCHAR(50),
email VARCHAR(50) unique,
admin BOOLEAN default FALSE ,
last_login_at timestamp,
is_active BOOLEAN default TRUE,
password VARCHAR(200) NOT NULL);

-- 插入初始用户 密码为 12345678
insert into users (name, email, admin, last_login_at, is_active, password)
VALUES ('jiesoul', 'jiesoul@gmail.com', '1', now(), '1',
        'bcrypt+sha512$91735d27fa9797835267bb14e457ba5d$12$7d6efccba210c2be98c9b9cabe0e428344cc8f69e38867f1');