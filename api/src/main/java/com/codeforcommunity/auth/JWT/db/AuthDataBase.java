package com.codeforcommunity.auth.JWT.db;

public interface AuthDataBase {

    boolean validateUser(String user, String pass);

}