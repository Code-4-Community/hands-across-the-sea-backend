package com.codeforcommunity.auth;

import java.util.Optional;

public class JWTCreator {
  private final JWTHandler handler;

  public JWTCreator(JWTHandler handler) {
    this.handler = handler;
  }

  public String createNewRefreshToken(JWTData userData) {
    return handler.createNewRefreshToken(userData);
  }

  public Optional<String> getNewAccessToken(String refreshToken) {
    return handler.getNewAccessToken(refreshToken);
  }
}
