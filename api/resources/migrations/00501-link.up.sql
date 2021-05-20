create table if not exists link
(
    id int8 primary key ,
    create_at timestamp default now(),
    update_at timestamp default now(),
    name varchar(200),
    url varchar(500)
);