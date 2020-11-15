package com.codeforcommunity.auth;

import java.util.Optional;

public class JWTAuthorizer {
  private final JWTHandler handler;

  public JWTAuthorizer(JWTHandler handler) {
    this.handler = handler;
  }

  public Optional<JWTData> checkTokenAndGetData(String accessToken) {
    return handler.checkTokenAndGetData(accessToken);
  }
}
