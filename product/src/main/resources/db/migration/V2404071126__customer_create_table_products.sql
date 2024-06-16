create schema if not exists "product_management";

create table if not exists "product_management"."products"
(
    "id"               uuid                        not null default gen_random_uuid() primary key,
    "deleted"          boolean                     not null default false,
    "version"          bigint                      not null,
    "created_at"       timestamp without time zone null,
    "created_by"       varchar(255)                null,
    "updated_at"       timestamp without time zone null,
    "updated_by"       varchar(255)                null,
    "active"           boolean                     not null,
    "sku"              varchar(50)                 not null,
    "description"      varchar(500)                not null,
    "unit_measurement" varchar(20)                 null,
    "quantity_stock"   numeric(16, 2)              null,
    "price"            numeric(16, 2)              null,
    "image_url"        varchar(2500)               null
);