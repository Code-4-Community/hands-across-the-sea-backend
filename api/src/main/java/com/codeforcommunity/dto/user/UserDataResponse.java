package com.codeforcommunity.dto.user;

import com.codeforcommunity.enums.Country;
import com.codeforcommunity.enums.PrivilegeLevel;

public class UserDataResponse {

  private String firstName;
  private String lastName;
  private String email;
  private Integer id;
  private Country country;
  private PrivilegeLevel privilegeLevel;
  private Boolean isDisabled;

  public UserDataResponse(
      String firstName,
      String lastName,
      Integer id,
      String email,
      Country country,
      PrivilegeLevel privilegeLevel,
      Boolean disabled) {
    this.email = email;
    this.firstName = firstName;
    this.id = id;
    this.lastName = lastName;
    this.country = country;
    this.privilegeLevel = privilegeLevel;
    this.isDisabled = disabled;
  }

  public UserDataResponse(
      String firstName,
      String lastName,
      Integer id,
      String email,
      Country country,
      Boolean disabled) {
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.id = id;
    this.country = country;
    this.isDisabled = disabled;
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

  public Integer getId() {
    return this.id;
  }

  public Boolean getDisabled() {
    return this.isDisabled;
  }

  public PrivilegeLevel getPrivilegeLevel() {
    return privilegeLevel;
  }
}
