CREATE TABLE IF NOT EXISTS serials(
    id SERIAL primary key ,
    name varchar(200) not null unique,
    description text default '',
    category_id varchar(16),
    create_by int default 0,
    create_at timestamp default now(),
    update_by int default 0,
    update_at timestamp default now()
);