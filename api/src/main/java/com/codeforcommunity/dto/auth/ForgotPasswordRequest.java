package com.codeforcommunity.dto.auth;

import com.codeforcommunity.dto.ApiDto;
import java.util.ArrayList;
import java.util.List;

public class ForgotPasswordRequest extends ApiDto {

  private String email;

  public ForgotPasswordRequest(String email) {
    this.email = email;
  }

  public ForgotPasswordRequest() {}

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) {
    List<String> fields = new ArrayList<>();
    if (emailInvalid(email)) {
      fields.add("email");
    }
    return fields;
  }
}
