CREATE TABLE IF NOT EXISTS verification_keys (
  id      VARCHAR(50) NOT NULL,
  user_id int NOT NULL,
  used    smallint DEFAULT 0,
  created timestamp DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT verification_keys_pk
    PRIMARY KEY (id),
  CONSTRAINT verification_keys_user_id_fk
    FOREIGN KEY (user_id) REFERENCES note_user(id)
);

ALTER TABLE note_user
  ADD COLUMN verified smallint DEFAULT 0
