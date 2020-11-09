package com.codeforcommunity.dto.user;

import com.codeforcommunity.dto.ApiDto;
import java.util.ArrayList;
import java.util.List;

public class ChangeEmailRequest extends ApiDto {
  private String newEmail;
  private String password;

  public ChangeEmailRequest(String newEmail, String password) {
    this.newEmail = newEmail;
    this.password = password;
  }

  private ChangeEmailRequest() {}

  public String getNewEmail() {
    return newEmail;
  }

  public void setNewEmail(String newEmail) {
    this.newEmail = newEmail;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) {
    String fieldName = fieldPrefix + "change_email_request.";
    List<String> fields = new ArrayList<>();

    if (emailInvalid(newEmail)) {
      fields.add(fieldName + "email");
    }
    if (password == null) {
      fields.add(fieldName + "password");
    }
    return fields;
  }
}
