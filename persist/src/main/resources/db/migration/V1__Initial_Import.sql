CREATE TABLE IF NOT EXISTS users
(
    id              BIGSERIAL   NOT NULL PRIMARY KEY,
    created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP,
    email           VARCHAR(36) NOT NULL,
    email_verified  BOOLEAN     NOT NULL DEFAULT FALSE,
    first_name      VARCHAR(36) NOT NULL,
    last_name       VARCHAR(36) NOT NULL,
    privilege_level VARCHAR(16) NOT NULL,
    country         VARCHAR(32) NOT NULL,
    password_hash   BYTEA       NOT NULL
);

CREATE TABLE IF NOT EXISTS schools
(
    id         BIGSERIAL   NOT NULL PRIMARY KEY,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    name       VARCHAR(36) NOT NULL,
    address    VARCHAR(64) NOT NULL,
    hidden     BOOLEAN              DEFAULT FALSE,
    country    VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS school_contacts
(
    id         BIGSERIAL   NOT NULL PRIMARY KEY,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    school_id  BIGINT      NOT NULL,
    name       VARCHAR(36) NOT NULL,
    email      VARCHAR(36),
    address    VARCHAR(36),
    phone      VARCHAR(36),

    CONSTRAINT fk_school_contact_school FOREIGN KEY (school_id) REFERENCES schools (id)
);

CREATE TABLE IF NOT EXISTS school_reports
(
    id                                BIGSERIAL NOT NULL PRIMARY KEY,
    created_at                        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at                        TIMESTAMP,
    user_id                           BIGINT    NOT NULL,
    school_id                         BIGINT    NOT NULL,
    time_period                       TIMESTAMP NOT NULL,
    number_of_children                INT       NOT NULL,
    contact_info_school               VARCHAR(128),
    contact_info_principal            VARCHAR(128),
    contact_info_literacy_coordinator VARCHAR(128),
    number_of_books                   INT       NOT NULL,
    most_recent_shipment_year         INT       NOT NULL,

    CONSTRAINT fk_school_report_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_school_report_school FOREIGN KEY (school_id) REFERENCES schools (id)
);

CREATE TABLE IF NOT EXISTS school_reports_with_libraries
(
    id                           BIGSERIAL   NOT NULL PRIMARY KEY,
    created_at                   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at                   TIMESTAMP,
    user_id                      BIGINT      NOT NULL,
    school_id                    BIGINT      NOT NULL,
    participates_oecs            VARCHAR(36) NOT NULL,
    has_classroom_check_ins      VARCHAR(36) NOT NULL,
    student_librarian_count      INT,
    has_sufficient_training      VARCHAR(36) NOT NULL,
    are_teachers_seeking_support BOOLEAN     NOT NULL,
    teacher_support_description  VARCHAR(128),
    has_involved_parents         BOOLEAN     NOT NULL,
    involved_parents_description VARCHAR(128),

    CONSTRAINT fk_school_report_lib_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_school_report_lib_school FOREIGN KEY (school_id) REFERENCES schools (id)
);

CREATE TABLE IF NOT EXISTS school_reports_without_libraries
(
    id             BIGSERIAL    NOT NULL PRIMARY KEY,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at     TIMESTAMP,
    user_id        BIGINT       NOT NULL,
    school_id      BIGINT       NOT NULL,
    why_not        VARCHAR(128) NOT NULL,
    wants_library  BOOLEAN      NOT NULL,
    has_space      VARCHAR(36)  NOT NULL,
    process_status VARCHAR(64)  NOT NULL,
    when_ready     VARCHAR(36)  NOT NULL,

    CONSTRAINT fk_school_report_wo_lib_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_school_report_wo_lib_school FOREIGN KEY (school_id) REFERENCES schools (id)
);

CREATE TABLE IF NOT EXISTS blacklisted_refreshes
(
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    refresh_hash VARCHAR(64) PRIMARY KEY,
    expires      TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS verification_keys
(
    id         VARCHAR(50) NOT NULL PRIMARY KEY,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id    BIGINT      NOT NULL,
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

DROP TRIGGER IF EXISTS users_trig_set_updated_at ON users;
CREATE TRIGGER users_trig_set_updated_at
    BEFORE UPDATE
    ON users
    FOR EACH ROW
EXECUTE PROCEDURE
    func_set_updated_at_timestamp();

DROP TRIGGER IF EXISTS schools_trig_set_updated_at ON schools;
CREATE TRIGGER schools_trig_set_updated_at
    BEFORE UPDATE
    ON schools
    FOR EACH ROW
EXECUTE PROCEDURE
    func_set_updated_at_timestamp();

DROP TRIGGER IF EXISTS schools_add_cts_trig_set_updated_at ON school_additional_contacts;
CREATE TRIGGER schools_add_cts_trig_set_updated_at
    BEFORE UPDATE
    ON school_additional_contacts
    FOR EACH ROW
EXECUTE PROCEDURE
    func_set_updated_at_timestamp();

DROP TRIGGER IF EXISTS school_reports_trig_set_updated_at ON school_reports;
CREATE TRIGGER school_reports_trig_set_updated_at
    BEFORE UPDATE
    ON school_reports
    FOR EACH ROW
EXECUTE PROCEDURE
    func_set_updated_at_timestamp();

DROP TRIGGER IF EXISTS school_reps_w_lib_trig_set_updated_at ON school_reports_with_libraries;
CREATE TRIGGER school_reps_w_lib_trig_set_updated_at
    BEFORE UPDATE
    ON school_reports_with_libraries
    FOR EACH ROW
EXECUTE PROCEDURE
    func_set_updated_at_timestamp();

DROP TRIGGER IF EXISTS school_reps_wo_lib_trig_set_updated_at ON school_reports_without_libraries;
CREATE TRIGGER school_reps_wo_lib_trig_set_updated_at
    BEFORE UPDATE
    ON school_reports_without_libraries
    FOR EACH ROW
EXECUTE PROCEDURE
    func_set_updated_at_timestamp();

DROP TRIGGER IF EXISTS bl_refs_trig_set_updated_at ON blacklisted_refreshes;
CREATE TRIGGER bl_refs_trig_set_updated_at
    BEFORE UPDATE
    ON blacklisted_refreshes
    FOR EACH ROW
EXECUTE PROCEDURE
    func_set_updated_at_timestamp();
/* [jooq ignore stop] */
