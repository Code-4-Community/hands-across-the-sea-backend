package com.codeforcommunity.api;

import com.codeforcommunity.auth.DTO.*;
import com.codeforcommunity.auth.exceptions.AuthException;

public interface IAuthProcessor {

    /**
     * Validates given access token. See implementation for definition of "valid". This is the one method to use
     * when authorizing users requesting access to protected resources.
     * @param accessToken token to validate.
     * @return boolean if token is valid.
     */
    boolean isAuthorized(String accessToken);

    /**
     * Given information of an already authenticated user will initiate a new session and supply client with
     * refresh and access token for maintaining authorization through session. Note: this method and by extension
     * this class does not authenticate users rather handles session token specific operations of already
     * authenticated users.
     * @param request session request object detailing any user specific data needed to generate session tokens.
     * @return session response object containing access and refresh tokens.
     * @throws AuthException if not passed needed information in request.
     */
    SessionResponse getSession(NewSessionRequest request) throws AuthException;

    /**
     * Ends session by invalidating given refresh token as to not allow it to be used again. See implementation
     * for specifics.
     * @param refreshToken refresh token related with session to be ended.
     */
    void endSession(String refreshToken);

    /**
     * Allows clients to refresh session and receive access token using given refresh token.
     * @param request request object containing refresh token as well as needed user information.
     * @return response object containing new access token to be passed to client.
     * @throws AuthException if given refresh token is invalid.
     */
    RefreshSessionResponse refreshSession(RefreshSessionRequest request) throws AuthException;

    /**
     * Given user credentials will decide if given correlates to a known user of our application.
     * @param request request containing user credentials to authenticate.
     * @return boolean if and only if user credentials exists in our records.
     */
    boolean isUser(IsUserRequest request);

    /**
     * Creates a new user to be known to our application.
     * @param request request object containing new user information.
     */
    void newUser(NewUserRequest request);

}
