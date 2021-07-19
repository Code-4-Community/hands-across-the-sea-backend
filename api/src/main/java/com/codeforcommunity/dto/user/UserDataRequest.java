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
  private String firstName;
  private String lastName;
  private String email;

  public UserDataRequest() {}

  public UserDataRequest(
      Country country,
      PrivilegeLevel privilegeLevel,
      String firstName,
      String lastName,
      String email) {
    this.country = country;
    this.privilegeLevel = privilegeLevel;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
  }

  public Country getCountry() {
    return country;
  }

  public PrivilegeLevel getPrivilegeLevel() {
    return privilegeLevel;
  }

  public String getFirstName() {
    return this.firstName;
  }

  public String getLastName() {
    return this.lastName;
  }

  public String getEmail() {
    return this.email;
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

    if (emailInvalid(this.email)) {
      fields.add(fieldName + "email");
    }
    return fields;
  }
}
