package com.codeforcommunity.enums;

public enum ContactType {
  PRINCIPAL("principal"),
  LITERACY_COORDINATOR("literacy_coordinator"),
  LIBRARIAN("librarian"),
  OTHER("other");

  private String name;

  ContactType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static ContactType from(String name) {
    for (ContactType contactType : ContactType.values()) {
      if (contactType.name.equals(name)) {
        return contactType;
      }
    }
    throw new IllegalArgumentException(
        String.format("Given name `%s` doesn't correspond to any `ContactType`", name));
  }
}
