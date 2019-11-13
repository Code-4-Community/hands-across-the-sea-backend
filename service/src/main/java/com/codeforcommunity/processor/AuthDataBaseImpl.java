package com.codeforcommunity.processor;

import com.codeforcommunity.auth.JWT.alg.SHA;
import com.codeforcommunity.auth.JWT.db.AuthDataBase;
import org.h2.engine.SessionRemote;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.generated.Tables;
import org.jooq.generated.tables.Sessions;
import org.jooq.generated.tables.records.NoteUserRecord;
import org.jooq.generated.tables.records.SessionsRecord;

public class AuthDataBaseImpl implements AuthDataBase {

    private final DSLContext db;
    private SHA sha;

    public AuthDataBaseImpl(DSLContext db) {
        try {
            this.sha = new SHA();
        } catch (Exception e) {

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
    //todo add user id to jwt
    public boolean recordNewRefreshToken(String signature, String username) {

        Result<NoteUserRecord> noteUser = db.selectFrom(Tables.NOTE_USER).where(Tables.NOTE_USER.USER_NAME.eq(username))
                .fetch();

        int userid = noteUser.getValue(0, Tables.NOTE_USER.ID);
        int i = db.insertInto(Tables.SESSIONS, Tables.SESSIONS.REFRESH_HASH, Tables.SESSIONS.USER_ID, //todo figure out how to date things
                Tables.SESSIONS.REFRESH_USES).values(signature, userid, 1).execute();

        return i == 1;
    }

    @Override
    public boolean invalidateRefresh(String signature) {

        int i = db.update(Tables.SESSIONS).set(Tables.SESSIONS.VOIDED, true).where(Tables.SESSIONS.REFRESH_HASH.
                eq(signature)).execute();

        return i == 1; //todo implement this
    }

    public boolean isValidRefresh(String signature) {

        Record record = db.select(Tables.SESSIONS.VOIDED).from(Tables.SESSIONS).where(Tables.SESSIONS.REFRESH_HASH.
                eq(signature)).fetchAny();

        return (boolean) record.getValue("voided");

    }
}
