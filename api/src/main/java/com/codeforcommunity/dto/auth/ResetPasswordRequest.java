package com.codeforcommunity.dto.auth;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.InvalidPasswordException;
import java.util.ArrayList;
import java.util.List;

public class ResetPasswordRequest extends ApiDto {

  private String secretKey;
  private String newPassword;

  public ResetPasswordRequest(String secretKey, String newPassword) {
    this.secretKey = secretKey;
    this.newPassword = newPassword;
  }

  private ResetPasswordRequest() {}

  public String getSecretKey() {
    return secretKey;
  }

  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) {
    String fieldName = fieldPrefix + "reset_password_request.";
    List<String> fields = new ArrayList<>();

    if (newPassword == null) {
      fields.add(fieldName + "new_password");
    }
    if (secretKey == null) {
      fields.add(fieldName + "secret_key");
    }
    // Only throw this exception if there are no issues with other fields
    if (passwordInvalid(newPassword) && fields.size() == 0) {
      throw new InvalidPasswordException();
    }
    return fields;
  }
}
