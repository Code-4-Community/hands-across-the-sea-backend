package com.codeforcommunity.dto.school;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.enums.Country;
import com.codeforcommunity.exceptions.HandledException;
import java.util.ArrayList;
import java.util.List;

public class NewSchoolRequest extends ApiDto {

  private String name;
  private String address;
  private Country country;
  private Boolean hidden;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Country getCountry() {
    return country;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

  public Boolean getHidden() {
    return hidden;
  }

  public void setHidden(Boolean hidden) {
    this.hidden = hidden;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    List<String> fields = new ArrayList<String>();
    if (name == null || name.isEmpty()) {
      fields.add("name");
    }
    if (address == null || address.isEmpty()) {
      fields.add("address");
    }
    if (country == null) {
      fields.add("country");
    }
    if (hidden == null) {
      fields.add("hidden");
    }
    return fields;
  }
}
