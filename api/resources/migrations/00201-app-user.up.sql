CREATE TABLE IF NOT EXISTS app_user
(id serial primary key,
 email VARCHAR(50) unique,
 name VARCHAR(50),
 password VARCHAR(200) NOT NULL,
 admin varchar(8) default '1202',
 last_login_at timestamp,
 is_valid varchar(20) default '1001',
 create_by int default 0,
 create_at timestamp default now(),
 update_by int default 0,
 update_at timestamp default now()
);

-- 插入初始用户 密码为 12345678
insert into app_user (name, email, admin, last_login_at, password, is_valid, create_by, update_by)
VALUES ('jiesoul', 'jiesoul@gmail.com', '1201', now(),
        'bcrypt+sha512$91735d27fa9797835267bb14e457ba5d$12$7d6efccba210c2be98c9b9cabe0e428344cc8f69e38867f1',
        '1001', 1, 1);