CREATE TABLE IF NOT EXISTS data_dic (
    id varchar(16) primary key ,
    name varchar(50) not null,
    pid varchar(16) ,
    note varchar(200),
    create_by int default 0,
    create_at timestamp default now(),
    update_by int default 0,
    update_at timestamp default now()
);

insert into data_dics (id, name, pid, note) values ('10', '不过如此', '0', '');
insert into data_dics (id, name, pid, note) values ('11', '网站关键词', '0', '');

insert into data_dics (id, name, pid, note) values ('12', '系列类型', '0', '');
insert into data_dics (id, name, pid, note) values ('1201', '文章', '12', '');
insert into data_dics (id, name, pid, note) values ('1202', '网站', '12', '');
insert into data_dics (id, name, pid, note) values ('1203', '链接', '12', '');

