create sequence if not exists compensation_details_seq start 1;

create table if not exists compensation_details
(
    compensation_details_id bigint default nextval('compensation_details_seq'::regclass) not null
        constraint pk_transaction_log_id
            primary key,
    transaction_log_id      bigint                                                       not null,
    compensation_reason     text                                                         not null,
    error_message           text,
    constraint fk_saga_transaction_log_id foreign key (transaction_log_id) references transaction_log
);