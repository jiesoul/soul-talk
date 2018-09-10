CREATE TABLE IF NOT EXISTS posts_tags
(id SERIAL primary key ,
  post_id int,
  tag_id int);