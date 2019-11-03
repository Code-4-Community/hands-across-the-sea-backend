DROP TABLE member_attended_meeting;
DROP TABLE meeting;
DROP TABLE member;

CREATE TABLE IF NOT EXISTS note_user (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(36),
    last_name VARCHAR(36)
);

CREATE TABLE IF NOT EXISTS note (
    id SERIAL PRIMARY KEY,
    user_id INT,
    title VARCHAR(36),
    body VARCHAR(36),

    CONSTRAINT fk_attended_member FOREIGN KEY (user_id) REFERENCES note_user (id)
);
