create table if not exists fired_saga
(
    saga_id     text                                   not null
        constraint pk_fired_saga_id
            primary key,
    saga_status varchar(128)                           not null,
    fired_time  timestamp with time zone default now() not null
);
