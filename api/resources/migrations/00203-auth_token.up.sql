CREATE TABLE auth_token (
    id serial primary key ,
    token varchar(256) unique ,
    user_id int,
    create_at timestamp not null default now(),
    refresh_at timestamp not null default now()
);
CREATE INDEX ON auth_token (token);