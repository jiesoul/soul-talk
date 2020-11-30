CREATE TABLE IF NOT EXISTS site_info (
    id serial primary key,
    name varchar(50) not null ,
    description varchar(200) ,
    logo varchar(200),
    author varchar(50)
);

INSERT INTO site_info (name, description, logo, author) VALUES ('不过如此', '个人网站', '', 'jiesoul');