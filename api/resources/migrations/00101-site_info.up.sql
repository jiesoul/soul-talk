CREATE TABLE IF NOT EXISTS site_info (
    id serial primary key,
    name varchar(50) not null ,
    description varchar(200) ,
    tags varchar(2000),
    logo varchar(200),
    author varchar(50)
);

INSERT INTO site_info (name, description, tags, logo, author) VALUES ('JIESOUL的个从网站', '个人网站','clojrue, java, C, C++, Blender, UE4', '', 'jiesoul');