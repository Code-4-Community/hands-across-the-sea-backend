package com.codeforcommunity.auth.JWT.db;

public interface AuthDataBase {

    boolean validateUser(String user, String pass);

    boolean newUser(String username, String email, String password, String firstName, String lastName); //todo investigate the best way to store passwords

    public boolean registerRefresh(String signature, String username);

    //todo add logout method and investigate how to do it

}