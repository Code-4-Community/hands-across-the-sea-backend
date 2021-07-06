ALTER TABLE school_reports_without_libraries
    DROP COLUMN current_status;
ALTER TABLE school_reports_without_libraries
    ADD COLUMN current_status VARCHAR(64)[];