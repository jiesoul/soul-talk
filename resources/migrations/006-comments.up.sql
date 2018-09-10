CREATE TABLE IF NOT EXISTS comments
(id serial primary key,
  post_id int,
  content varchar(500) default '',
  create_time timestamp,
  reply_id int)