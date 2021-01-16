package com.codeforcommunity.dto.report;

public class ReportGeneric {

  private Integer id;
  private Integer userId;
  private Integer schoolId;
  private Integer numberOfChildren;
  private Integer numberOfBooks;
  private Integer mostRecentShipmentYear;

  public ReportGeneric(Integer id, Integer userId, Integer schoolId, Integer numberOfChildren, Integer numberOfBooks, Integer mostRecentShipmentYear) {
    this.id = id;
    this.userId = userId;
    this.schoolId = schoolId;
    this.numberOfChildren = numberOfChildren;
    this.numberOfBooks = numberOfBooks;
    this.mostRecentShipmentYear = mostRecentShipmentYear;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public Integer getSchoolId() {
    return schoolId;
  }

  public void setSchoolId(Integer schoolId) {
    this.schoolId = schoolId;
  }

  public Integer getNumberOfChildren() {
    return numberOfChildren;
  }

  public void setNumberOfChildren(Integer numberOfChildren) {
    this.numberOfChildren = numberOfChildren;
  }

  public Integer getNumberOfBooks() {
    return numberOfBooks;
  }

  public void setNumberOfBooks(Integer numberOfBooks) {
    this.numberOfBooks = numberOfBooks;
  }

  public Integer getMostRecentShipmentYear() {
    return mostRecentShipmentYear;
  }

  public void setMostRecentShipmentYear(Integer mostRecentShipmentYear) {
    this.mostRecentShipmentYear = mostRecentShipmentYear;
  }
}
