package com.codeforcommunity.dto.auth;

import com.codeforcommunity.api.ApiDto;
import com.codeforcommunity.dto.IDTO;
import java.util.ArrayList;
import java.util.List;

public class NewUserRequest extends ApiDto implements IDTO {

    private String email;
    private String username;
    private String password;
    private String firstName;
    private String lastName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public List<String> validateFields(String fieldPrefix) {
        String fieldName = fieldPrefix + "new_user_request.";
        List<String> fields = new ArrayList<>();

        if (emailInvalid(email)) {
            fields.add(fieldName + "email");
        }
        if (isEmpty(username)) {
            fields.add(fieldName + "username");
        }
        if (passwordInvalid(password)) {
            fields.add(fieldName + "password");
        }
        if (isEmpty(firstName)) {
            fields.add(fieldName + "first_name");
        }
        if (isEmpty(lastName)) {
            fields.add(fieldName + "last_name");
        }
        return fields;
    }
}
