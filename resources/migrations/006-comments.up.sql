CREATE TABLE IF NOT EXISTS comments
(id serial primary key,
  post_id varchar (20) not null ,
  content varchar(500) default '',
  email varchar(50) not null ,
  name varchar (50) not null ,
  create_time timestamp,
  reply_id int)