create table if not exists collect_sites_serials (
    id serial primary key,
    collect_site_id varchar(50),
    serials_id int
)