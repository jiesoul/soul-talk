create table if not exists article (
    id varchar(20) primary key,
    image varchar(100) ,
    title varchar(200) not null,
    description text,
    body text not null ,
    create_at timestamp,
    update_at timestamp,
    publish int default 0,
    create_by int,
    counter int default 0
)