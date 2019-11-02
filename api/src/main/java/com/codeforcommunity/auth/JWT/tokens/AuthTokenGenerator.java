package com.codeforcommunity.auth.JWT.tokens;

import com.codeforcommunity.auth.JWT.Statics;
import com.codeforcommunity.auth.JWT.alg.SHA;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.codeforcommunity.auth.exceptions.AuthException;
import com.codeforcommunity.utils.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AuthTokenGenerator implements Statics { //todo generalize this to list of string fields and expose the ability to parameterize by fields names and values


    private ObjectMapper mapper = new ObjectMapper();
    private SHA sha;
    private Date exp;
    private int access;
    private String username;

    private AuthTokenGenerator(Builder builder) {
        this.exp = createExp(builder.exp);
        this.access = builder.access;
        this.username = builder.username;
    }

    private final Date createExp(long exp) { //todo how do I actually handle timestamps
        Date d = new Date();
        d.setTime(d.getTime() + exp);
        return d;
    }

    private final String get() throws AuthException {
        try {
            return encodeSign(header(), body());
        } catch (Exception e) {
            Logger.log("generating key:" + e.getMessage());
            throw new AuthException(e.getMessage());
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private String header() throws JsonProcessingException {

        Map<String, String> header = new HashMap<String, String>() {{
            put("alg", alg);
            put("type", type);
        }};

        return mapper.writeValueAsString(header);
    }

    private String body() throws JsonProcessingException {

        Map<String, String> body = new HashMap<String, String>() {{
            put("issuer", issuer);
            put("expiration", exp.toString());
            put("username", username);
            put("access", Integer.toString(access));
         }};
        return mapper.writeValueAsString(body);

}

    private String encodeSign(String header, String body) {

        String unsigned = String.format("%s.%s", sha.encode64(header, true), sha.encode64(body, true));
        String signature = sha.hash(unsigned);
        return unsigned + "." + signature;
    }


    public static class Builder {

        private String username;
        private int access;
        private long exp;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder access(int access) {
            this.access = access;
            return this;
        }

        public Builder exp(long exp) {
            this.exp = exp;
            return this;
        }

        public final String getSigned() throws AuthException {
            return new AuthTokenGenerator(this).get();
        }

    }
}


