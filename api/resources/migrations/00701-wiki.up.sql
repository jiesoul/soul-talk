create table if not exists wiki
(
    id int8 primary key ,
    create_at timestamp default now(),
    update_at timestamp default now(),
    image_id int,
    user_id int,
    tag varchar(500),
    name varchar(200),
    views int,
    publish_at timestamp default now()
);