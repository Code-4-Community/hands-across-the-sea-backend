package com.codeforcommunity.auth;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Verification;

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
      verification.build().verify(accessToken);
      return true;
    } catch (JWTVerificationException exception) {
      return false;
    }
  }





  /**
   * Create verification object that ensures all default claims we have decided should be in every token are present.
   * @return verification object.
   */
  private static Verification getDefaultClaimVerification(Algorithm algorithm) {
    return JWT.require(algorithm).withIssuer(C4C_ISSUER);
  }
}
