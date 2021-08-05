package com.codeforcommunity.dto.report;

import com.codeforcommunity.enums.Grade;
import com.codeforcommunity.enums.LibraryStatus;
import com.codeforcommunity.enums.ReadyTimeline;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.jooq.generated.tables.records.SchoolReportsWithoutLibrariesRecord;

public class ReportWithoutLibrary extends ReportGeneric {

  private Boolean wantsLibrary;
  private Boolean hasSpace;
  private List<String> currentStatus;
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
      List<String> currentStatus,
      String reason,
      ReadyTimeline readyTimeline,
      String visitReason,
      String actionPlan,
      String successStories,
      List<Grade> gradesAttended,
      String userName,
      String schoolName) {
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
        visitReason,
        actionPlan,
        successStories,
        gradesAttended,
        userName,
        schoolName);
    this.wantsLibrary = wantsLibrary;
    this.hasSpace = hasSpace;
    this.currentStatus = currentStatus;
    this.reason = reason;
    this.readyTimeline = readyTimeline;
  }

  public static ReportWithoutLibrary instantiateFromRecord(
      SchoolReportsWithoutLibrariesRecord record, String userName, String schoolName) {
    return new ReportWithoutLibrary(
        record.getId(),
        record.getCreatedAt(),
        record.getUpdatedAt(),
        record.getSchoolId(),
        record.getUserId(),
        record.getNumberOfChildren(),
        record.getNumberOfBooks(),
        record.getMostRecentShipmentYear(),
        record.getWantsLibrary(),
        record.getHasSpace(),
        Arrays.asList((String[]) record.getCurrentStatus()),
        record.getReasonWhyNot(),
        record.getReadyTimeline(),
        record.getVisitReason(),
        record.getActionPlan(),
        record.getSuccessStories(),
        Arrays.stream(record.getGradesAttended())
            .map(gradeString -> Grade.valueOf((String) gradeString))
            .collect(Collectors.toList()),
        userName,
        schoolName);
  }

  public Boolean getWantsLibrary() {
    return this.wantsLibrary;
  }

  public Boolean getHasSpace() {
    return this.hasSpace;
  }

  public List<String> getCurrentStatus() {
    return this.currentStatus;
  }

  public String getReason() {
    return this.reason;
  }

  public ReadyTimeline getReadyTimeline() {
    return this.readyTimeline;
  }
}
