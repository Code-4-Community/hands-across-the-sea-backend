package com.codeforcommunity.auth;

public interface IAuthDatabase {

    boolean isValidLogin(String user, String pass);

    boolean newUser(String username, String email, String password, String firstName, String lastName);

    boolean recordNewRefreshToken(String signature, String username);

    boolean invalidateRefresh(String signature);

    boolean isValidRefresh(String signature);

    boolean validateSecretKey(String secretKey);
}
