DROP TABLE sessions;

CREATE TABLE IF NOT EXISTS blacklisted_refreshes (
    refresh_hash VARCHAR(64) PRIMARY KEY,
    expires TIMESTAMP
)
