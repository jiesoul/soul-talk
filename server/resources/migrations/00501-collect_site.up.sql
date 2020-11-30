CREATE TABLE IF NOT EXISTS collect_site (
    id SERIAL primary key,
    title varchar(200) not null ,
    url varchar(200) not null unique,
    image varchar(200),
    description text,
    pv int,
    create_by int default 0,
    create_at timestamp default now(),
    update_by int default 0,
    update_at timestamp default now()
);