package com.codeforcommunity.processor;

import com.codeforcommunity.api.IAuthProcessor;
import com.codeforcommunity.auth.JWTCreator;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.auth.Passwords;
import com.codeforcommunity.dataaccess.AuthDatabaseOperations;
import com.codeforcommunity.dto.auth.ForgotPasswordRequest;
import com.codeforcommunity.dto.auth.LoginRequest;
import com.codeforcommunity.dto.auth.NewUserRequest;
import com.codeforcommunity.dto.auth.RefreshSessionRequest;
import com.codeforcommunity.dto.auth.RefreshSessionResponse;
import com.codeforcommunity.dto.auth.ResetPasswordRequest;
import com.codeforcommunity.dto.auth.SessionResponse;
import com.codeforcommunity.enums.VerificationKeyType;
import com.codeforcommunity.exceptions.AuthException;
import com.codeforcommunity.exceptions.EmailAlreadyInUseException;
import com.codeforcommunity.exceptions.TokenInvalidException;
import com.codeforcommunity.requester.Emailer;
import java.util.Optional;
import org.jooq.DSLContext;
import org.jooq.generated.tables.pojos.Users;
import org.jooq.generated.tables.records.UsersRecord;

public class AuthProcessorImpl implements IAuthProcessor {

  private final AuthDatabaseOperations authDatabaseOperations;
  private final Emailer emailer;
  private final JWTCreator jwtCreator;

  public AuthProcessorImpl(DSLContext db, Emailer emailer, JWTCreator jwtCreator) {
    this.authDatabaseOperations = new AuthDatabaseOperations(db);
    this.emailer = emailer;
    this.jwtCreator = jwtCreator;
  }

  /**
   * Check that inputs are valid with the database Creates a new refresh jwt Creates a new access
   * jwt Creates a new user database row Return the new jwts
   *
   * @throws EmailAlreadyInUseException if the given email is already used.
   */
  @Override
  public SessionResponse signUp(NewUserRequest request) {
    UsersRecord user =
        authDatabaseOperations.createNewUser(
            request.getEmail(),
            request.getPassword(),
            request.getFirstName(),
            request.getLastName());

    emailer.sendWelcomeEmail(
        request.getEmail(), AuthDatabaseOperations.getFullName(user.into(Users.class)));

    return setupSessionResponse(request.getEmail());
  }

  /**
   * Checks if username password combination is valid with database Creates a new refresh jwt
   * Creates a new access jwt Return the access and refresh jwts
   *
   * @throws AuthException if the given username password combination is invalid.
   */
  @Override
  public SessionResponse login(LoginRequest loginRequest) throws AuthException {
    if (authDatabaseOperations.isValidLogin(loginRequest.getEmail(), loginRequest.getPassword())) {
      return setupSessionResponse(loginRequest.getEmail());
    } else {
      throw new AuthException("Could not validate username password combination");
    }
  }

  /** Add refresh jwt to the blacklist token database table */
  @Override
  public void logout(String refreshToken) {
    authDatabaseOperations.addToBlackList(getSignature(refreshToken));
  }

  /**
   * Checks if refresh jwt is valid Checks if refresh jwt is blacklisted *uses database Creates a
   * new access jwt Returns the access jwt
   *
   * @throws AuthException if the given refresh token is invalid.
   */
  @Override
  public RefreshSessionResponse refreshSession(RefreshSessionRequest request) throws AuthException {
    if (authDatabaseOperations.isOnBlackList(getSignature(request.getRefreshToken()))) {
      throw new AuthException("The refresh token has been invalidated by a previous logout");
    }

    Optional<String> accessToken = jwtCreator.getNewAccessToken(request.getRefreshToken());

    if (accessToken.isPresent()) {
      return new RefreshSessionResponse() {
        {
          setFreshAccessToken(accessToken.get());
        }
      };
    } else {
      throw new TokenInvalidException("refresh");
    }
  }

  /**
   * Get the user associated with the email. Invalidate any outstanding requests Create and log a
   * secret key associated with the user Send an email to the user with the secret key in a url.
   */
  @Override
  public void requestPasswordReset(ForgotPasswordRequest request) {
    String email = request.getEmail();
    JWTData userData = authDatabaseOperations.getUserJWTData(email);

    String token =
        authDatabaseOperations.createSecretKey(
            userData.getUserId(), VerificationKeyType.FORGOT_PASSWORD);

    Users user = authDatabaseOperations.getUserPojo(userData.getUserId());
    emailer.sendPasswordChangeRequestEmail(email, AuthDatabaseOperations.getFullName(user), token);
  }

  /**
   * Check for an existing secret key that matches the request Make sure the key is valid (time
   * constraint, and not used) Get the user associated with the key Update the user's password
   * Update the key to be used
   */
  @Override
  public void resetPassword(ResetPasswordRequest request) {
    UsersRecord user =
        authDatabaseOperations.validateSecretKey(
            request.getSecretKey(), VerificationKeyType.FORGOT_PASSWORD);

    user.setPasswordHash(Passwords.createHash(request.getNewPassword()));
    user.store();

    emailer.sendPasswordChangeConfirmationEmail(
        user.getEmail(), AuthDatabaseOperations.getFullName(user.into(Users.class)));
  }

  @Override
  public void verifyEmail(String secretKey) {
    UsersRecord user =
        authDatabaseOperations.validateSecretKey(secretKey, VerificationKeyType.VERIFY_EMAIL);

    user.setEmailVerified(true);
    user.store();
  }

  /**
   * Given a valid user's email, get a corresponding refresh and access token and return them as a
   * SessionResponse object.
   */
  private SessionResponse setupSessionResponse(String email) {
    JWTData userData = authDatabaseOperations.getUserJWTData(email);
    String refreshToken = jwtCreator.createNewRefreshToken(userData);
    Optional<String> accessToken = jwtCreator.getNewAccessToken(refreshToken);

    if (accessToken.isPresent()) {
      return new SessionResponse() {
        {
          setAccessToken(accessToken.get());
          setRefreshToken(refreshToken);
        }
      };
    } else {
      // If this is thrown there is probably an error in our JWT creation / validation logic
      throw new IllegalStateException("Newly created refresh token was deemed invalid");
    }
  }

  /**
   * Gets the signature of a given JWT string. Will be the third segment of a JWT that is
   * partitioned by "." characters.
   */
  private String getSignature(String token) {
    return token.split("\\.")[2];
  }
}
