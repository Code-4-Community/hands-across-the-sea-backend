ALTER TABLE note_user
DROP COLUMN pass_hash;

ALTER TABLE note_user
ADD COLUMN pass_hash BYTEA;