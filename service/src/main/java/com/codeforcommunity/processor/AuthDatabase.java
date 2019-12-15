package com.codeforcommunity.processor;

import com.codeforcommunity.auth.AuthUtils;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;
import org.jooq.generated.Tables;
import org.jooq.generated.tables.records.NoteUserRecord;

import java.sql.Timestamp;
import java.time.Instant;

import static org.jooq.generated.Tables.NOTE_USER;

public class AuthDatabase {

    private final DSLContext db;
    private AuthUtils sha;

    public AuthDatabase(DSLContext db) {
        this.sha = new AuthUtils();
        this.db = db;
    }

    public boolean isValidLogin(String user, String pass) {

        Result<NoteUserRecord> noteUser = db.selectFrom(NOTE_USER).where(NOTE_USER.USER_NAME.eq(user)).fetch();

        String pass_hash = noteUser.getValue(0, NOTE_USER.PASS_HASH);

        return sha.hash(pass).equals(pass_hash);
    }

    public void createNewUser(String username, String email, String password, String firstName, String lastName) {

        boolean emailUsed = db.fetchExists(db.selectFrom(NOTE_USER).where(NOTE_USER.EMAIL.eq(email)));
        boolean usernameUsed = db.fetchExists(db.selectFrom(NOTE_USER).where(NOTE_USER.USER_NAME.eq(username)));
        if (emailUsed || usernameUsed) {
            //TODO: Throw some exception type thing
        }

        String pass_hash = sha.hash(password);
        db.insertInto(NOTE_USER, NOTE_USER.USER_NAME, NOTE_USER.EMAIL, NOTE_USER.PASS_HASH,
            NOTE_USER.FIRST_NAME, NOTE_USER.LAST_NAME).values(username, email, pass_hash,
            firstName, lastName).execute();
    }

    public void addToBlackList(String signature) {
        Timestamp timestamp = Timestamp.from(Instant.now().plusMillis(AuthUtils.refresh_exp));
        db.insertInto(Tables.BLACKLISTED_REFRESHES, Tables.BLACKLISTED_REFRESHES.REFRESH_HASH,
            Tables.BLACKLISTED_REFRESHES.EXPIRES).values(signature, timestamp).execute();
    }

    public boolean isOnBlackList(String signature) {

        int count = db.selectCount().from(Tables.BLACKLISTED_REFRESHES)
            .where(Tables.BLACKLISTED_REFRESHES.REFRESH_HASH.eq(signature))
            .fetchOne(0, int.class);

        return count == 1;

    }
}
