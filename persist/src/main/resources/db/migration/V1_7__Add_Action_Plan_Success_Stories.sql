ALTER TABLE school_reports_with_libraries
    ADD COLUMN action_plan VARCHAR(512);

ALTER TABLE school_reports_with_libraries
    ADD COLUMN success_stories VARCHAR(512);

ALTER TABLE school_reports_without_libraries
    ADD COLUMN action_plan VARCHAR(512);

ALTER TABLE school_reports_without_libraries
    ADD COLUMN success_stories VARCHAR(512);
