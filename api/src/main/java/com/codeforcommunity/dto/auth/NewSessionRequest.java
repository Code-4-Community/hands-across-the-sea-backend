package com.codeforcommunity.dto.auth;

import com.codeforcommunity.dto.ApiDto;
import java.util.ArrayList;
import java.util.List;

public class NewSessionRequest extends ApiDto {
  private String username;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) {
    String fieldName = fieldPrefix + "new_session_request.";
    List<String> fields = new ArrayList<>();

    if (isEmpty(username)) {
      fields.add(fieldName + "username");
    }
    return fields;
  }
}
