create schema if not exists "user_management";

create table if not exists "user_management"."users"
(
    "id"              uuid                        not null default gen_random_uuid() primary key,
    "deleted"         boolean                     not null default false,
    "version"         bigint                      not null,
    "created_at"      timestamp without time zone null,
    "created_by"      varchar(255)                null,
    "updated_at"      timestamp without time zone null,
    "updated_by"      varchar(255)                null,
    "name"            varchar(500)                not null,
    "email"           varchar(500)                not null,
    "doc_number_type" varchar(4)                  not null,
    "doc_number"      varchar(14)                 not null,
    "password"        varchar(255)                not null
);

create index if not exists users_email_idx ON user_management.users using btree (email);
create index if not exists users_cpf_idx ON user_management.users using btree (doc_number);