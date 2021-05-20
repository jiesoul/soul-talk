CREATE TABLE IF NOT EXISTS role_menu (
    id serial primary key ,
    role_id int,
    menu_id int
);

INSERT INTO role_menu (role_id, menu_id) (SELECT r.id, m.id FROM role r, menu m);