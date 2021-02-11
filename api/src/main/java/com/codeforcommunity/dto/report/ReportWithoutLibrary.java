package com.codeforcommunity.dto.report;

import com.codeforcommunity.enums.ReadyTimeline;
import java.sql.Timestamp;

public class ReportWithoutLibrary extends ReportGeneric {

  private Boolean wantsLibrary;
  private Boolean hasSpace;
  private String currentStatus;
  private String reason;
  private ReadyTimeline timeline;

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
      ReadyTimeline timeline) {
    super(
        id,
        createdAt,
        updatedAt,
        schoolId,
        userId,
        numberOfChildren,
        numberOfBooks,
        mostRecentShipmentYear);
    this.wantsLibrary = wantsLibrary;
    this.hasSpace = hasSpace;
    this.currentStatus = currentStatus;
    this.reason = reason;
    this.timeline = timeline;
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

  public ReadyTimeline getTimeline() {
    return this.timeline;
  }
}
