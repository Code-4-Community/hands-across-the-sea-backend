package com.codeforcommunity.dto.auth;

import com.codeforcommunity.dto.ApiDto;
import java.util.ArrayList;
import java.util.List;

public class RefreshSessionRequest extends ApiDto {
  private String refreshToken;

  public RefreshSessionRequest(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) {
    String fieldName = fieldPrefix + "refresh_session_request.";
    List<String> fields = new ArrayList<>();

    if (isEmpty(refreshToken) || refreshToken.split("\\.").length < 3) {
      fields.add(fieldName + "refresh_token");
    }
    return fields;
  }
}
