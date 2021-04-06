create table if not exists article_series (
    id serial primary key,
    article_id varchar(50),
    series_id int
)

