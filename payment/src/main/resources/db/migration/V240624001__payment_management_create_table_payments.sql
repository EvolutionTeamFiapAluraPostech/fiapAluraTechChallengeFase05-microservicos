create schema if not exists "payment_management";

create table if not exists "payment_management"."payments"
(
    "id"                   uuid                        not null default gen_random_uuid() primary key,
    "deleted"              boolean                     not null default false,
    "version"              bigint                      not null,
    "created_at"           timestamp without time zone null,
    "created_by"           varchar(255)                null,
    "updated_at"           timestamp without time zone null,
    "updated_by"           varchar(255)                null,
    "active"               boolean                     not null,
    "order_id"             uuid                        not null,
    "company_id"           uuid                        not null,
    "company_name"         varchar(500)                not null,
    "customer_id"          uuid                        not null,
    "customer_name"        varchar(500)                not null,
    "payment_type"         varchar(50)                 not null,
    "payment_status"       varchar(50)                 not null,
    "payment_date"         timestamp                   not null,
    "payment_total_amount" numeric(16, 2)              not null
);