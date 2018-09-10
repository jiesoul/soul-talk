CREATE TABLE IF NOT EXISTS posts (
  id varchar(20) primary key,
  title varchar(200) not null,
  content text not null ,
  create_time timestamp,
  modity_time timestamp,
  category int,
  publish int,
  author varchar(50),
  counter int
);