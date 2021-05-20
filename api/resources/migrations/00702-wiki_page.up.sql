create table if not exists wiki_page
(
    text_id int8,
    display_order int,
    parent_id int8 ,
    wiki_id int8,
    name varchar(200),
    views int,
    publish_at timestamp default now()
);