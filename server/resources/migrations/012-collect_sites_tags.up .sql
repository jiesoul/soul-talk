create table if not exists collect_site_tags (
    id serial primary key,
    collect_site_id varchar(50),
    tag_id int
)