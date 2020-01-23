CREATE TABLE IF NOT EXISTS verification_keys (
  id      VARCHAR(36)   NOT NULL,
  user_id VARCHAR(255)  NOT NULL,

  CONSTRAINT verification_keys_pk
    PRIMARY KEY (id)
);