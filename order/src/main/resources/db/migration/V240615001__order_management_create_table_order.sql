create schema if not exists "order_management";

create table if not exists "order_management"."orders"
(
    "id"                       uuid                        not null default gen_random_uuid() primary key,
    "deleted"                  boolean                     not null default false,
    "version"                  bigint                      not null,
    "created_at"               timestamp without time zone null,
    "created_by"               varchar(255)                null,
    "updated_at"               timestamp without time zone null,
    "updated_by"               varchar(255)                null,
    "active"                   boolean                     not null,
    "company_id"               uuid                        not null,
    "customer_id"              uuid                        not null,
    "status"                   varchar(50)                 not null,
    "order_date"               timestamp                   not null,
    "order_total_amount"       numeric(16, 2)              not null
);