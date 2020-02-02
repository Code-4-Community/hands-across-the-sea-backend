package com.codeforcommunity.processor;

import com.codeforcommunity.api.IAuthProcessor;
import com.codeforcommunity.auth.JWTCreator;
import com.codeforcommunity.dto.auth.LoginRequest;
import com.codeforcommunity.dto.auth.NewUserRequest;
import com.codeforcommunity.dto.auth.RefreshSessionRequest;
import com.codeforcommunity.dto.auth.RefreshSessionResponse;
import com.codeforcommunity.dto.auth.VerifySecretKeyResponse;
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

    //TODO how will we handle/check for clashes in usernames and such
    @Override
    public SessionResponse signUp(NewUserRequest request) {
        // Check that inputs are valid ??
        // Create new refresh jwt
        // Create new access jwt
        // Create new user database row *uses database
        // Return jwts

        String refreshToken = jwtCreator.createNewRefreshToken(request.getUsername());
        String accessToken = jwtCreator.getNewAccessToken(refreshToken);

        authDatabase.createNewUser(request.getUsername(), request.getEmail(), request.getPassword(),
            request.getFirstName(), request.getLastName());

        return new SessionResponse() {{
            setRefreshToken(refreshToken);
            setAccessToken(accessToken);
        }};
    }

    @Override
    public SessionResponse login(LoginRequest loginRequest) throws AuthException {
        // Check if username password combination is good *uses database
        // Create new refresh jwt
        // Create new access jwt
        // Return jwts

        if (authDatabase.isValidLogin(loginRequest.getUsername(), loginRequest.getPassword())) {
            String refreshToken = jwtCreator.createNewRefreshToken(loginRequest.getUsername());
            String accessToken = jwtCreator.getNewAccessToken(refreshToken);

            return new SessionResponse() {{
                setAccessToken(accessToken);
                setRefreshToken(refreshToken);
            }};
        } else {
            throw new AuthException("Invalid user");
        }
    }

    @Override
    public void logout(String refreshToken) {
        // Add refresh jwt to blacklist *uses database

        authDatabase.addToBlackList(getSignature(refreshToken));
    }

    @Override
    public RefreshSessionResponse refreshSession(RefreshSessionRequest request) throws AuthException {
        // Check if refresh jwt is valid
        // Check if refresh jwt is blacklisted *uses database
        // Create new access jwt
        // Return access jwt

        if(authDatabase.isOnBlackList(getSignature(request.getRefreshToken()))) {
            throw new AuthException("refresh token is voided by previous logout");
        }

        String accessToken = jwtCreator.getNewAccessToken(request.getRefreshToken());

        return new RefreshSessionResponse() {{
            setFreshAccessToken(accessToken);
        }};
    }

    private String getSignature(String token) {
        return token.split("\\.")[2];
    }

    @Override
    public VerifySecretKeyResponse validateSecretKey(String secretKey) throws AuthException {
      Integer userId = authDatabase.validateSecretKey(secretKey);

      if (userId == null) {
          throw new AuthException("Secret Key is invalid.");
      }
      return new VerifySecretKeyResponse().setUserId(userId);
    }
}
