
create table if not exists error_saga
(
    saga_id             text                                   not null
        constraint pk_error_saga_id
            primary key,
    fired_time          timestamp with time zone default now() not null,
    error_time          timestamp with time zone default now() not null,
    attempts_to_recover int                                    not null default 0
);

CREATE INDEX idx_transaction_status_saga_id
    ON transaction_log (status asc, saga_id asc);