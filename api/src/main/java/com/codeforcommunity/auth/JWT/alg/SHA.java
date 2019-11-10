package com.codeforcommunity.auth.JWT.alg;

import com.codeforcommunity.auth.JWT.Statics;
import com.codeforcommunity.auth.exceptions.AuthException;
import com.codeforcommunity.utils.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class SHA implements Statics {

    private Base64.Encoder en = Base64.getEncoder();
    private Base64.Decoder de = Base64.getDecoder();
    SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), alg);
    Mac mac;

    public SHA() throws AuthException {
        try {
            this.mac = Mac.getInstance(alg);
            mac.init(signingKey);
        } catch (Exception e) {
            Logger.log("error creating sha");
            throw new AuthException(e.getMessage());
        }
    }

    public String encode64(String s, boolean padded) {
        byte[] b = s.getBytes();
        String ret;
        if (padded) {
            ret = en.encodeToString(b);
        } else {
            ret = en.withoutPadding().encodeToString(b);
        }
        return ret;
    }

    public String decode64(String s) {

        return new String(de.decode(s.getBytes()));

    }

    public String hash(String s) {

        return toHexString(mac.doFinal(s.getBytes()));

    }

}
