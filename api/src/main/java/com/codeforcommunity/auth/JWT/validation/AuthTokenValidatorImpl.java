package com.codeforcommunity.auth.JWT.validation;
import com.codeforcommunity.auth.JWT.alg.SHA;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AuthTokenValidatorImpl implements AuthTokenValidator {

    private SHA sha = new SHA();
    private ObjectMapper mapper = new ObjectMapper();

    public AuthTokenValidatorImpl() throws Exception { //todo handle this exception

    }
    @Override
    public boolean valid(String token) throws Exception {

         return unchanged(token) && unexpired(token);

    }

    private boolean unchanged(String token) throws Exception {
        String[] parts = token.split("\\.");

        return sha.hash(parts[0] + "." + parts[1]).equals(parts[2]);
    }

    private boolean unexpired(String token) throws Exception { //todo implement

        String[] tokenArr = token.split("\\.");
        String header = sha.decode64(tokenArr[0]);
        String body = sha.decode64(tokenArr[1]);

        Map<String, String> bodyMap = mapper.readValue(body, HashMap.class);

        Date d = new Date(bodyMap.get("expiration"));

        Date now = new Date();

        return now.getTime() < d.getTime();

    }
}
