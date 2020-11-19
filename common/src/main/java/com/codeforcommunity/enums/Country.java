package com.codeforcommunity.enums;

public enum Country {
  COUNTRY_ONE("one"),
  COUNTRY_TWO("two"),
  COUNTRY_THREE("three"),
  COUNTRY_FOUR("four"),
  COUNTRY_FIVE("five"),
  COUNTRY_SIX("six");

  private String name;

  Country(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static Country from(String name) {
    for (Country country : Country.values()) {
      if (country.name.equals(name)) {
        return country;
      }
    }
    throw new IllegalArgumentException(
        String.format("Given name `%s` doesn't correspond to any `PrivilegeLevel`", name));
  }
}
