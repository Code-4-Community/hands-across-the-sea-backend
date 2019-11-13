package com.codeforcommunity.auth.JWT.db;

public interface AuthDataBase {

    boolean isValidUser(String user, String pass);

    boolean newUser(String username, String email, String password, String firstName, String lastName); //todo investigate the best way to store passwords

    boolean recordNewRefreshToken(String signature, String username);

    boolean invalidateRefresh(String signature);

    boolean isValidRefresh(String signature);
    //todo add logout method and investigate how to do it

}