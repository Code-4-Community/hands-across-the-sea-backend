package com.codeforcommunity.auth;

import com.codeforcommunity.enums.Country;
import com.codeforcommunity.enums.PrivilegeLevel;

public class JWTData {

  private final Integer userId;
  private final PrivilegeLevel privilegeLevel;
  private final Country country;

  public JWTData(Integer userId, PrivilegeLevel privilegeLevel, Country country) {
    this.userId = userId;
    this.privilegeLevel = privilegeLevel;
    this.country = country;
  }

  public Integer getUserId() {
    return this.userId;
  }

  public PrivilegeLevel getPrivilegeLevel() {
    return this.privilegeLevel;
  }

  public Country getCountry() { return this.country; }

  public boolean isAdmin() {
    return this.privilegeLevel == PrivilegeLevel.ADMIN;
  }

  public boolean isOfficer() {
    return this.privilegeLevel == PrivilegeLevel.OFFICER;
  }

  public boolean isVolunteer() {
    return this.privilegeLevel == PrivilegeLevel.VOLUNTEER;
  }
}
