package com.codeforcommunity.dataaccess;

import com.codeforcommunity.auth.Passwords;
import com.codeforcommunity.exceptions.CreateUserException;
import com.codeforcommunity.exceptions.ExpiredTokenException;
import com.codeforcommunity.exceptions.InvalidTokenException;
import com.codeforcommunity.exceptions.UserDoesNotExistException;
import com.codeforcommunity.processor.AuthProcessorImpl;
import com.codeforcommunity.propertiesLoader.PropertiesLoader;
import org.jooq.DSLContext;
import org.jooq.generated.Tables;
import org.jooq.generated.tables.pojos.NoteUser;
import org.jooq.generated.tables.records.NoteUserRecord;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import org.jooq.generated.tables.records.VerificationKeysRecord;

import static org.jooq.generated.Tables.NOTE_USER;

/**
 * Encapsulates all the database operations that are required for {@link AuthProcessorImpl}.
 */
public class AuthDatabaseOperations {

    private final DSLContext db;
    public final int SECONDS_VERIFICATION_EMAIL_VALID;
    public final int MS_REFRESH_EXPIRATION;

    public AuthDatabaseOperations(DSLContext db) {
        this.db = db;
        this.SECONDS_VERIFICATION_EMAIL_VALID = Integer.valueOf(PropertiesLoader
            .getExpirationProperties().getProperty("seconds_verification_email_valid"));
        this.MS_REFRESH_EXPIRATION = Integer.valueOf(PropertiesLoader
            .getExpirationProperties().getProperty("ms_refresh_expiration"));
    }

    /**
     * Returns true if the given username and password correspond to a user in the USER table and
     * false otherwise.
     */
    public boolean isValidLogin(String username, String pass) {
        Optional<NoteUser> maybeUser = Optional.ofNullable(db
            .selectFrom(NOTE_USER)
            .where(NOTE_USER.USER_NAME.eq(username))
            .fetchOneInto(NoteUser.class));

        return maybeUser
            .filter(noteUser -> Passwords.isExpectedPassword(pass, noteUser.getPassHash()))
            .isPresent();
    }

    /**
     * TODO: Refactor this method to take in a DTO / POJO instance
     * Creates a new row in the USER table with the given values.
     *
     * @throws CreateUserException if the given username and email are already used in the USER table.
     */
    public void createNewUser(String username, String email, String password, String firstName, String lastName) {

        boolean emailUsed = db.fetchExists(db.selectFrom(NOTE_USER).where(NOTE_USER.EMAIL.eq(email)));
        boolean usernameUsed = db.fetchExists(db.selectFrom(NOTE_USER).where(NOTE_USER.USER_NAME.eq(username)));
        if (emailUsed || usernameUsed) {
            if (emailUsed && usernameUsed) {
                throw new CreateUserException(CreateUserException.UsedField.BOTH);
            } else if (emailUsed) {
                throw new CreateUserException(CreateUserException.UsedField.EMAIL);
            } else {
                throw new CreateUserException(CreateUserException.UsedField.USERNAME);
            }
        }

        byte[] pass_hash = Passwords.createHash(password);
        NoteUserRecord newUser = db.newRecord(NOTE_USER);
        newUser.setUserName(username);
        newUser.setEmail(email);
        newUser.setPassHash(pass_hash);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.store();
    }

    /**
     * Given a JWT signature, store it in the BLACKLISTED_REFRESHES table.
     */
    public void addToBlackList(String signature) {
        Timestamp expirationTimestamp = Timestamp.from(Instant.now().plusMillis(MS_REFRESH_EXPIRATION));
        db.newRecord(Tables.BLACKLISTED_REFRESHES)
            .values(signature, expirationTimestamp)
            .store();
    }

    /**
     * Given a JWT signature return true if it is stored in the BLACKLISTED_REFRESHES table.
     */
    public boolean isOnBlackList(String signature) {
        return db.fetchExists(
            Tables.BLACKLISTED_REFRESHES
                .where(Tables.BLACKLISTED_REFRESHES.REFRESH_HASH.eq(signature)));
    }

    /**
     * Given a userId and token, stores the token in the verification_keys table for the user.
     * @param userId id of the user.
     * @param token token to store for the user.
     * @throws UserDoesNotExistException if given userId does not match a user.
     */
    public void createSecretKey(int userId, String token) {
        if (!db.fetchExists(Tables.NOTE_USER.where(NOTE_USER.ID.eq(userId)))) {
          throw new UserDoesNotExistException(userId);
        }

        VerificationKeysRecord keysRecord = db.newRecord(Tables.VERIFICATION_KEYS);
        keysRecord.setId(token);
        keysRecord.setUserId(userId);
        keysRecord.store();
    }

    /**
     * Determines if given token date is still valid.
     *
     * @param tokenResult VerificationKeysRecord to check.
     * @return true if it is within the time specified in the expiration.properties file.
     */
    private boolean isTokenDateValid(VerificationKeysRecord tokenResult) {
        Timestamp cutoffDate = Timestamp.from(Instant.now().minusSeconds(SECONDS_VERIFICATION_EMAIL_VALID));
        if (tokenResult.getCreated().before(cutoffDate)) {
            return false;
        }
        return true;
    }

    /**
     * Validates the email/secret key for the user it was created for.
     *
     * @param secretKey the secret key to validate.
     * @throws InvalidTokenException if the given token does not exist.
     * @throws ExpiredTokenException if the given token is expired.
     */
    public void validateSecretKey(String secretKey) {
        VerificationKeysRecord veriKey = db.selectFrom(Tables.VERIFICATION_KEYS)
            .where(Tables.VERIFICATION_KEYS.ID.eq(secretKey)
                .and(Tables.VERIFICATION_KEYS.USED.eq(false)))
            .fetchOneInto(VerificationKeysRecord.class);

        if (veriKey == null) {
          throw new InvalidTokenException();
        }

        if (!isTokenDateValid(veriKey)) {
          throw new ExpiredTokenException();
        }

        veriKey.setUsed(false);
        veriKey.store();
        db.update(Tables.NOTE_USER).set(NOTE_USER.VERIFIED,1)
            .where(NOTE_USER.ID.eq(veriKey.getUserId()));
    }
}
