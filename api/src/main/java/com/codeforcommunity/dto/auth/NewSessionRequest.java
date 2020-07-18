package com.codeforcommunity.dto.auth;

import com.codeforcommunity.api.ApiDto;
import com.codeforcommunity.dto.IDTO;
import java.util.ArrayList;
import java.util.List;

public class NewSessionRequest extends ApiDto implements IDTO {
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
