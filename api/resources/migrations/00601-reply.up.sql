create table if not exists reply
(
    id serial primary key ,
    create_at timestamp default now(),
    update_at timestamp default now(),
    topic_id varchar(200),
    user_id int,
    user_name varchar(200),
    user_image_url varchar(500),
    content text
);