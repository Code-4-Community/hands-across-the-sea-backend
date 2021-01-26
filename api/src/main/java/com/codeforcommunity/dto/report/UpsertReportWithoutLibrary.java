package com.codeforcommunity.dto.report;

public class UpsertReportWithoutLibrary extends UpsertReportGeneric {

  private String reasonWhyNot;
  private Boolean wantsLibrary;
  private Boolean hasSpace;
  private String currentStatus;
  private String readyTimeline;

  public UpsertReportWithoutLibrary(
      Integer numberOfChildren,
      Integer numberOfBooks,
      Integer mostRecentShipmentYear,
      String reasonWhyNot,
      Boolean wantsLibrary,
      Boolean hasSpace,
      String currentStatus,
      String readyTimeline) {
    super(numberOfChildren, numberOfBooks, mostRecentShipmentYear);
    this.reasonWhyNot = reasonWhyNot;
    this.wantsLibrary = wantsLibrary;
    this.hasSpace = hasSpace;
    this.currentStatus = currentStatus;
    this.readyTimeline = readyTimeline;
  }

  public String getReasonWhyNot() {
    return reasonWhyNot;
  }

  public void setReasonWhyNot(String reasonWhyNot) {
    this.reasonWhyNot = reasonWhyNot;
  }

  public Boolean getWantsLibrary() {
    return wantsLibrary;
  }

  public void setWantsLibrary(Boolean wantsLibrary) {
    this.wantsLibrary = wantsLibrary;
  }

  public Boolean getHasSpace() {
    return hasSpace;
  }

  public void setHasSpace(Boolean hasSpace) {
    this.hasSpace = hasSpace;
  }

  public String getCurrentStatus() {
    return currentStatus;
  }

  public void setCurrentStatus(String currentStatus) {
    this.currentStatus = currentStatus;
  }

  public String getReadyTimeline() {
    return readyTimeline;
  }

  public void setReadyTimeline(String readyTimeline) {
    this.readyTimeline = readyTimeline;
  }
}
