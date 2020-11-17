CREATE TABLE app_keys (
    id serial primary key ,
    token varchar(256) unique ,
    app_name varchar(30) not null ,
    create_by int default 0,
    valid int default 1,
    create_at timestamp not null default now(),
    refresh_at timestamp not null default now()
);
CREATE INDEX ON app_keys (token);

INSERT INTO app_keys (token, app_name, create_by, create_at, refresh_at)
VALUES ('ty57zxCEOc6KQeEcAz6PZcb3FneD2p7ANrsm0rmZID4=', 'test', now(), 1, now());

