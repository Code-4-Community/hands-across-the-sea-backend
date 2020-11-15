package com.codeforcommunity.dto.auth;

public class RefreshSessionResponse {

  private String freshAccessToken;

  public String getFreshAccessToken() {
    return freshAccessToken;
  }

  public void setFreshAccessToken(String freshAccessToken) {
    this.freshAccessToken = freshAccessToken;
  }
}
