INSERT INTO user_management.users_authorities (user_id, authority_id)
SELECT (select id FROM user_management.users WHERE email = 'thomas.anderson@itcompany.com'),
       (SELECT id FROM user_management.authorities a where name = 'ADMIN')
where (SELECT count(*) FROM user_management.users_authorities a2) = 0;
