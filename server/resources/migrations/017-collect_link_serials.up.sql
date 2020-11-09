create table if not exists collect_link_serials (
    id serial primary key,
    collect_link_id varchar(50),
    serials_id int
)