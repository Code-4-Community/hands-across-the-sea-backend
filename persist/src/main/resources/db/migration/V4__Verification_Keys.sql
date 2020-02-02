CREATE TABLE IF NOT EXISTS verification_keys (
  id      VARCHAR(50) NOT NULL,
  user_id int NOT NULL,
  used    smallint DEFAULT 0,

  CONSTRAINT verification_keys_pk
    PRIMARY KEY (id)
);

ALTER TABLE note_user
  ADD COLUMN verified smallint DEFAULT 0
