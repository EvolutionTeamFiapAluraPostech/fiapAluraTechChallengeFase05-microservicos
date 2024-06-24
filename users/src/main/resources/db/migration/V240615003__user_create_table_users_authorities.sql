create table if not exists "user_management"."users_authorities"
(
    "user_id"         uuid                not null,
    "authority_id"    uuid                not null
)