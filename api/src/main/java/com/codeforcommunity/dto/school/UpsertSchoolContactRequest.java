package com.codeforcommunity.dto.school;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.util.ArrayList;
import java.util.List;

public class UpsertSchoolContactRequest extends ApiDto {

  private String name;
  private String email;
  private String address;
  private String phone;

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

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    List<String> fields = new ArrayList<String>();
    if (name == null || name.isEmpty()) {
      fields.add("name");
    }
    if (email == null) {
      fields.add("email");
    }
    if (address == null || address.isEmpty()) {
      fields.add("address");
    }
    if (phone == null) {
      fields.add("phone");
    }
    return fields;
  }
}
