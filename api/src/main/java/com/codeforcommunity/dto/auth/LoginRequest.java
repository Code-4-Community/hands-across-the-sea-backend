package com.codeforcommunity.dto.auth;

import com.codeforcommunity.dto.ApiDto;
import java.util.ArrayList;
import java.util.List;

public class LoginRequest extends ApiDto {

  private String email;
  private String password;

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

  @Override
  public List<String> validateFields(String fieldPrefix) {
    List<String> fields = new ArrayList<>();
    if (emailInvalid(email)) {
      fields.add("email");
    }
    if (password == null || password.trim().isEmpty()) {
      fields.add("password");
    }
    return fields;
  }
}
