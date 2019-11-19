package com.codeforcommunity.processor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import com.codeforcommunity.api.IAuthProcessor;
import com.codeforcommunity.auth.AuthUtils;
import com.codeforcommunity.auth.DTO.*;
import com.codeforcommunity.auth.IAuthDatabase;
import com.codeforcommunity.auth.exceptions.AuthException;

import java.time.Instant;
import java.util.Date;

public class AuthProcessorImpl implements IAuthProcessor {

    private IAuthDatabase db;

    private static Date refreshExp = Date.from(Instant.now().plusMillis(AuthUtils.refresh_exp));
    private static Date accessExp = Date.from(Instant.now().plusMillis(AuthUtils.access_exp));
    private static final Algorithm algorithm = Algorithm.HMAC256("secretKey");
    private static Verification verification = getDefaultClaimVerification();

    public AuthProcessorImpl(IAuthDatabase db) {
        this.db = db;
    }

    /**
     * Verifies that given access token is unedited and unexpired. Also will confirm any claims defined in
     * @code this.getDefaultClaimVerification().
     * @param accessToken token to be validated
     * @return true if and only if all conforms to all of said conditions.
     */
    @Override
    public boolean isAuthorized(String accessToken) {
        try {
            verification.build().verify(accessToken);
            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    @Override
    public SessionResponse getSession(NewSessionRequest request) throws AuthException {

        try {

            JWTCreator.Builder bld = getTokenBuilderWithCommonClaims().withClaim("username",
                    request.getUsername());

            String accessToken = getFinalToken(bld, accessExp);
            String refreshToken = getFinalToken(bld, refreshExp);

            db.recordNewRefreshToken(getSignature(refreshToken), request.getUsername());

            return new SessionResponse() {{
                setAccessToken(accessToken);
                setRefreshToken(refreshToken);
            }};

        } catch (JWTCreationException exception) {
            throw new AuthException(exception.getMessage());
        }

    }

    @Override
    public void endSession(String refreshToken) {
        db.invalidateRefresh(getSignature(refreshToken));
    }

    @Override
    public RefreshSessionResponse refreshSession(RefreshSessionRequest request) throws AuthException {

        if(!db.isValidRefresh(getSignature(request.getRefreshToken()))) {
            throw new AuthException("refresh token is voided by previous logout");
        }

        String username;

        try {

            DecodedJWT jwt = verification.build().verify(request.getRefreshToken());
            username = jwt.getClaim("username").asString();

        } catch (JWTVerificationException exception) {
            throw new AuthException("unable to verify token");
        }

        String freshAccessToken = getFinalToken(getTokenBuilderWithCommonClaims()
                .withClaim("username", username), accessExp);

        return new RefreshSessionResponse() {{
            setFreshAccessToken(freshAccessToken);
        }};
    }

    @Override
    public boolean isUser(IsUserRequest request) {
        return db.isValidUser(request.getUsername(), request.getPassword());
    }

    @Override
    public void newUser(NewUserRequest request) {
        db.newUser(request.getUsername(), request.getEmail(), request.getPassword(), request.getFirstName(),
                request.getLastName());
    }

    /**
     * Creates token builder with all default claims we have decided should be in every token.
     * @return token builder object.
     */
    private JWTCreator.Builder getTokenBuilderWithCommonClaims() {
        return JWT.create()
                .withIssuer("c4c");
    }

    private String getFinalToken(JWTCreator.Builder builder, Date expiration) {
        return builder.withExpiresAt(expiration).sign(algorithm);
    }

    private String getSignature(String token) {
        return token.split("\\.")[2];
    }

    /**
     * Create verification object that ensures all default claims we have decided should be in every token are present.
     * @return verification object.
     */
    private static Verification getDefaultClaimVerification() {
        return JWT.require(algorithm).withIssuer("c4c");
    }

}
