create table if not exists collect_site_series (
    id serial primary key,
    collect_site_id varchar(50),
    series_id int
)