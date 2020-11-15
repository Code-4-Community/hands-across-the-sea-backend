package com.codeforcommunity.enums;

public enum PrivilegeLevel {
  STANDARD("standard"),
  ADMIN("admin");

  private String name;

  PrivilegeLevel(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static PrivilegeLevel from(String name) {
    for (PrivilegeLevel privilegeLevel : PrivilegeLevel.values()) {
      if (privilegeLevel.name.equals(name)) {
        return privilegeLevel;
      }
    }
    throw new IllegalArgumentException(
        String.format("Given name `%s` doesn't correspond to any `PrivilegeLevel`", name));
  }
}
