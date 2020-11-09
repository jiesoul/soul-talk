create table if not exists article_tags (
    id serial primary key,
    article_id varchar(50),
    tag_id int
)