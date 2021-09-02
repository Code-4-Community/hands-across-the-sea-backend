ALTER TABLE school_reports_with_libraries
    ALTER COLUMN most_recent_shipment_year
        DROP NOT NULL;

ALTER TABLE school_reports_without_libraries
    ALTER COLUMN most_recent_shipment_year
        DROP NOT NULL;