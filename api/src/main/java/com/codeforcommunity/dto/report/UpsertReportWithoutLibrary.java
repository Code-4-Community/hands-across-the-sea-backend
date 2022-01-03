package com.codeforcommunity.dto.report;

import com.codeforcommunity.enums.ReadyTimeline;
import java.util.List;

public class UpsertReportWithoutLibrary extends UpsertReportGeneric {

  private String reason;
  private Boolean wantsLibrary;
  private Boolean hasSpace;
  private List<String> currentStatus;
  private ReadyTimeline readyTimeline;
  private String reasonNoLibrarySpace;

  public String getReason() {
    return reason;
  }

  public String getReasonNoLibrarySpace() {
    return this.reasonNoLibrarySpace;
  }

  public void setReason(String reason) {
    this.reason = reason;
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

  public List<String> getCurrentStatus() {
    return currentStatus;
  }

  public void setCurrentStatus(List<String> currentStatus) {
    this.currentStatus = currentStatus;
  }

  public ReadyTimeline getReadyTimeline() {
    return readyTimeline;
  }

  public void setReadyTimeline(ReadyTimeline readyTimeline) {
    this.readyTimeline = readyTimeline;
  }
}
