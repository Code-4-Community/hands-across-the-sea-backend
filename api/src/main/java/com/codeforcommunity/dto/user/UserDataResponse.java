package com.codeforcommunity.dto.user;

import com.codeforcommunity.enums.Country;
import com.codeforcommunity.enums.PrivilegeLevel;

public class UserDataResponse {

  private String firstName;
  private String lastName;
  private String email;
  private Country country;
  private PrivilegeLevel privilegeLevel;

  public UserDataResponse(
      String firstName,
      String lastName,
      String email,
      Country country,
      PrivilegeLevel privilegeLevel) {
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.country = country;
    this.privilegeLevel = privilegeLevel;
  }

  public UserDataResponse(String firstName, String lastName, String email, Country country) {
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.country = country;
  }

  public String getEmail() {
    return email;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public Country getCountry() {
    return country;
  }

  public PrivilegeLevel getPrivilegeLevel() {
    return privilegeLevel;
  }
}
