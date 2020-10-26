ALTER TABLE note_user
    ADD CONSTRAINT email_unique UNIQUE (email);

ALTER TABLE note_user
    ADD CONSTRAINT name_unique UNIQUE (user_name);