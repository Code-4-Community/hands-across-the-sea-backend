package com.codeforcommunity.dto.school;

import com.codeforcommunity.enums.Country;

import java.util.ArrayList;
import java.util.List;

public class School {

  private Long id;
  private String name;
  private String address;
  private Country country;
  private Boolean hidden;
  private List<SchoolContact> contacts;

  public School(Long id, String name, String address, Country country, Boolean hidden) {
    this.id = id;
    this.name = name;
    this.address = address;
    this.country = country;
    this.hidden = hidden;
    this.contacts = new ArrayList<SchoolContact>();
  }

  public School(Long id, String name, String address, Country country, Boolean hidden, List<SchoolContact> contacts) {
    this.id = id;
    this.name = name;
    this.address = address;
    this.country = country;
    this.hidden = hidden;
    this.contacts = contacts;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public List<SchoolContact> getContacts() {
    return contacts;
  }

  public void setContacts(List<SchoolContact> contacts) {
    this.contacts = contacts;
  }
}
