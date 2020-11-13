CREATE TABLE app_keys (
    id serial primary key ,
    token varchar(256) unique ,
    app_name varchar(30) not null ,
    create_by int default 0,
    create_at timestamp not null default now(),
    refresh_at timestamp not null default now()
);
CREATE INDEX ON app_keys (token);

