package com.codeforcommunity.api;

import com.codeforcommunity.dto.auth.ForgotPasswordRequest;
import com.codeforcommunity.dto.auth.LoginRequest;
import com.codeforcommunity.dto.auth.NewUserRequest;
import com.codeforcommunity.dto.auth.RefreshSessionRequest;
import com.codeforcommunity.dto.auth.RefreshSessionResponse;
import com.codeforcommunity.dto.auth.ResetPasswordRequest;
import com.codeforcommunity.dto.auth.SessionResponse;
import com.codeforcommunity.exceptions.AuthException;

public interface IAuthProcessor {

  /**
   * Creates a new user to be known to our application.
   *
   * @param request request object containing new user information.
   */
  SessionResponse signUp(NewUserRequest request) throws AuthException;

  /**
   * Given a LoginRequest log the user in if they're valid and return access and refresh tokens for
   * their session.
   *
   * @throws AuthException If the given email / password combination is invalid
   */
  SessionResponse login(LoginRequest loginRequest) throws AuthException;

  /**
   * Logs the user out by adding the given refresh token to the blacklist so that it cannot be used
   * for future refreshes.
   */
  void logout(String refreshToken);

  /**
   * Allows clients to refresh session and receive access token using given refresh token.
   *
   * @param request request object containing refresh token as well as needed user information.
   * @return response object containing new access token to be passed to client.
   * @throws AuthException if given refresh token is invalid.
   */
  RefreshSessionResponse refreshSession(RefreshSessionRequest request) throws AuthException;

  /**
   * If the given request corresponds to a real user, send that user an email for them to reset
   * their password with a secret key.
   */
  void requestPasswordReset(ForgotPasswordRequest request);

  /**
   * Given a secret key and a new password, update the user that's associated with the key's
   * password.
   */
  void resetPassword(ResetPasswordRequest request);

  /**
   * Allows clients to submit a secret key in order to verify their email.
   *
   * @param secretKey string of user's verification token.
   */
  void verifyEmail(String secretKey);
}
