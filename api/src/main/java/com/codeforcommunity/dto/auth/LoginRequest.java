package com.codeforcommunity.dto.auth;

import com.codeforcommunity.api.ApiDto;
import com.codeforcommunity.dto.IDTO;
import java.util.ArrayList;
import java.util.List;

public class LoginRequest extends ApiDto implements IDTO {

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public List<String> validateFields(String fieldPrefix) {
        String fieldName = fieldPrefix + "login_request.";
        List<String> fields = new ArrayList<>();

        if (isEmpty(username)) {
            fields.add(fieldName + "username");
        }
        if (password == null) {
            fields.add(fieldName + "password");
        }
        return fields;
    }
}
