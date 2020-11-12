create table if not exists articles (
    id varchar(20) primary key,
    image varchar(200) ,
    title varchar(200) not null,
    description text,
    body text not null ,
    publish int default 0,
    pv int default 0,
    create_by int default 0,
    create_at timestamp default now(),
    update_by int default 0,
    update_at timestamp default now()
)