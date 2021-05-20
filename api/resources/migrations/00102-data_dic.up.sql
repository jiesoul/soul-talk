CREATE TABLE IF NOT EXISTS data_dic (
    id varchar(16) primary key ,
    name varchar(50) not null,
    pid varchar(16) ,
    publish int default 0,
    note varchar(200),
    create_by int default 0,
    create_at timestamp default now(),
    update_by int default 0,
    update_at timestamp default now()
);

insert into data_dic (id, name, pid, note) values ('10', '有效状态', '0', '');
insert into data_dic (id, name, pid, note) values ('1001', '是', '10', '');
insert into data_dic (id, name, pid, note) values ('1002', '否', '10', '');

insert into data_dic (id, name, pid, note) values ('11', '发布状态', '0', '');
insert into data_dic (id, name, pid, note) values ('1101', '是', '11', '');
insert into data_dic (id, name, pid, note) values ('1102', '否', '11', '');

insert into data_dic (id, name, pid, note) values ('12', '管理员', '0', '');
insert into data_dic (id, name, pid, note) values ('1201', '是', '12', '');
insert into data_dic (id, name, pid, note) values ('1202', '否', '12', '');

insert into data_dic (id, name, pid, note, publish) values ('13', '文章分类', '0', '', 1);
insert into data_dic (id, name, pid, note, publish) values ('1301', '编程', '13', '', 1);
insert into data_dic (id, name, pid, note, publish) values ('1302', '读书', '13', '', 1);