create table if not exists wiki_text
(
    id int8 primary key ,
    create_at timestamp without time zone default now(),
    update_at timestamp without time zone default now(),
    content text
);