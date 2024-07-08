INSERT INTO user_management.users (id, deleted, "version", created_at, updated_at, name, email,
                                   doc_number_type, doc_number, "password", created_by, updated_by)
SELECT '6b42b26f-ffd8-4dc3-9caf-8629add26a92'::uuid, false,
       0,
       current_timestamp,
       null,
       'Thomas Anderson',
       'thomas.anderson@itcompany.com',
       'CPF',
       '95962710088',
       '$2a$12$AP2oQl/3BD518uHwNq/ekOh4gNE1MOHK1yxwOG11vnqrcCb.cdelq',
       null,
       NULL
WHERE (SELECT count(*) FROM user_management.users u2) = 0;
