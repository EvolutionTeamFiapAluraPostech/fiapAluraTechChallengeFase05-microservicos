create schema if not exists "order_management";

create table if not exists "order_management"."order_items"
(
    "id"                  uuid                        not null default gen_random_uuid() primary key,
    "deleted"             boolean                     not null default false,
    "version"             bigint                      not null,
    "created_at"          timestamp without time zone null,
    "created_by"          varchar(255)                null,
    "updated_at"          timestamp without time zone null,
    "updated_by"          varchar(255)                null,
    "order_id"            uuid                        not null,
    "product_id"          uuid                        not null,
    "product_sku"         varchar(50)                 not null,
    "product_description" varchar(500)                not null,
    "quantity"            numeric(16, 2)              not null,
    "price"               numeric(16, 2)              not null,
    "total_amount"        numeric(16, 2)              not null
);