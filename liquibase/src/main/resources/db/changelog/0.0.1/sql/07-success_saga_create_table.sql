create table if not exists success_saga
(
    saga_id             text                                   not null
        constraint pk_success_saga_id
            primary key,
    fired_time          timestamp with time zone default now() not null,
    finish_time         timestamp with time zone default now() not null,
    attempts_to_recover int                                    not null default 0
);