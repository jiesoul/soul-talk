CREATE TABLE IF NOT EXISTS favorite_rticles
(
    id serial primary key,
    article_id varchar(50),
    user_id int
);