package com.codeforcommunity.dto.school;

import com.codeforcommunity.enums.ContactType;

public class SchoolContact {

  private Integer id;
  private Integer schoolId;
  private String firstName;
  private String lastName;
  private String email;
  private String address;
  private String phone;
  private ContactType type;

  public SchoolContact(
      Integer id,
      Integer schoolId,
      String firstName,
      String lastName,
      String email,
      String address,
      String phone,
      ContactType type) {
    this.id = id;
    this.schoolId = schoolId;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.address = address;
    this.phone = phone;
    this.type = type;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getSchoolId() {
    return schoolId;
  }

  public void setSchoolId(Integer schoolId) {
    this.schoolId = schoolId;
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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public ContactType getType() {
    return type;
  }

  public void setType(ContactType type) {
    this.type = type;
  }
}
