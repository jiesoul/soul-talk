create table if not exists reply
(
    id varchar(32) primary key ,
    create_at timestamp default now(),
    update_at timestamp default now(),
    topic_id varchar(32),
    user_id int default 0,
    user_name varchar(200),
    user_image_url varchar(500),
    content text
);