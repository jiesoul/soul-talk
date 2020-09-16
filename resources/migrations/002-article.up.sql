create table if not exists article (
    id varchar(20) primary key,
    image varchar(100) ,
    title varchar(200) not null,
    description text not null,
    body text not null ,
    createAt timestamp,
    modifyAt timestamp,
    publish int default 0,
    userId int,
    counter int default 0
)