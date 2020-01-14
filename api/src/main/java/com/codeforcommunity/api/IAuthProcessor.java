package com.codeforcommunity.api;

import com.codeforcommunity.auth.AuthUtils;
import com.codeforcommunity.dto.auth.LoginRequest;
import com.codeforcommunity.dto.auth.NewUserRequest;
import com.codeforcommunity.dto.auth.RefreshSessionRequest;
import com.codeforcommunity.dto.auth.RefreshSessionResponse;
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

}
