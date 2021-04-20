CREATE TABLE app_key (
    id serial primary key ,
    token varchar(256) unique ,
    app_name varchar(30) not null ,
    create_by int default 1,
    is_valid varchar(8) default '1001',
    create_at timestamp not null default now(),
    refresh_at timestamp not null default now()
);
CREATE INDEX ON app_key (token);

-- 测试
INSERT INTO app_key (token, app_name, create_by, create_at, refresh_at)
VALUES ('pmyzXOP27cbvyyqDuEWGM1WAy4Bw1UKK_qpYzfP63rk', 'test', 1, now(), now());

