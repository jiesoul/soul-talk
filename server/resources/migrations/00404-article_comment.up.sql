CREATE TABLE IF NOT EXISTS article_comment (
    id serial primary key,
    article_id varchar (20) not null ,
    body text default '',
    reply_id int,
    create_by_name varchar(200),
    create_by_email varchar(200),
    create_at timestamp default now()
);