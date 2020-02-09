package com.codeforcommunity.dataaccess;

import com.codeforcommunity.auth.AuthUtils;
import com.codeforcommunity.exceptions.CreateUserException;
import com.codeforcommunity.processor.AuthProcessorImpl;
import org.jooq.DSLContext;
import org.jooq.generated.Tables;
import org.jooq.generated.tables.pojos.NoteUser;
import org.jooq.generated.tables.records.NoteUserRecord;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static org.jooq.generated.Tables.NOTE_USER;

/**
 * Encapsulates all the database operations that are required for {@link AuthProcessorImpl}.
 */
public class AuthDatabaseOperations {

    private final DSLContext db;
    private AuthUtils sha;

    public AuthDatabaseOperations(DSLContext db) {
        this.sha = new AuthUtils();
        this.db = db;
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
            .filter(noteUser -> sha.hash(pass).equals(noteUser.getPassHash()))
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

        String pass_hash = sha.hash(password);
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
        Timestamp expirationTimestamp = Timestamp.from(Instant.now().plusMillis(AuthUtils.refresh_exp));
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
}
