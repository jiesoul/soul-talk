create table if not exists collect_link_tag (
    id serial primary key,
    collect_link_id varchar(50),
    tag_id int
)