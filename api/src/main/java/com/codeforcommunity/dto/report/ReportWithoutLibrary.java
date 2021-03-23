package com.codeforcommunity.dto.report;

import com.codeforcommunity.enums.LibraryStatus;
import com.codeforcommunity.enums.ReadyTimeline;
import java.sql.Timestamp;

public class ReportWithoutLibrary extends ReportGeneric {

  private Boolean wantsLibrary;
  private Boolean hasSpace;
  private String currentStatus;
  private String reason;
  private ReadyTimeline readyTimeline;

  public ReportWithoutLibrary() {
    super(LibraryStatus.DOES_NOT_EXIST);
  }

  public ReportWithoutLibrary(
      Integer id,
      Timestamp createdAt,
      Timestamp updatedAt,
      Integer schoolId,
      Integer userId,
      Integer numberOfChildren,
      Integer numberOfBooks,
      Integer mostRecentShipmentYear,
      Boolean wantsLibrary,
      Boolean hasSpace,
      String currentStatus,
      String reason,
      ReadyTimeline readyTimeline,
      String visitReason) {
    super(
        id,
        createdAt,
        updatedAt,
        schoolId,
        userId,
        numberOfChildren,
        numberOfBooks,
        mostRecentShipmentYear,
        LibraryStatus.DOES_NOT_EXIST,
        visitReason);
    this.wantsLibrary = wantsLibrary;
    this.hasSpace = hasSpace;
    this.currentStatus = currentStatus;
    this.reason = reason;
    this.readyTimeline = readyTimeline;
  }

  public Boolean getWantsLibrary() {
    return this.wantsLibrary;
  }

  public Boolean getHasSpace() {
    return this.hasSpace;
  }

  public String getCurrentStatus() {
    return this.currentStatus;
  }

  public String getReason() {
    return this.reason;
  }

  public ReadyTimeline getReadyTimeline() {
    return this.readyTimeline;
  }

  public String toHeaderCSV() {
    return "Report ID,"
        + "Created At,"
        + "Updated At,"
        + "School ID,"
        + "User ID,"
        + "Number of Children,"
        + "Number of Books,"
        + "Most Recent Shipment Year,"
        + "Library Status,"
        + "Visit Reason"
        + "Wants Library,"
        + "Has Space,"
        + "Current Status,"
        + "Reason,"
        + "Ready Timeline\n";
  }

  public String toRowCSV() {
    return this.getId().toString()
        + ","
        + (this.getCreatedAt() == null ? "" : this.getCreatedAt().toString())
        + ","
        + (this.getUpdatedAt() == null ? "" : this.getUpdatedAt().toString())
        + ","
        + (this.getSchoolId() == null ? "" : this.getSchoolId().toString())
        + ","
        + (this.getUserId() == null ? "" : this.getUserId().toString())
        + ","
        + (this.getNumberOfChildren() == null ? "" : this.getNumberOfChildren().toString())
        + ","
        + (this.getNumberOfBooks() == null ? "" : this.getNumberOfBooks().toString())
        + ","
        + (this.getMostRecentShipmentYear() == null
            ? ""
            : this.getMostRecentShipmentYear().toString())
        + ","
        + (this.getLibraryStatus() == null ? "" : this.getLibraryStatus().toString())
        + ","
        + (this.getVisitReason() == null ? "" : this.getVisitReason())
        + ","
        + (this.wantsLibrary == null ? "" : this.wantsLibrary.toString())
        + ","
        + (this.hasSpace == null ? "" : this.hasSpace.toString())
        + ","
        + (this.currentStatus == null ? "" : this.currentStatus)
        + ","
        + (this.reason == null ? "" : this.reason)
        + ","
        + (this.readyTimeline == null ? "" : this.readyTimeline.toString())
        + "\n";
  }
}
