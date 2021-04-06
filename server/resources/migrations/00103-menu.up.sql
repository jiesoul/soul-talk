CREATE TABLE IF NOT EXISTS menu (
    id int primary key ,
    name varchar(50),
    url varchar(200),
    pid int default 0,
    note varchar(200),
    create_by int default 0,
    create_at timestamp default now(),
    update_by int default 0,
    update_at timestamp default now()
);

insert into menu (id, name, url, pid, note) VALUES (10, '统计面板', '/dash', 0, '');

insert into menu (id, name, url, pid, note) VALUES (11, '基础数据', '', 0, '');
insert into menu (id, name, url, pid, note) VALUES (1101, '网站信息', '/site-info/1', 11, '');
insert into menu (id, name, url, pid, note) VALUES (1102, '数据字典', '/data-dic', 11, '');
insert into menu (id, name, url, pid, note) VALUES (1103, '菜单管理', '/menu', 11, '');
insert into menu (id, name, url, pid, note) VALUES (1104, '角色管理', '/role', 11, '');

insert into menu (id, name, url, pid, note) VALUES (12, '用户授权', '', 0, '');
insert into menu (id, name, url, pid, note) VALUES (1201, '用户管理', '/user', 12, '');
insert into menu (id, name, url, pid, note) VALUES (1202, '用户授权管理', '/user/auth-key', 12, '');
insert into menu (id, name, url, pid, note) VALUES (1203, 'APP key管理', '/app-key', 12, '');

insert into menu (id, name, url, pid, note) VALUES (13, '数据管理', '', 0, '');
insert into menu (id, name, url, pid, note) VALUES (1301, '系列管理', '/series', 13, '');
insert into menu (id, name, url, pid, note) VALUES (1302, '标签管理', '/tag', 13, '');

insert into menu (id, name, url, pid, note) VALUES (14, '文章管理', '', 0, '');
insert into menu (id, name, url, pid, note) values (1401, '文章列表', '/article', 14, '');
insert into menu (id, name, url, pid, note) values (1402, '文章标签管理', '/article/tag', 14, '');

insert into menu (id, name, url, pid, note) VALUES (15, '收藏网站管理', '', 0, '');
insert into menu (id, name, url, pid, note) values (1501, '收藏网站列表', '/collect-site', 15, '');
insert into menu (id, name, url, pid, note) values (1502, '收藏网站标签管理', '/collect-site/tag', 15, '');

insert into menu (id, name, url, pid, note) VALUES (16, '收藏链接管理', '', 0, '');
insert into menu (id, name, url, pid, note) values (1601, '收藏链接列表', '/collect-link', 16, '');
insert into menu (id, name, url, pid, note) values (1602, '收藏链接标签管理', '/collect-link/tag', 16, '');

