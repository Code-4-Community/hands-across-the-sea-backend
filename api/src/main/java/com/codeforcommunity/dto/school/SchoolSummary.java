package com.codeforcommunity.dto.school;

import com.codeforcommunity.enums.Country;

public class SchoolSummary {

  private Long id;
  private String name;
  private Country country;

  public SchoolSummary(Long id, String name, Country country) {
    this.id = id;
    this.name = name;
    this.country = country;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Country getCountry() {
    return country;
  }

  public void setCountry(Country country) {
    this.country = country;
  }
}
