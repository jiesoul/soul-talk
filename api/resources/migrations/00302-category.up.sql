CREATE TABLE IF NOT EXISTS category
(
    id SERIAL primary key ,
    create_by int default 0,
    create_at timestamp default now(),
    update_by int default 0,
    update_at timestamp default now(),
    display_order int default 0,
    name varchar(50) not null unique,
    description varchar(200) default ''
);

insert into category (display_order, name, description) VALUES (1, '编程', '编程相关');
insert into category (display_order, name, description) VALUES (2, '读书', '读书');
insert into category (display_order, name, description) VALUES (3, '游戏', 'UE4');
insert into category (display_order, name, description) VALUES (4, '设计', 'Blender');
