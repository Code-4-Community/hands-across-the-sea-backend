package com.codeforcommunity.auth;

public class JWTAuthorizer {
  private final JWTHandler handler;

  public JWTAuthorizer(JWTHandler handler) {
    this.handler = handler;
  }

  public boolean isAuthorized(String accessToken) {
    return handler.isAuthorized(accessToken);
  }

}
