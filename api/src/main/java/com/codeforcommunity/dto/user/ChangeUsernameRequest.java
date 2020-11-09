package com.codeforcommunity.dto.user;

import com.codeforcommunity.dto.ApiDto;
import java.util.ArrayList;
import java.util.List;

public class ChangeUsernameRequest extends ApiDto {
  private String newUsername;
  private String password;

  public ChangeUsernameRequest(String newUsername, String password) {
    this.newUsername = newUsername;
    this.password = password;
  }

  private ChangeUsernameRequest() {}

  public String getNewUsername() {
    return newUsername;
  }

  public void setNewUsername(String newUsername) {
    this.newUsername = newUsername;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) {
    String fieldName = fieldPrefix + "change username request";
    List<String> fields = new ArrayList<>();

    if (isEmpty(newUsername)) {
      fields.add(fieldName + " username");
    }

    if (password == null) {
      fields.add(fieldName + " password");
    }
    return fields;
  }
}
