create table if not exists collect_link_series (
    id serial primary key,
    collect_link_id varchar(50),
    series_id int
)