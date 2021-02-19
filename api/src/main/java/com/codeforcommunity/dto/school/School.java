package com.codeforcommunity.dto.school;

import com.codeforcommunity.enums.Country;
import com.codeforcommunity.enums.LibraryStatus;
import java.util.ArrayList;
import java.util.List;

public class School {

  private Integer id;
  private String name;
  private String address;
  private String email;
  private String phone;
  private String notes;
  private String area;
  private Country country;
  private Boolean hidden;
  private LibraryStatus libraryStatus;
  private List<SchoolContact> contacts;

  public School() {}

  public School(
      Integer id,
      String name,
      String address,
      String email,
      String phone,
      String notes,
      String area,
      Country country,
      Boolean hidden,
      LibraryStatus libraryStatus) {
    this.id = id;
    this.name = name;
    this.address = address;
    this.email = email;
    this.phone = phone;
    this.notes = notes;
    this.area = area;
    this.country = country;
    this.hidden = hidden;
    this.libraryStatus = libraryStatus;
    this.contacts = new ArrayList<SchoolContact>();
  }

  public School(
      Integer id,
      String name,
      String address,
      String email,
      String phone,
      String notes,
      String area,
      Country country,
      Boolean hidden,
      LibraryStatus libraryStatus,
      List<SchoolContact> contacts) {
    this.id = id;
    this.name = name;
    this.address = address;
    this.email = email;
    this.phone = phone;
    this.notes = notes;
    this.area = area;
    this.country = country;
    this.hidden = hidden;
    this.libraryStatus = libraryStatus;
    this.contacts = contacts;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
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

  public LibraryStatus getLibraryStatus() {
    return libraryStatus;
  }

  public void setLibraryStatus(LibraryStatus libraryStatus) {
    this.libraryStatus = libraryStatus;
  }
}
