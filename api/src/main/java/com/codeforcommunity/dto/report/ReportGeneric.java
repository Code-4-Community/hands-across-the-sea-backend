package com.codeforcommunity.dto.report;

import com.codeforcommunity.enums.LibraryStatus;
import java.sql.Timestamp;
import java.util.Date;

public class ReportGeneric {

  protected Integer id;
  protected Timestamp createdAt;
  protected Timestamp updatedAt;
  protected Integer schoolId;
  protected Integer userId;
  protected Integer numberOfChildren;
  protected Integer numberOfBooks;
  protected Integer mostRecentShipmentYear;
  protected LibraryStatus libraryStatus;
  protected String visitReason;

  public ReportGeneric(LibraryStatus libraryStatus) {
    this.libraryStatus = libraryStatus;
  }

  public ReportGeneric(
      Integer id,
      Timestamp createdAt,
      Timestamp updatedAt,
      Integer schoolId,
      Integer userId,
      Integer numberOfChildren,
      Integer numberOfBooks,
      Integer mostRecentShipmentYear,
      LibraryStatus libraryStatus,
      String visitReason) {
    this.id = id;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.schoolId = schoolId;
    this.userId = userId;
    this.numberOfChildren = numberOfChildren;
    this.numberOfBooks = numberOfBooks;
    this.mostRecentShipmentYear = mostRecentShipmentYear;
    this.libraryStatus = libraryStatus;
    this.visitReason = visitReason;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getCreatedAt() {
    return new Date(createdAt.getTime()).toString();
  }

  public void setCreatedAt(Timestamp createdAt) {
    this.createdAt = createdAt;
  }

  public String getUpdatedAt() {
    return new Date(updatedAt.getTime()).toString();
  }

  public void setUpdatedAt(Timestamp updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Integer getSchoolId() {
    return schoolId;
  }

  public void setSchoolId(Integer schoolId) {
    this.schoolId = schoolId;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
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

  public LibraryStatus getLibraryStatus() {
    return libraryStatus;
  }

  public void setLibraryStatus(LibraryStatus libraryStatus) {
    this.libraryStatus = libraryStatus;
  }

  public String getVisitReason() {
    return visitReason;
  }

  public void setVisitReason(String visitReason) {
    this.visitReason = visitReason;
  }
}
