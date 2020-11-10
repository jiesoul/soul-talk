CREATE TABLE app_keys (
    id serial primary key ,
    token varchar(256) unique ,
    app_name varchar(30) not null ,
    create_by int default 0,
    create_at timestamp not null default now(),
    refresh_at timestamp not null default now()
);
CREATE INDEX ON app_keys (token);


insert into app_keys (token, app_name, create_by) values ('ttuy8JElV95vu8DbRF6d6GiUsiLseC3NAMQG1nvqtr8=', 'web' ,1);