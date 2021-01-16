ALTER TABLE schools
    ADD COLUMN area VARCHAR(64);

ALTER TABLE schools
    ADD COLUMN phone VARCHAR(36);

ALTER TABLE schools
    ADD COLUMN email VARCHAR(36);

ALTER TABLE schools
    ADD COLUMN notes VARCHAR(256);


ALTER TABLE school_reports
    DROP COLUMN contact_info_school;

ALTER TABLE school_reports
    DROP COLUMN contact_info_principal;

ALTER TABLE school_reports
    DROP COLUMN contact_info_literacy_coordinator;


ALTER TABLE school_contacts
    DROP COLUMN name;

ALTER TABLE school_contacts
    ADD COLUMN is_primary BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE school_contacts
    ADD COLUMN first_name VARCHAR(36);

ALTER TABLE school_contacts
    ADD COLUMN last_name VARCHAR(36);

ALTER TABLE school_contacts
    ADD COLUMN type VARCHAR(36);


ALTER TABLE school_contacts
    ALTER COLUMN type SET NOT NULL;

DROP TABLE IF EXISTS school_reports_with_libraries;
CREATE TABLE IF NOT EXISTS school_reports_with_libraries
(
    id                             BIGSERIAL   NOT NULL PRIMARY KEY,
    created_at                     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at                     TIMESTAMP,
    school_report_id               INT         NOT NULL,
    is_shared_space                BOOLEAN     NOT NULl,
    has_inviting_space             BOOLEAN     NOT NULl,
    assigned_person_role           VARCHAR(32) NOT NULL, /* ENUM */
    assigned_person_title          VARCHAR(64), /* ENUM */
    apprenticeship_program         VARCHAR(64), /* ENUM */
    trains_and_mentors_apprentices BOOLEAN,
    has_check_in_timetables        BOOLEAN     NOT NULL,
    has_book_checkout_system       BOOLEAN     NOT NULl,
    number_of_student_librarians   INT         NOT NULL,
    reason_no_student_librarians   VARCHAR(128),
    has_sufficient_training        BOOLEAN     NOT NULL,
    teacher_support                VARCHAR(128),
    parent_support                 VARCHAR(128),

    CONSTRAINT fk_school_report_lib_report FOREIGN KEY (school_report_id) REFERENCES school_reports (id)
);


DROP TABLE IF EXISTS school_reports_without_libraries;
CREATE TABLE IF NOT EXISTS school_reports_without_libraries
(
    id               BIGSERIAL    NOT NULL PRIMARY KEY,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at       TIMESTAMP,
    school_report_id INT          NOT NULL,
    reason_why_not   VARCHAR(256) NOT NULL,
    wants_library    BOOLEAN      NOT NULL,
    has_space        BOOLEAN      NOT NULL,
    current_status   VARCHAR(256),
    ready_timeline   VARCHAR(64)  NOT NULL, /* ENUM */

    CONSTRAINT fk_school_report_wo_lib_report FOREIGN KEY (school_report_id) REFERENCES school_reports (id)
);

CREATE TABLE IF NOT EXISTS school_reports_in_progress_libraries
(
    id                             BIGSERIAL   NOT NULL PRIMARY KEY,
    created_at                     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at                     TIMESTAMP,
    school_report_id               INT         NOT NULL,
    is_shared_space                BOOLEAN     NOT NULL,
    has_inviting_space             BOOLEAN     NOT NULl,
    assigned_person_role           VARCHAR(32) NOT NULL, /* ENUM */
    assigned_person_title          VARCHAR(64), /* ENUM */
    apprenticeship_program         VARCHAR(64), /* ENUM */
    trains_and_mentors_apprentices BOOLEAN,

    CONSTRAINT fk_school_report_wo_lib_report FOREIGN KEY (school_report_id) REFERENCES school_reports (id)
);


ALTER TABLE school_contacts
    ALTER COLUMN type SET NOT NULL;
