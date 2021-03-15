ALTER TABLE school_reports_with_libraries
    ADD COLUMN visit_reason VARCHAR(128);

ALTER TABLE school_reports_without_libraries
    ADD COLUMN visit_reason VARCHAR(128);

ALTER TABLE school_reports_with_libraries
    ALTER COLUMN visit_reason SET NOT NULL;

ALTER TABLE school_reports_without_libraries
    ALTER COLUMN visit_reason SET NOT NULL;
