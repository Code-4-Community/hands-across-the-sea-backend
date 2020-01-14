package com.codeforcommunity.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;

import java.time.Instant;
import java.util.Date;

public class JWTHandler {

  private static final String C4C_ISSUER = "c4c";

  private final Algorithm algorithm;
  private Verification verification;

  public JWTHandler(String secretKey) {
    this.algorithm = Algorithm.HMAC256(secretKey);
    this.verification = getDefaultClaimVerification(this.algorithm);
  }

  /**
   * Verifies that given access token is unedited and unexpired. Also will confirm any claims defined in
   * @code this.getDefaultClaimVerification().
   * @param accessToken token to be validated
   * @return true if and only if all conforms to all of said conditions.
   */
  public boolean isAuthorized(String accessToken) {
    try {
      getDecodedJWT(accessToken);
      return true;
    } catch (JWTVerificationException exception) {
      return false;
    }
  }

  public String createNewRefreshToken(String username) {
    return createToken(true, username);
  }

  public String getNewAccessToken(String refreshToken) {
    if(isAuthorized(refreshToken)) {
      String username = getDecodedJWT(refreshToken).getClaim("username").asString();
      return createToken(false, username);
    } else {
      throw new IllegalArgumentException("invalid refresh token"); //TODO make auth exception
    }
  }

  private DecodedJWT getDecodedJWT(String jwt) throws JWTVerificationException {
    if (jwt == null) {
      throw new JWTVerificationException("Given a null jwt string");
    }
    return verification.build().verify(jwt);
  }

  private Date getTokenExpiration(boolean isRefresh) {
    long exp = isRefresh ? AuthUtils.refresh_exp : AuthUtils.access_exp;
    return Date.from(Instant.now().plusMillis(exp));
  }

  private String createToken(boolean isRefresh, String username) {
    Date date = getTokenExpiration(isRefresh);
    return JWT.create()
        .withClaim("username", username)
        .withExpiresAt(date)
        .withIssuer(C4C_ISSUER)
        .sign(algorithm);
  }

  /**
   * Create verification object that ensures all default claims we have decided should be in every token are present.
   * @return verification object.
   */
  private static Verification getDefaultClaimVerification(Algorithm algorithm) {
    return JWT.require(algorithm).withIssuer(C4C_ISSUER);
  }
}
