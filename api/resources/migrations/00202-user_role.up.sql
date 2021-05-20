CREATE TABLE IF NOT EXISTS user_role (
    id serial primary key ,
    user_id int,
    role_id int
);

INSERT INTO user_role (user_id, role_id) (SELECT u.id, r.id from app_user u, role r);