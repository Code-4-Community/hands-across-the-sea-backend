package com.codeforcommunity.processor;

import com.codeforcommunity.api.IAuthProcessor;
import com.codeforcommunity.auth.JWT.Statics;
import com.codeforcommunity.auth.JWT.db.AuthDataBase;
import com.codeforcommunity.auth.JWT.db.AuthDataBaseDemoImpl;
import com.codeforcommunity.auth.JWT.tokens.AuthTokenGenerator;
import com.codeforcommunity.auth.JWT.validation.AuthTokenValidator;
import com.codeforcommunity.auth.JWT.validation.AuthTokenValidatorImpl;
import com.codeforcommunity.auth.exceptions.AuthException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.codeforcommunity.logger.Logger;
import com.codeforcommunity.auth.JWT.alg.SHA;

import java.util.HashMap;
import java.util.Map;

public class AuthProcessorImpl implements IAuthProcessor, Statics { //todo find out the best way to make this an asyschronous verticle

    private ObjectMapper mapper = new ObjectMapper(); //todo make this a singleton pattern
    private AuthTokenValidator validator = new AuthTokenValidatorImpl();
    private AuthDataBase authDataBase = new AuthDataBaseDemoImpl(); //todo this need to take in an instance of this
    private SHA sha = new SHA();

    public AuthProcessorImpl() throws Exception {}

    @Override
    public String[] getNewUserSession(String credentials) throws AuthException { //todo handle this exception

        Map<String, String> creds;

        try {
            Logger.log(credentials);
            creds = mapper.readValue(credentials, HashMap.class);
            Logger.log(creds.get("username"));
            Logger.log(creds.get("password"));
        } catch (Exception e) {
            throw new AuthException("invalid credentials format"); //todo clean this up
        }
        if (authDataBase.isValidUser(creds.getOrDefault("username", null), creds.getOrDefault("password", null))) {
           try {
               String refresh = AuthTokenGenerator.builder().access(0).username(creds.get("username")).exp(refresh_exp).getSigned();
               authDataBase.recordNewRefreshToken(refresh.split("\\.")[2], creds.get("username"));
               return new String[]{
                       AuthTokenGenerator.builder().access(0).username(creds.get("username")).exp(access_exp).getSigned(),
                       refresh,
               };
           } catch (Exception e) {
               return null; //todo handle this
           }
        } else {
            Logger.log("illegal getNewUserSession attempt");
            throw new AuthException("invalid credentials");
        }
    }

    @Override
    public boolean authenticateUser(String token) throws AuthException { //todo abstract common code in auth classes for shaing things
        try {
            return validator.valid(token);
        } catch (Exception e) {
            throw new AuthException(e.getMessage());
        }
    }

    @Override
    public String getNewAccessToken(String refreshToken) throws AuthException{ //todo reimplement this

        try {
            Map<String, String> creds = parseJWTBody(refreshToken);
            if (this.authenticateUser(refreshToken) && (creds.get("username") != null) && authDataBase.isValidRefresh(refreshToken)) {
                return AuthTokenGenerator.builder().access(0).username(creds.get("username")).exp(access_exp).getSigned();
            } else {
                throw new AuthException("Invalid JWT Refresh Token");
            }
        } catch (Exception e) {
            throw new AuthException(e.getMessage());
        }
    }

    @Override
    public boolean invalidateUserSession(String refreshToken) {

        String signature = refreshToken.split("\\.")[2];
        return authDataBase.invalidateRefresh(signature);
    }

    @Override
    public boolean newUser(String username, String email, String password, String firstName, String lastName) {
        return authDataBase.newUser(username, email, password, firstName, lastName);
    }

    private Map<String, String> parseJWTBody(String token) throws AuthException {
        String[] jwt = token.split("\\.");
        Map<String, String> creds;
        try {
            creds = mapper.readValue(sha.decode64(jwt[1]), HashMap.class);
            return creds;
        } catch (Exception e) {
            throw new AuthException("Invalid JWT Refresh Token");
        }
    }
}
