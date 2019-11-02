package com.codeforcommunity.auth.JWT.validation;

public interface AuthTokenValidator {

    boolean valid(String token) throws Exception;

}
