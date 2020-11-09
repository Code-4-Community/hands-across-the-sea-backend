package com.codeforcommunity.dto.auth;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.InvalidPasswordException;
import java.util.ArrayList;
import java.util.List;

public class NewUserRequest extends ApiDto {

  private String email;
  private String password;
  private String firstName;
  private String lastName;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
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
    if (isEmpty(firstName)) {
      fields.add(fieldName + "first_name");
    }
    if (isEmpty(lastName)) {
      fields.add(fieldName + "last_name");
    }
    if (password == null) {
      fields.add(fieldName + "password");
    }
    // Only throw this exception if there are no issues with other fields
    if (passwordInvalid(password) && fields.size() == 0) {
      throw new InvalidPasswordException();
    }
    return fields;
  }
}
