package com.codeforcommunity.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import com.codeforcommunity.enums.PrivilegeLevel;
import com.codeforcommunity.propertiesLoader.PropertiesLoader;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

public class JWTHandler {

  private static final String C4C_ISSUER = "c4c";

  private final Algorithm algorithm;
  private final Verification verification;
  private final Long MS_REFRESH_EXPIRATION;
  private final Long MS_ACCESS_EXPIRATION;

  public JWTHandler(String secretKey) {
    this.algorithm = Algorithm.HMAC256(secretKey);
    this.verification = getDefaultClaimVerification(this.algorithm);

    this.MS_REFRESH_EXPIRATION =
        Long.valueOf(PropertiesLoader.loadProperty("expiration_ms_refresh"));
    this.MS_ACCESS_EXPIRATION = Long.valueOf(PropertiesLoader.loadProperty("expiration_ms_access"));
  }

  /**
   * Given a jwt token, if the token is valid return its data otherwise return an empty optional.
   */
  public Optional<JWTData> checkTokenAndGetData(String token) {
    if (isAuthorized(token)) {
      return Optional.of(getJWTDataFromToken(token));
    } else {
      return Optional.empty();
    }
  }

  /** Generate a new refresh token that stores the given JWTData object's information. */
  public String createNewRefreshToken(JWTData jwtData) {
    return createToken(true, jwtData);
  }

  /** Create a new access token from the given refresh token. */
  public Optional<String> getNewAccessToken(String refreshToken) {
    if (isAuthorized(refreshToken)) {
      JWTData refreshTokenData = getJWTDataFromToken(refreshToken);
      return Optional.of(createToken(false, refreshTokenData));
    } else {
      return Optional.empty();
    }
  }

  /**
   * Verifies that given access token is unedited and unexpired. Also will confirm any claims
   * defined in this.getDefaultClaimVerification().
   *
   * @param accessToken token to be validated
   * @return true if and only if all conforms to all of said conditions.
   */
  private boolean isAuthorized(String accessToken) {
    try {
      getDecodedJWT(accessToken);
      return true;
    } catch (JWTVerificationException exception) {
      return false;
    }
  }

  /** Get the stored information in the given jwt string. */
  private JWTData getJWTDataFromToken(String token) {
    DecodedJWT decodedJWT = getDecodedJWT(token);
    int userId = decodedJWT.getClaim("userId").asInt();
    PrivilegeLevel privilegeLevel =
        PrivilegeLevel.from(decodedJWT.getClaim("privilegeLevel").asString());
    return new JWTData(userId, privilegeLevel);
  }

  private DecodedJWT getDecodedJWT(String jwt) throws JWTVerificationException {
    if (jwt == null) {
      throw new JWTVerificationException("Given a null JWT String");
    }
    return verification.build().verify(jwt);
  }

  private Date getTokenExpiration(boolean isRefresh) {
    long exp = isRefresh ? MS_REFRESH_EXPIRATION : MS_ACCESS_EXPIRATION;
    return Date.from(Instant.now().plusMillis(exp));
  }

  private String createToken(boolean isRefresh, JWTData jwtData) {
    Date date = getTokenExpiration(isRefresh);
    return JWT.create()
        .withClaim("userId", jwtData.getUserId())
        .withClaim("privilegeLevel", jwtData.getPrivilegeLevel().getName())
        .withExpiresAt(date)
        .withIssuer(C4C_ISSUER)
        .sign(algorithm);
  }

  /**
   * Create verification object that ensures all default claims we have decided should be in every
   * token are present.
   *
   * @return verification object.
   */
  private static Verification getDefaultClaimVerification(Algorithm algorithm) {
    return JWT.require(algorithm).withIssuer(C4C_ISSUER);
  }
}
