package com.codeforcommunity.api;

public interface IAuthProcessor { //todo rename

    /**
     * Must generate 2 JWT's returned in an array of String of length 2.
     * 
     * @param credentials
     * @return 
     * String[0] must correspond to access token.
     * String[1] must correspond to refresh token.
     * @throws Exception
     */
    String[] login(String credentials) throws Exception; //todo we need an object as a parameter insead of string

    boolean validate(String token) throws Exception;

    String refresh(String accessToken) throws Exception;

}
