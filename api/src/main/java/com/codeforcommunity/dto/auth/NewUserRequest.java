package com.codeforcommunity.dto.auth;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.enums.Country;
import com.codeforcommunity.enums.PrivilegeLevel;
import com.codeforcommunity.exceptions.InvalidPasswordException;
import java.util.ArrayList;
import java.util.List;

public class NewUserRequest extends ApiDto {

  private String email;
  private String password;
  private String firstName;
  private String lastName;
  private PrivilegeLevel privilegeLevel;
  private Country country;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Country getCountry() {
    return country;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

  public PrivilegeLevel getPrivilegeLevel() {
    return privilegeLevel;
  }

  public void setPrivilegeLevel(PrivilegeLevel privilegeLevel) {
    this.privilegeLevel = privilegeLevel;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) {
    List<String> invalidFields = new ArrayList<>();
    if (emailInvalid(email)) {
      invalidFields.add("email");
    }
    if (isEmpty(firstName)) {
      invalidFields.add("first_name");
    }
    if (isEmpty(lastName)) {
      invalidFields.add("last_name");
    }
    if (isEmpty(password)) {
      invalidFields.add("password");
    }
    if (country == null) {
      invalidFields.add("country");
    }
    if (privilegeLevel == null) {
      invalidFields.add("privilegeLevel");
    }
    // Only throw this exception if there are no issues with other fields
    if (passwordInvalid(password) && invalidFields.isEmpty()) {
      throw new InvalidPasswordException();
    }

    return invalidFields;
  }
}
