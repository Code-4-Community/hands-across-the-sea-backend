package com.codeforcommunity.auth;

import com.codeforcommunity.auth.JWT.Statics;
import com.codeforcommunity.auth.JWT.db.AuthDataBase;
import com.codeforcommunity.auth.JWT.db.AuthDataBaseDemoImpl;
import com.codeforcommunity.auth.JWT.tokens.AuthTokenGenerator;
import com.codeforcommunity.auth.JWT.validation.AuthTokenValidator;
import com.codeforcommunity.auth.JWT.validation.AuthTokenValidatorImpl;
import com.codeforcommunity.auth.exceptions.AuthException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.codeforcommunity.utils.Logger;
import com.codeforcommunity.auth.JWT.alg.SHA;

import java.util.HashMap;
import java.util.Map;

public class AuthProcessorImpl implements AuthProcessor, Statics { //todo find out the best way to make this an asyschronous verticle

    private ObjectMapper mapper = new ObjectMapper(); //todo make this a singleton pattern
    private AuthTokenValidator validator = new AuthTokenValidatorImpl();
    private AuthDataBase authDataBase = new AuthDataBaseDemoImpl();
    private SHA sha = new SHA();

    public AuthProcessorImpl() throws Exception {}

    @Override
    public String[] login(String credentials) throws AuthException { //todo handle this exception

        Map<String, String> creds;

        try {
            Logger.log(credentials);
            creds = mapper.readValue(credentials, HashMap.class);
            Logger.log(creds.get("username"));
            Logger.log(creds.get("password"));
        } catch (Exception e) {
            throw new AuthException("invalid credentials format");
        }
        if (authDataBase.validateUser(creds.getOrDefault("username", null), creds.getOrDefault("password", null))) {
           try {
               Logger.log("passed validation");
               Logger.log(AuthTokenGenerator.builder().access(0).username(creds.get("username")).exp(access_exp).getSigned());
               return new String[]{
                       AuthTokenGenerator.builder().access(0).username(creds.get("username")).exp(access_exp).getSigned(),
                       AuthTokenGenerator.builder().access(0).username(creds.get("username")).exp(refresh_exp).getSigned(),
               };
           } catch (Exception e) {
               Logger.log("poop in my but");
               return null; //todo handle this
           }
        } else {
            Logger.log("illegal login attempt");
            throw new AuthException("invalid credentials");
        }
    }

    @Override
    public boolean validate(String token) throws AuthException { //todo abstract common code in auth classes for shaing things
        try {
            return validator.valid(token);
        } catch (Exception e) {
            throw new AuthException(e.getMessage());
        }
    }

    @Override
    public String refresh(String accessToken) throws AuthException{
        try {
            Map<String, String> creds = parseJWTBody(accessToken);
            if (this.validate(accessToken) && (creds.get("username") != null)) {
                return AuthTokenGenerator.builder().access(0).username(creds.get("username")).exp(access_exp).getSigned();
            } else {
                throw new AuthException("Invalid JWT Refresh Token");
            }
        } catch (Exception e) {
            throw new AuthException(e.getMessage());
        }
    }

    private Map<String, String> parseJWTBody(String token) throws AuthException {
        String[] jwt = sha.decode64(token).split("\\.");
        Map<String, String> creds;
        try {
            creds = mapper.readValue(jwt[1], HashMap.class);
            return creds;
        } catch (Exception e) {
            throw new AuthException("Invalid JWT Refresh Token");
        }
    }
}
