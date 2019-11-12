ALTER TABLE note_user 
ADD COLUMN pass_hash VARCHAR(126);

ALTER TABLE note_user
ADD COLUMN email VARCHAR(36);

ALTER TABLE note_user
ADD COLUMN user_name VARCHAR(36);

CREATE TABLE IF NOT EXISTS sessions (
	id SERIAL PRIMARY KEY,
	user_id INT,
	refresh_hash VARCHAR(64),
	created TIMESTAMP,
	refresh_uses INT, 

	CONSTRAINT fk_session_user FOREIGN KEY (user_id) REFERENCES note_user (id)

);