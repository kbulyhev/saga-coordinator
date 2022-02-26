create sequence if not exists transaction_log_seq start 1;

create table if not exists transaction_log
(
    transaction_log_id  bigint                   default nextval('transaction_log_seq'::regclass) not null
        constraint pk_saga_transaction
            primary key,
    transaction_id      text,
    saga_id             text                                                                      not null,
    name                varchar(128)                                                              not null,
    state               varchar(50)                                                               not null,
    status              varchar(50)                                                               not null,
    timestamp           timestamp with time zone default now()                                    not null,
    constraint fk_transaction_id foreign key (transaction_id) references saga_transaction,
    constraint saga_id_name_uk unique (saga_id, name)
);

CREATE INDEX idx_transaction_status_saga_id
    ON transaction_log (saga_id asc, status asc);