package com.codeforcommunity.api;

public interface IAuthProcessor { //todo rename

    /**
     * Must generate 2 JWT's returned in an array of String of length 2.
     * 
     * @param credentials
     * @return 
     * String[0] must correspond to access token.
     * String[1] must correspond to getNewAccessToken token.
     * @throws Exception
     */
    String[] getNewUserSession(String credentials) throws Exception; //todo we to parse json upstream and

    boolean authenticateUser(String token) throws Exception;

    String getNewAccessToken(String refreshToken) throws Exception;

    boolean invalidateUserSession(String refreshToken);

    boolean newUser(String username, String email, String password, String firstName, String lastName);

}
