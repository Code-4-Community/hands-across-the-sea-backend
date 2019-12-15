package com.codeforcommunity.processor;

import com.codeforcommunity.api.IAuthProcessor;
import com.codeforcommunity.auth.IAuthDatabase;
import com.codeforcommunity.auth.JWTCreator;
import com.codeforcommunity.dto.auth.LoginRequest;
import com.codeforcommunity.dto.auth.NewSessionRequest;
import com.codeforcommunity.dto.auth.NewUserRequest;
import com.codeforcommunity.dto.auth.RefreshSessionRequest;
import com.codeforcommunity.dto.auth.RefreshSessionResponse;
import com.codeforcommunity.exceptions.AuthException;
import com.codeforcommunity.dto.*;

public class AuthProcessorImpl implements IAuthProcessor {

    private final IAuthDatabase db;
    private final JWTCreator jwtCreator;

    public AuthProcessorImpl(IAuthDatabase db, JWTCreator jwtCreator) {
        this.db = db;
        this.jwtCreator = jwtCreator;
    }

    @Override
    public RefreshSessionResponse refreshSession(RefreshSessionRequest request) throws AuthException {

        if(!db.isValidRefresh(getSignature(request.getRefreshToken()))) {
            throw new AuthException("refresh token is voided by previous logout");
        }

        String accessToken = jwtCreator.getNewAccessToken(request.getRefreshToken());

        return new RefreshSessionResponse() {{
            setFreshAccessToken(accessToken);
        }};
    }

    @Override
    public void newUser(NewUserRequest request) {
        db.newUser(request.getUsername(), request.getEmail(), request.getPassword(), request.getFirstName(),
                request.getLastName());
    }

    @Override
    public SessionResponse login(LoginRequest loginRequest) throws AuthException {
        if (db.isValidUser(loginRequest.getUsername(), loginRequest.getPassword())) {
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
        db.invalidateRefresh(refreshToken);
    }

    private String getSignature(String token) {
        return token.split("\\.")[2];
    }

}
