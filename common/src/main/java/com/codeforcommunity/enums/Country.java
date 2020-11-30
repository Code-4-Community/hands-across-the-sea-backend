package com.codeforcommunity.enums;

public enum Country {
  ANTIGUA_AND_BARBUDA("antigua_and_barbuda"),
  DOMINICA("dominica"),
  GRENADA("grenada"),
  ST_KITTS_AND_NEVIS("st_kitts_and_nevis"),
  ST_LUCIA("st_lucia"),
  ST_VINCENT_AND_THE_GRENADINES("st_vincent_and_the_grenadines");

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
