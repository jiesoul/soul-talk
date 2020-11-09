create table if not exists article_serials (
    id serial primary key,
    article_id varchar(50),
    serials_id int
)