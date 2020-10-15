CREATE TABLE IF NOT EXISTS comments (
    id serial primary key,
    article_id varchar (20) not null ,
    body text default '',
    create_by int not null ,
    create_at timestamp,
    update_at timestamp,
    reply_id int
                                    )