package com.codeforcommunity.auth;

import com.codeforcommunity.logger.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Formatter;

public class AuthUtils {

    private String secret = "secret";
    private String alg = "HmacSHA1";
    private SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), alg);
    private Mac mac;

    // 30 min
    public static long access_exp = 1800000;
    // 7 days
    public static long refresh_exp = 604800000;

    public String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    public AuthUtils() throws Exception {
        try {
            this.mac = Mac.getInstance(alg);
            mac.init(signingKey);
        } catch (Exception e) {
            Logger.log("error creating sha");
            throw new Exception();
        }
    }

    public String hash(String s) {

        return toHexString(mac.doFinal(s.getBytes()));

    }


}
