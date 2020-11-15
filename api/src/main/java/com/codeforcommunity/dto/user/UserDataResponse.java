package com.codeforcommunity.dto.user;

public class UserDataResponse {

  private String firstName;
  private String lastName;
  private String email;

  public UserDataResponse(String firstName, String lastName, String email) {
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
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
}
