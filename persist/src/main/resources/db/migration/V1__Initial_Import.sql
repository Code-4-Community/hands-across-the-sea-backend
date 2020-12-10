CREATE TABLE IF NOT EXISTS users
(
    id              SERIAL      NOT NULL PRIMARY KEY,
    created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP,
    email           VARCHAR(36) NOT NULL,
    email_verified  BOOLEAN     NOT NULL DEFAULT FALSE,
    first_name      VARCHAR(36) NOT NULL,
    last_name       VARCHAR(36) NOT NULL,
    privilege_level VARCHAR(16) NOT NULL,
    password_hash   BYTEA       NOT NULL
);


CREATE TABLE IF NOT EXISTS blacklisted_refreshes
(
    refresh_hash VARCHAR(64) PRIMARY KEY,
    expires      TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS verification_keys
(
    id         VARCHAR(50) NOT NULL PRIMARY KEY,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    user_id    INT         NOT NULL,
    used       BOOLEAN              DEFAULT false,
    type       VARCHAR(16) NOT NULL,

    CONSTRAINT verification_keys_user_id_fk FOREIGN KEY (user_id) REFERENCES users (id)
);

/* [jooq ignore start] */
CREATE OR REPLACE FUNCTION func_set_updated_at_timestamp()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ language 'plpgsql';

DROP TRIGGER IF EXISTS bl_refs_trig_set_updated_at ON blacklisted_refreshes;
CREATE TRIGGER bl_refs_trig_set_updated_at
    BEFORE UPDATE
    ON blacklisted_refreshes
    FOR EACH ROW
EXECUTE PROCEDURE
    func_set_updated_at_timestamp();

DROP TRIGGER IF EXISTS ver_keys_trig_set_updated_at ON verification_keys;
CREATE TRIGGER ver_keys_trig_set_updated_at
    BEFORE UPDATE
    ON verification_keys
    FOR EACH ROW
EXECUTE PROCEDURE
    func_set_updated_at_timestamp();
/* [jooq ignore stop] */
