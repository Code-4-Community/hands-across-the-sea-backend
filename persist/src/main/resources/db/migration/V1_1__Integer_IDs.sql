ALTER TABLE users
    ALTER COLUMN id TYPE INT;

ALTER TABLE schools
    ALTER COLUMN id TYPE INT;

ALTER TABLE school_contacts
    ALTER COLUMN id TYPE INT;

ALTER TABLE school_contacts
    ALTER COLUMN school_id TYPE INT;

ALTER TABLE school_reports
    ALTER COLUMN id TYPE INT;

ALTER TABLE school_reports
    ALTER COLUMN user_id TYPE INT;

ALTER TABLE school_reports
    ALTER COLUMN school_id TYPE INT;

ALTER TABLE school_reports_with_libraries
    ALTER COLUMN id TYPE INT;

ALTER TABLE school_reports_with_libraries
    ALTER COLUMN user_id TYPE INT;

ALTER TABLE school_reports_with_libraries
    ALTER COLUMN school_id TYPE INT;

ALTER TABLE school_reports_without_libraries
    ALTER COLUMN id TYPE INT;

ALTER TABLE school_reports_without_libraries
    ALTER COLUMN user_id TYPE INT;

ALTER TABLE school_reports_without_libraries
    ALTER COLUMN school_id TYPE INT;

ALTER TABLE verification_keys
    ALTER COLUMN user_id TYPE INT;
