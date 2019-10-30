DROP TABLE member_attended_meeting;
DROP TABLE meeting;
DROP TABLE member;

CREATE TABLE IF NOT EXISTS user (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    first_name VARCHAR(36),
    last_name VARCHAR(36)
);

CREATE TABLE IF NOT EXISTS note (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    user_id VARCHAR(36),
    title VARCHAR(36),
    body VARCHAR(36),

    CONSTRAINT fk_attended_member FOREIGN KEY (user_id) REFERENCES user (id)
);
