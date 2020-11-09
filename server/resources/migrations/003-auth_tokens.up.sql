CREATE TABLE auth_tokens (
    token varchar(256) primary key ,
    user_id int,
    create_at timestamp not null default now(),
    refresh_at timestamp not null default now()
);
CREATE INDEX ON auth_tokens (token);


-- insert into auth_tokens (id, user_id) values ('ttuy8JElV95vu8DbRF6d6GiUsiLseC3NAMQG1nvqtr8=', 1);