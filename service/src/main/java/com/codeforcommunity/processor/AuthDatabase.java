package com.codeforcommunity.processor;

import com.codeforcommunity.auth.AuthUtils;
import com.codeforcommunity.exceptions.AuthException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.generated.Tables;
import org.jooq.generated.tables.records.NoteUserRecord;

import java.sql.Timestamp;
import java.time.Instant;
import org.jooq.generated.tables.records.VerificationKeysRecord;

import static org.jooq.generated.Tables.NOTE_USER;
import static org.jooq.generated.Tables.VERIFICATION_KEYS;

public class AuthDatabase {

    private final DSLContext db;
    private AuthUtils sha;

    public AuthDatabase(DSLContext db) {
        this.sha = new AuthUtils();
        this.db = db;
    }

    public boolean isValidLogin(String user, String pass) {
        //NOTE: Will throw DataAccessException if the username is not present
        String passHash = db.selectFrom(NOTE_USER)
            .where(NOTE_USER.USER_NAME.eq(user)).fetchOne().getPassHash();

        return sha.hash(pass).equals(passHash);
    }

    public void createNewUser(String username, String email, String password, String firstName, String lastName) {

        boolean emailUsed = db.fetchExists(db.selectFrom(NOTE_USER).where(NOTE_USER.EMAIL.eq(email)));
        boolean usernameUsed = db.fetchExists(db.selectFrom(NOTE_USER).where(NOTE_USER.USER_NAME.eq(username)));
        if (emailUsed || usernameUsed) {
            //TODO: Throw some exception type thing
        }

        String pass_hash = sha.hash(password);
        NoteUserRecord newUser = db.newRecord(NOTE_USER);
        newUser.setUserName(username);
        newUser.setEmail(email);
        newUser.setPassHash(pass_hash);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.store();
    }

    public void addToBlackList(String signature) {
        Timestamp expirationTimestamp = Timestamp.from(Instant.now().plusMillis(AuthUtils.refresh_exp));
        db.newRecord(Tables.BLACKLISTED_REFRESHES)
            .values(signature, expirationTimestamp)
            .store();
    }

    public boolean isOnBlackList(String signature) {
        int count = db.fetchCount(
            Tables.BLACKLISTED_REFRESHES
                .where(Tables.BLACKLISTED_REFRESHES.REFRESH_HASH.eq(signature)));

        return count >= 1;
    }

    public void createSecretKey(int userId, String token) throws AuthException {
        if (!doesUserExist(userId)) {
            throw new AuthException("User does not exist.");
        }

        db.insertInto(Tables.VERIFICATION_KEYS).columns(VERIFICATION_KEYS.ID, VERIFICATION_KEYS.USER_ID)
            .values(token, userId).execute();
    }

    private boolean doesUserExist(int userId) {
        Result<NoteUserRecord> userRecord = db.selectFrom(Tables.NOTE_USER).where(NOTE_USER.ID.eq(userId)).fetch();
        return userRecord.isNotEmpty();
    }

    public int validateSecretKey(String secretKey) throws AuthException {
        Timestamp cutoffDate = Timestamp.from(LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        Result<VerificationKeysRecord> veriKey = db.selectFrom(Tables.VERIFICATION_KEYS)
            .where(VERIFICATION_KEYS.ID.eq(secretKey)
                .and(VERIFICATION_KEYS.USED.eq((short)0))).fetch();

        if (veriKey.isEmpty()) {
            throw new AuthException("Token is invalid.");
        }

        if (veriKey.get(0).getCreated().before(cutoffDate)) {
            throw new AuthException("Token has expired.");
        }

        int userId = veriKey.get(0).getUserId();
        db.update(Tables.VERIFICATION_KEYS).set(VERIFICATION_KEYS.USED, (short)1).execute();
        return userId;
    }
}
