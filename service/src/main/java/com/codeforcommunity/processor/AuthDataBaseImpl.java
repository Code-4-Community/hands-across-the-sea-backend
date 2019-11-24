package com.codeforcommunity.processor;

import com.codeforcommunity.auth.AuthUtils;
import com.codeforcommunity.auth.IAuthDatabase;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.generated.Tables;
import org.jooq.generated.tables.records.NoteUserRecord;

import java.sql.Timestamp;
import java.time.Instant;

public class AuthDataBaseImpl implements IAuthDatabase {

    private final DSLContext db;
    private AuthUtils sha;

    public AuthDataBaseImpl(DSLContext db) {
        try {
            this.sha = new AuthUtils();
        } catch (Exception e) {
            //what to do here?
        }
        this.db = db;
    }

    public boolean isValidUser(String user, String pass) {

        Result<NoteUserRecord> noteUser = db.selectFrom(Tables.NOTE_USER).where(Tables.NOTE_USER.USER_NAME.eq(user)).fetch();

        String pass_hash = noteUser.getValue(0, Tables.NOTE_USER.PASS_HASH);

        return sha.hash(pass).equals(pass_hash);
    }

    public boolean newUser(String username, String email, String password, String firstName, String lastName) {

        String pass_hash = sha.hash(password);
        int i = db.insertInto(Tables.NOTE_USER, Tables.NOTE_USER.USER_NAME, Tables.NOTE_USER.EMAIL, Tables.NOTE_USER.PASS_HASH,
                Tables.NOTE_USER.FIRST_NAME, Tables.NOTE_USER.LAST_NAME).values(username, email, pass_hash,
                firstName, lastName).execute();

        return i == 1;
    }

    public boolean recordNewRefreshToken(String signature, String username) {

        Result<NoteUserRecord> noteUser = db.selectFrom(Tables.NOTE_USER).where(Tables.NOTE_USER.USER_NAME.eq(username))
                .fetch();
        Timestamp timestamp = Timestamp.from(Instant.now());
        int userid = noteUser.getValue(0, Tables.NOTE_USER.ID);
        int i = db.insertInto(Tables.SESSIONS, Tables.SESSIONS.REFRESH_HASH, Tables.SESSIONS.USER_ID,
                Tables.SESSIONS.REFRESH_USES, Tables.SESSIONS.CREATED).values(signature, userid, 1, timestamp).execute();

        return i == 1;
    }

    public boolean invalidateRefresh(String signature) {

        int i = db.update(Tables.SESSIONS).set(Tables.SESSIONS.VOIDED, true).where(Tables.SESSIONS.REFRESH_HASH.
                eq(signature)).execute();

        return i == 1;
    }

    public boolean isValidRefresh(String signature) {

        Record record = db.select(Tables.SESSIONS.VOIDED).from(Tables.SESSIONS).where(Tables.SESSIONS.REFRESH_HASH.
                eq(signature)).fetchAny();

        return (boolean) record.getValue("voided");

    }
}
