package com.codeforcommunity.dto;

public class ClientErrorResponse {

    private String status = "BAD REQUEST";
    private String reason;

    public ClientErrorResponse(String reason) {
        this.reason = reason;
    }
}
