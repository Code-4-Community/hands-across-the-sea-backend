package com.codeforcommunity.dto.school;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.enums.Country;
import com.codeforcommunity.enums.LibraryStatus;
import com.codeforcommunity.exceptions.HandledException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder.In;

public class UpsertSchoolRequest extends ApiDto {

  private String name;
  private String address;
  private String email;
  private String phone;
  private String notes;
  private String area;
  private Country country;
  private Boolean hidden;
  private LibraryStatus libraryStatus;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

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

  public LibraryStatus getLibraryStatus() {
    return libraryStatus;
  }

  public void setLibraryStatus(LibraryStatus libraryStatus) {
    this.libraryStatus = libraryStatus;
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
    if (libraryStatus == null) {
      fields.add("libraryStatus");
    }
    return fields;
  }
}
