package com.codeforcommunity.auth.DTO;

public class RefreshSessionResponse implements IDTO {

    private String freshAccessToken;

    public String getFreshAccessToken() {
        return freshAccessToken;
    }

    public void setFreshAccessToken(String freshAccessToken) {
        this.freshAccessToken = freshAccessToken;
    }
}
