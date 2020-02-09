package com.codeforcommunity.processor;

import com.codeforcommunity.auth.AuthUtils;
import com.codeforcommunity.exceptions.AuthException;
import org.jooq.DSLContext;
import org.jooq.generated.Tables;
import org.jooq.generated.tables.records.NoteUserRecord;

import java.sql.Timestamp;
import java.time.Instant;
import org.jooq.generated.tables.records.VerificationKeysRecord;

import static org.jooq.generated.Tables.NOTE_USER;
import static org.jooq.generated.Tables.VERIFICATION_KEYS;

public class AuthDatabase {


    public static final int CUTOFF_TIME = 86400;
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
        if (!db.fetchExists(Tables.NOTE_USER.where(NOTE_USER.ID.eq(userId)))) {
            throw new AuthException("User does not exist.");
        }

        VerificationKeysRecord keysRecord = db.newRecord(Tables.VERIFICATION_KEYS);
        keysRecord.setId(token);
        keysRecord.setUserId(userId);
        keysRecord.store();
    }

    private boolean isTokenDateValid(VerificationKeysRecord tokenResult) {
        Timestamp cutoffDate = Timestamp.from(Instant.now().minusSeconds(CUTOFF_TIME));
        if (tokenResult.getCreated().before(cutoffDate)) {
            return false;
        }

        return true;
    }

    public int validateSecretKey(String secretKey) throws AuthException {
        VerificationKeysRecord veriKey = db.selectFrom(Tables.VERIFICATION_KEYS)
            .where(VERIFICATION_KEYS.ID.eq(secretKey)
                .and(VERIFICATION_KEYS.USED.eq((short)0))).fetchOneInto(VerificationKeysRecord.class);

        if (veriKey == null) {
            throw new AuthException("Token is invalid.");
        }

        if (!isTokenDateValid(veriKey)) {
            throw new AuthException("Token has expired.");
        }

        veriKey.setUsed((short)1);
        veriKey.store();
        db.update(Tables.NOTE_USER).set(NOTE_USER.VERIFIED, (short)1).execute();
        return veriKey.getUserId();
    }
}
