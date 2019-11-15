package com.codeforcommunity.auth.JWT.validation;
import com.codeforcommunity.auth.JWT.alg.SHA;
import com.codeforcommunity.logger.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
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

        Logger.log("unchanged: " + unchanged(token));
        Logger.log("unexpired: " + unexpired(token));

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

        Instant tokenExpiration = Instant.parse(bodyMap.get("expiration"));

        Instant now = Instant.now();

        return tokenExpiration.isAfter(now);

    }



}
