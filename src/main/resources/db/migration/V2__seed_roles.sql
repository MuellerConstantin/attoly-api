INSERT INTO roles (id, name, created_at, last_modified_at, deleted, version)
VALUES
    (gen_random_uuid(), 'ROLE_ADMIN', now(), now(), false, 0),
    (gen_random_uuid(), 'ROLE_MODERATOR', now(), now(), false, 0),
    (gen_random_uuid(), 'ROLE_USER', now(), now(), false, 0)
    ON CONFLICT (name) DO NOTHING;
