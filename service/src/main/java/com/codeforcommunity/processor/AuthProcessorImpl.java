package com.codeforcommunity.processor;

import com.codeforcommunity.api.IAuthProcessor;
import com.codeforcommunity.auth.JWTCreator;
import com.codeforcommunity.auth.Passwords;
import com.codeforcommunity.dataaccess.AuthDatabaseOperations;
import com.codeforcommunity.dto.SessionResponse;
import com.codeforcommunity.dto.auth.LoginRequest;
import com.codeforcommunity.dto.auth.NewUserRequest;
import com.codeforcommunity.dto.auth.RefreshSessionRequest;
import com.codeforcommunity.dto.auth.RefreshSessionResponse;
import com.codeforcommunity.exceptions.AuthException;
import org.jooq.DSLContext;

public class AuthProcessorImpl implements IAuthProcessor {

    private final AuthDatabaseOperations authDatabaseOperations;
    private final JWTCreator jwtCreator;

    public AuthProcessorImpl(DSLContext db, JWTCreator jwtCreator) {
        this.authDatabaseOperations = new AuthDatabaseOperations(db);
        this.jwtCreator = jwtCreator;
    }

    /**
     * Check that inputs are valid with the database
     * Creates a new refresh jwt
     * Creates a new access jwt
     * Creates a new user database row
     * Return the new jwts
     *
     * @throws com.codeforcommunity.exceptions.CreateUserException if the given username or email
     *   are already used.
     */
    @Override
    public SessionResponse signUp(NewUserRequest request) {
        String refreshToken = jwtCreator.createNewRefreshToken(request.getUsername());
        String accessToken = jwtCreator.getNewAccessToken(refreshToken);

        authDatabaseOperations.createNewUser(request.getUsername(), request.getEmail(), request.getPassword(),
            request.getFirstName(), request.getLastName());

        return new SessionResponse() {{
            setRefreshToken(refreshToken);
            setAccessToken(accessToken);
        }};
    }

    /**
     * Checks if username password combination is valid with database
     * Creates a new refresh jwt
     * Creates a new access jwt
     * Return the access and refresh jwts
     *
     * @throws AuthException if the given username password combination is invalid.
     */
    @Override
    public SessionResponse login(LoginRequest loginRequest) throws AuthException {
        if (authDatabaseOperations.isValidLogin(loginRequest.getUsername(), loginRequest.getPassword())) {
            String refreshToken = jwtCreator.createNewRefreshToken(loginRequest.getUsername());
            String accessToken = jwtCreator.getNewAccessToken(refreshToken);

            return new SessionResponse() {{
                setAccessToken(accessToken);
                setRefreshToken(refreshToken);
            }};
        } else {
            throw new AuthException("Could not validate username password combination");
        }
    }

    /**
     * Add refresh jwt to the blacklist token database table
     */
    @Override
    public void logout(String refreshToken) {
        authDatabaseOperations.addToBlackList(getSignature(refreshToken));
    }

    /**
     * Checks if refresh jwt is valid
     * Checks if refresh jwt is blacklisted *uses database
     * Creates a new access jwt
     * Returns the access jwt
     *
     * @throws AuthException if the given refresh token is invalid.
     */
    @Override
    public RefreshSessionResponse refreshSession(RefreshSessionRequest request) throws AuthException {
        if(authDatabaseOperations.isOnBlackList(getSignature(request.getRefreshToken()))) {
            throw new AuthException("The refresh token has been invalidated by a previous logout");
        }

        String accessToken = jwtCreator.getNewAccessToken(request.getRefreshToken());

        return new RefreshSessionResponse() {{
            setFreshAccessToken(accessToken);
        }};
    }

    /**
     * Gets the signature of a given JWT string. Will be the third segment of a JWT that is
     * partitioned by "." characters.
     */
    private String getSignature(String token) {
        return token.split("\\.")[2];
    }

    @Override
    public void validateSecretKey(String secretKey) {
        authDatabaseOperations.validateSecretKey(secretKey);
    }

    @Override
    public String createSecretKey(int userId) {
       String token = Passwords.generateRandomToken(50);

       authDatabaseOperations.createSecretKey(userId, token);

       return token;
    }
}
