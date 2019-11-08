package com.codeforcommunity.auth.JWT;

import java.util.Formatter;

public interface Statics { //todo work on best place to put this so it doesn't have to be public

    String secret = "secret";
    String alg = "HmacSHA1";
    String type = "jwt";

    String issuer = "c4c_admin";

    // 30 min
    long access_exp = 1800000;
    // 7 days 
    long refresh_exp = 604800000; 

    default String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }
}
