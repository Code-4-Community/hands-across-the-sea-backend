package com.codeforcommunity.dto.school;

import com.codeforcommunity.enums.Country;

public class School {

  private Long id;
  private String name;
  private String address;
  private Country country;

  public School(Long id, String name, String address, Country country) {
    this.id = id;
    this.name = name;
    this.address = address;
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

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Country getCountry() {
    return country;
  }

  public void setCountry(Country country) {
    this.country = country;
  }
}
