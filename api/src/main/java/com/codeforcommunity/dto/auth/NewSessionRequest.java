package com.codeforcommunity.dto.auth;

import com.codeforcommunity.dto.IDTO;

public class NewSessionRequest implements IDTO {
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
