package com.codeforcommunity.dto.auth;

public class RefreshSessionRequest {
  private String refreshToken;

  public RefreshSessionRequest(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }
}
