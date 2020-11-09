CREATE TABLE IF NOT EXISTS collect_link_comments (
    id serial primary key,
    collect_link_id int ,
    body text default '',
    reply_id int,
    create_by_name varchar(200),
    create_by_email varchar(200),
    create_at timestamp default now()
);