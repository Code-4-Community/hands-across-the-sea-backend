package com.codeforcommunity.processor;

import com.codeforcommunity.api.IAuthProcessor;
import com.codeforcommunity.auth.JWTCreator;
import com.codeforcommunity.dto.auth.LoginRequest;
import com.codeforcommunity.dto.auth.NewUserRequest;
import com.codeforcommunity.dto.auth.RefreshSessionRequest;
import com.codeforcommunity.dto.auth.RefreshSessionResponse;
import com.codeforcommunity.exceptions.AuthException;
import com.codeforcommunity.dto.*;
import org.jooq.DSLContext;

public class AuthProcessorImpl implements IAuthProcessor {

    private final AuthDatabase authDatabase;
    private final JWTCreator jwtCreator;

    public AuthProcessorImpl(DSLContext db, JWTCreator jwtCreator) {
        this.authDatabase = new AuthDatabase(db);
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

        authDatabase.createNewUser(request.getUsername(), request.getEmail(), request.getPassword(),
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
        if (authDatabase.isValidLogin(loginRequest.getUsername(), loginRequest.getPassword())) {
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
        authDatabase.addToBlackList(getSignature(refreshToken));
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
        if(authDatabase.isOnBlackList(getSignature(request.getRefreshToken()))) {
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

}
