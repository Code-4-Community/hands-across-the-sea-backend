ALTER TABLE users
    DROP COLUMN disabled;

ALTER TABLE users
    ADD COLUMN disabled BOOLEAN NOT NULL DEFAULT FALSE;