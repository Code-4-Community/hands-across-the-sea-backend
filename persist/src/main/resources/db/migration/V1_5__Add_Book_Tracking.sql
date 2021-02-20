CREATE TABLE IF NOT EXISTS book_logs
(
    id         SERIAL    NOT NULL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    school_id  INT       NOT NULL,
    count      INT       NOT NULL,
    date       TIMESTAMP,
    notes      VARCHAR(128),

    CONSTRAINT fk_book_flows_school FOREIGN KEY (school_id) REFERENCES schools (id)
);

/* [jooq ignore start] */
DROP TRIGGER IF EXISTS book_flows_updated_at ON book_logs;
CREATE TRIGGER book_flows_updated_at
    BEFORE UPDATE
    ON book_logs
    FOR EACH ROW
EXECUTE PROCEDURE
    func_set_updated_at_timestamp();
/* [jooq ignore stop] */