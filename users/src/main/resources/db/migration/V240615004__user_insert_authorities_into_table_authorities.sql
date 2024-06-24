INSERT INTO "user_management"."authorities" (version, created_at, name)
SELECT 0,
       current_timestamp,
       'USER'
WHERE (SELECT count(*) FROM "user_management"."authorities" u2 WHERE name = 'USER') = 0;

INSERT INTO "user_management"."authorities" (version, created_at, name)
SELECT 0,
       current_timestamp,
       'ADMIN'
WHERE (SELECT count(*) FROM "user_management"."authorities" u2 WHERE name = 'ADMIN') = 0;