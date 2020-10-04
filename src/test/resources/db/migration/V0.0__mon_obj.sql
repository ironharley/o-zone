create table mon.obj
(
    id_obj        numeric(18) not null
        constraint obj_pk
            primary key,
    geom          geometry,
    s_name        text        not null,
    s_comment     text,
    id_account    numeric(18) not null,
    geom_buffer_m numeric(18),
    measure       numeric(18, 2),
    color_border  numeric(18),
    color_fill    numeric(18),
    alpha_border  numeric(18),
    alpha_fill    numeric(18),
    is_active     integer,
    dt_change     timestamp,
    id_obj_ext    numeric(18)
);


create index obj_id_account
    on mon.obj (id_account);