package com.codeforcommunity.dto.school;

public class SchoolContact {

  private Integer id;
  private Integer schoolId;
  private String name;
  private String email;
  private String address;
  private String phone;

  public SchoolContact(
      Integer id, Integer schoolId, String name, String email, String address, String phone) {
    this.id = id;
    this.schoolId = schoolId;
    this.name = name;
    this.email = email;
    this.address = address;
    this.phone = phone;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
}
