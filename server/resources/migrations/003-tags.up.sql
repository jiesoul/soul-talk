CREATE TABLE IF NOT EXISTS tags
(id SERIAL primary key ,
 name text not null unique );