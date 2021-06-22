ALTER TABLE school_reports_with_libraries
    ADD COLUMN grades_attended VARCHAR(64)[];

ALTER TABLE school_reports_without_libraries
    ADD COLUMN grades_attended VARCHAR(64)[];

