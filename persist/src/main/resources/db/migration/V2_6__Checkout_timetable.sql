ALTER TABLE school_reports_with_libraries
    ADD COLUMN checkout_timetable VARCHAR(768) DEFAULT NULL;

ALTER TABLE school_reports_with_libraries
    RENAME COLUMN timetable TO checkin_timetable;