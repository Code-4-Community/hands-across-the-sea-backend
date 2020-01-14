package com.codeforcommunity.auth;

public class JWTCreator {
  private final JWTHandler handler;

  public JWTCreator(JWTHandler handler) {
    this.handler = handler;
  }

  public String createNewRefreshToken(String username) {
    return handler.createNewRefreshToken(username);
  }

  public String getNewAccessToken(String refreshToken) {
    return handler.getNewAccessToken(refreshToken);
  }


}
