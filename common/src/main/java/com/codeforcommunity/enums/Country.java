package com.codeforcommunity.enums;

public enum Country {
  UNITED_STATES("UNITED_STATES"),
  ANTIGUA_AND_BARBUDA("ANTIGUA_AND_BARBUDA"),
  DOMINICA("DOMINICA"),
  GRENADA("GRENADA"),
  ST_KITTS_AND_NEVIS("ST_KITTS_AND_NEVIS"),
  ST_LUCIA("ST_LUCIA"),
  ST_VINCENT_AND_THE_GRENADINES("ST_VINCENT_AND_THE_GRENADINES");

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
        String.format("Given name `%s` doesn't correspond to any `Country`", name));
  }
}
