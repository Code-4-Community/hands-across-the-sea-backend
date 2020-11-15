package com.codeforcommunity.enums;

public enum VerificationKeyType {
  VERIFY_EMAIL("verify_email"),
  FORGOT_PASSWORD("forgot_password");

  private String name;

  VerificationKeyType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static VerificationKeyType from(String name) {
    for (VerificationKeyType type : VerificationKeyType.values()) {
      if (type.name.equals(name)) {
        return type;
      }
    }
    throw new IllegalArgumentException(
        String.format("Given name (%s) that doesn't correspond to any VerificationKeyType", name));
  }
}
