package com.codeforcommunity.enums;

public enum TimeRole {
  PART_TIME("part_time"),
  FULL_TIME("full_time"),
  NONE("None");

  private String name;

  TimeRole(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static TimeRole from(String name) {
    for (TimeRole timeRole : TimeRole.values()) {
      if (timeRole.name.equals(name)) {
        return timeRole;
      }
    }
    throw new IllegalArgumentException(
        String.format("Given name `%s` doesn't correspond to any `TimeRole`", name));
  }
}
