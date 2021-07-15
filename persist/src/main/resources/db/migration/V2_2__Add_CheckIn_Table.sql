ALTER TABLE school_reports_without_libraries
    ADD COLUMN timetable_id INT;

ALTER TABLE school_reports_with_libraries
    ADD COLUMN timetable_id INT;

CREATE TABLE IF NOT EXISTS timetables
(
    id           BIGSERIAL NOT NULL PRIMARY KEY,
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at   TIMESTAMP,
    month        INT       NOT NULL,
    kindergarten INT[],
    first_grade  INT[],
    second_grade INT[],
    third_grade  INT[],
    fourth_grade INT[],
    fifth_grade  INT[],
    sixth_grade  INT[],
    form_one     INT[],
    form_two     INT[],
    form_three   INT[],
    form_four    INT[],
    form_five    INT[]
);

/* [jooq ignore start] */
DROP TRIGGER IF EXISTS timetables_trig_set_updated_at ON timetables;
CREATE TRIGGER timetables_trig_set_updated_at
    BEFORE UPDATE
    ON timetables
    FOR EACH ROW
EXECUTE PROCEDURE
    func_set_updated_at_timestamp();
/* [jooq ignore stop] */