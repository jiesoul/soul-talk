CREATE TABLE IF NOT EXISTS tag
(id SERIAL primary key ,
 create_by int default 0,
 create_at timestamp default now(),
 update_by int default 0,
 update_at timestamp default now(),
 name varchar(50) not null unique,
 description varchar(200)
);

insert into tag (name) values ('Clojure');
insert into tag (name) values ('Java');
insert into tag (name) values ('Postgresql');
insert into tag (name) values ('Docker');
insert into tag (name) values ('React');
insert into tag (name) values ('UE4');
insert into tag (name) values ('Blender');
insert into tag (name) values ('游戏');


