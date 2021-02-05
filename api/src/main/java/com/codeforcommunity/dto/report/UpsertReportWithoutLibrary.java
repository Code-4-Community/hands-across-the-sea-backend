package com.codeforcommunity.dto.report;

public class UpsertReportWithoutLibrary extends UpsertReportGeneric {

  private String reasonWhyNot;
  private Boolean wantsLibrary;
  private Boolean hasSpace;
  private String currentStatus;
  private String readyTimeline;

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
