package com.codeforcommunity.enums;

public enum LibraryStatus {
  EXISTS("exists"),
  DOES_NOT_EXIST("does_not_exist"),
  UNKNOWN("unknown");

  private String name;

  LibraryStatus(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static LibraryStatus from(String name) {
    for (LibraryStatus status : LibraryStatus.values()) {
      if (status.name.equals(name)) {
        return status;
      }
    }
    throw new IllegalArgumentException(
        String.format("Given name `%s` doesn't correspond to any `LibraryStatus`", name));
  }
}
