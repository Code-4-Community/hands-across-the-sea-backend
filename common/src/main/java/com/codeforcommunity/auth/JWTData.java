package com.codeforcommunity.auth;

import com.codeforcommunity.enums.PrivilegeLevel;

public class JWTData {

  private final Long userId;
  private final PrivilegeLevel privilegeLevel;

  public JWTData(Long userId, PrivilegeLevel privilegeLevel) {
    this.userId = userId;
    this.privilegeLevel = privilegeLevel;
  }

  public Long getUserId() {
    return this.userId;
  }

  public PrivilegeLevel getPrivilegeLevel() {
    return this.privilegeLevel;
  }
}
