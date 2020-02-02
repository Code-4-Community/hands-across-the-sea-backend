package com.codeforcommunity.api;

import com.codeforcommunity.auth.AuthUtils;
import com.codeforcommunity.dto.auth.LoginRequest;
import com.codeforcommunity.dto.auth.NewUserRequest;
import com.codeforcommunity.dto.auth.RefreshSessionRequest;
import com.codeforcommunity.dto.auth.RefreshSessionResponse;
import com.codeforcommunity.dto.auth.VerifySecretKeyResponse;
import com.codeforcommunity.exceptions.AuthException;
import com.codeforcommunity.dto.*;

public interface IAuthProcessor {

    /**
     * Creates a new user to be known to our application.
     * @param request request object containing new user information.
     */
    SessionResponse signUp(NewUserRequest request) throws AuthException;

    /**
     * Logs in. TODO
     *
     * @throws AuthException
     */
    SessionResponse login(LoginRequest loginRequest) throws AuthException;

    /**
     * Logs out. TODO
     */
    void logout(String refreshToken);

    /**
     * Allows clients to refresh session and receive access token using given refresh token.
     * @param request request object containing refresh token as well as needed user information.
     * @return response object containing new access token to be passed to client.
     * @throws AuthException if given refresh token is invalid.
     */
    RefreshSessionResponse refreshSession(RefreshSessionRequest request) throws AuthException;

    /**
     * Allows clients to submit a secret key in order to verify their email.
     * @param secretKey string of user's verificaiton token.
     * @return a boolean representing whether or not the token was able to be verified.
     */
    VerifySecretKeyResponse validateSecretKey(String secretKey);

    /**
     * Creates a secret key to validate a user's email and stores it in the verification_keys table.
     * @param userId the id for the given user.
     * @return the token created for the given user.
     */
    String createSecretKey(int userId) throws AuthException;
}
