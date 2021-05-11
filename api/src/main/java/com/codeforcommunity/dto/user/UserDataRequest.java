package com.codeforcommunity.dto.user;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.enums.Country;
import com.codeforcommunity.enums.PrivilegeLevel;
import com.codeforcommunity.exceptions.HandledException;
import java.util.ArrayList;
import java.util.List;

public class UserDataRequest extends ApiDto {

  private Country country;
  private PrivilegeLevel privilegeLevel;

  public UserDataRequest(Country country, PrivilegeLevel privilegeLevel) {
    this.country = country;
    this.privilegeLevel = privilegeLevel;
  }

  public Country getCountry() {
    return country;
  }

  public PrivilegeLevel getPrivilegeLevel() {
    return privilegeLevel;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    String fieldName = fieldPrefix + "update_user_data_request.";
    List<String> fields = new ArrayList<>();

    if (privilegeLevel == null) {
      fields.add(fieldName + "privilege_level");
    }
    if (country == null) {
      fields.add(fieldName + "country");
    }
    return fields;
  }
}
