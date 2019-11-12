package com.codeforcommunity.auth.JWT.db;

/**
 * Note this is currently a placeholder class,
 * next step is to implement a database connection that has access to persistant user data
 */
public class AuthDataBaseDemoImpl implements AuthDataBase {

    @Override
    public boolean validateUser(String user, String pass) {
        return user != null && pass != null;
    }
}
