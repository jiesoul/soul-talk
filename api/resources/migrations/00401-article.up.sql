create table if not exists article (
    id varchar(32) primary key,
    category_id int default 0,
    image varchar(200) ,
    title varchar(200) not null,
    description text,
    body text not null ,
    publish varchar(8) default '1102',
    pv int default 0,
    is_valid varchar(8) default '1001',
    create_by int default 0,
    create_at timestamp default now(),
    update_by int default 0,
    update_at timestamp default now()
)