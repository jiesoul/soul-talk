CREATE TABLE auth_tokens (
    id varchar(64) primary key ,
    user_id integer REFERENCES users(id) on delete cascade ,
    create_at TIMESTAMP not null default now()
);
CREATE INDEX ON auth_tokens (id, create_at DESC );


-- insert into auth_tokens (id, user_id) values ('ttuy8JElV95vu8DbRF6d6GiUsiLseC3NAMQG1nvqtr8=', 1);