create table if not exists saga_transaction
(
    transaction_id        text         not null,
    saga_id               text         not null,
    service_name          varchar(128) not null,
    transaction_index     varchar(128) not null,
    transaction_status    varchar(128) not null,
    timestamp             timestamptz  not null default now(),
    request_payload       text,
    compensation_payload  text,
    request_url           text,
    compensation_url      text,
    compensated           boolean      not null default false,
    parent_transaction_id text,

    constraint pk_saga_transaction_id primary key (transaction_id),
    constraint fk_parent_transaction_id foreign key (parent_transaction_id) references saga_transaction,
    constraint fk_saga_id foreign key (saga_id) references success_saga
);
