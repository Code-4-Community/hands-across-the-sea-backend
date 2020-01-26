package com.codeforcommunity.exceptions;

public class CreateUserException extends RuntimeException {
  public enum UsedField {
    EMAIL, USERNAME, BOTH
  }

  private UsedField usedField;

  public CreateUserException(UsedField usedField) {
    super();
    this.usedField = usedField;
  }

  public UsedField getUsedField() {
    return usedField;
  }
}
