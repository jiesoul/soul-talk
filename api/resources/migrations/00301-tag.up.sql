CREATE TABLE IF NOT EXISTS tag
(id SERIAL primary key ,
 name text not null unique,
 create_by int default 0,
 create_at timestamp default now(),
 update_by int default 0,
 update_at timestamp default now()
);