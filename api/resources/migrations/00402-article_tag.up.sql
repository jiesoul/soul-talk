create table if not exists article_tag (
    id serial primary key,
    article_id varchar(50),
    tag_id int
)