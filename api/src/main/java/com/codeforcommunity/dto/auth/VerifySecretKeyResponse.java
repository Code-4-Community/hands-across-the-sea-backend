package com.codeforcommunity.dto.auth;

import com.codeforcommunity.dto.IDTO;

public class VerifySecretKeyResponse implements IDTO {
    private int userId;

    private String message;

    public int getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public VerifySecretKeyResponse setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public VerifySecretKeyResponse setMessage(String message) {
        this.message = message;
        return this;
    }
}
