CREATE TABLE IF NOT EXISTS comments (
    id serial primary key,
    articleId varchar (20) not null ,
    body text default '',
    userId int not null ,
    createAt timestamp,
    updateAt timestamp,
    reply_id int
                                    )