package com.codeforcommunity.dto.report;

import com.codeforcommunity.enums.ApprenticeshipProgram;
import com.codeforcommunity.enums.AssignedPersonTitle;
import com.codeforcommunity.enums.Grade;
import com.codeforcommunity.enums.LibraryStatus;
import com.codeforcommunity.enums.TimeRole;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.JsonObject;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.jooq.generated.tables.records.SchoolReportsWithLibrariesRecord;

public class ReportWithLibrary extends ReportGeneric {

  private Boolean isSharedSpace;
  private Boolean hasInvitingSpace;
  private TimeRole assignedPersonRole;
  private AssignedPersonTitle assignedPersonTitle;
  private ApprenticeshipProgram apprenticeshipProgram;
  private Boolean trainsAndMentorsApprentices;
  private Boolean hasCheckInTimetables;
  private Boolean hasBookCheckoutSystem;
  private Integer numberOfStudentLibrarians;
  private String reasonNoStudentLibrarians;
  private Boolean hasSufficientTraining;
  private String teacherSupport;
  private String parentSupport;
  private JsonNode checkInTimetable;
  private JsonNode checkOutTimetable;

  public ReportWithLibrary() {
    super(LibraryStatus.EXISTS);
  }

  public ReportWithLibrary(
      Integer id,
      Timestamp createdAt,
      Timestamp updatedAt,
      Integer schoolId,
      Integer userId,
      Integer numberOfChildren,
      Integer numberOfBooks,
      Integer mostRecentShipmentYear,
      Boolean isSharedSpace,
      Boolean hasInvitingSpace,
      TimeRole assignedPersonRole,
      AssignedPersonTitle assignedPersonTitle,
      ApprenticeshipProgram apprenticeshipProgram,
      Boolean trainsAndMentorsApprentices,
      Boolean hasCheckInTimetables,
      Boolean hasBookCheckoutSystem,
      Integer numberOfStudentLibrarians,
      String reasonNoStudentLibrarians,
      Boolean hasSufficientTraining,
      String teacherSupport,
      String parentSupport,
      String visitReason,
      String actionPlan,
      String successStories,
      List<Grade> gradesAttended,
      JsonNode checkInTimetable,
      String userName,
      String schoolName,
      JsonNode checkOutTimetable) {
    super(
        id,
        createdAt,
        updatedAt,
        schoolId,
        userId,
        numberOfChildren,
        numberOfBooks,
        mostRecentShipmentYear,
        LibraryStatus.EXISTS,
        visitReason,
        actionPlan,
        successStories,
        gradesAttended,
        userName,
        schoolName);
    this.isSharedSpace = isSharedSpace;
    this.hasInvitingSpace = hasInvitingSpace;
    this.assignedPersonRole = assignedPersonRole;
    this.assignedPersonTitle = assignedPersonTitle;
    this.apprenticeshipProgram = apprenticeshipProgram;
    this.trainsAndMentorsApprentices = trainsAndMentorsApprentices;
    this.hasCheckInTimetables = hasCheckInTimetables;
    this.hasBookCheckoutSystem = hasBookCheckoutSystem;
    this.numberOfStudentLibrarians = numberOfStudentLibrarians;
    this.reasonNoStudentLibrarians = reasonNoStudentLibrarians;
    this.hasSufficientTraining = hasSufficientTraining;
    this.teacherSupport = teacherSupport;
    this.parentSupport = parentSupport;
    this.checkInTimetable = checkInTimetable;
    this.checkOutTimetable = checkOutTimetable;
  }

  public ReportWithLibrary(
      Integer id,
      Timestamp createdAt,
      Timestamp updatedAt,
      Integer schoolId,
      Integer userId,
      Integer numberOfChildren,
      Integer numberOfBooks,
      Integer mostRecentShipmentYear,
      Boolean isSharedSpace,
      Boolean hasInvitingSpace,
      TimeRole assignedPersonRole,
      AssignedPersonTitle assignedPersonTitle,
      ApprenticeshipProgram apprenticeshipProgram,
      Boolean trainsAndMentorsApprentices,
      Boolean hasCheckInTimetables,
      Boolean hasBookCheckoutSystem,
      Integer numberOfStudentLibrarians,
      String reasonNoStudentLibrarians,
      Boolean hasSufficientTraining,
      String teacherSupport,
      String parentSupport,
      String visitReason,
      String actionPlan,
      String successStories,
      List<Grade> gradesAttended,
      String checkInTimetable,
      String userName,
      String schoolName,
      String checkOutTimetable) {
    super(
        id,
        createdAt,
        updatedAt,
        schoolId,
        userId,
        numberOfChildren,
        numberOfBooks,
        mostRecentShipmentYear,
        LibraryStatus.EXISTS,
        visitReason,
        actionPlan,
        successStories,
        gradesAttended,
        userName,
        schoolName);
    this.isSharedSpace = isSharedSpace;
    this.hasInvitingSpace = hasInvitingSpace;
    this.assignedPersonRole = assignedPersonRole;
    this.assignedPersonTitle = assignedPersonTitle;
    this.apprenticeshipProgram = apprenticeshipProgram;
    this.trainsAndMentorsApprentices = trainsAndMentorsApprentices;
    this.hasCheckInTimetables = hasCheckInTimetables;
    this.hasBookCheckoutSystem = hasBookCheckoutSystem;
    this.numberOfStudentLibrarians = numberOfStudentLibrarians;
    this.reasonNoStudentLibrarians = reasonNoStudentLibrarians;
    this.hasSufficientTraining = hasSufficientTraining;
    this.teacherSupport = teacherSupport;
    this.parentSupport = parentSupport;

    try {
      ObjectMapper mapper = new ObjectMapper();
      ObjectMapper checkoutMapper = new ObjectMapper();
      this.checkInTimetable = mapper.readTree(checkInTimetable);
      this.checkOutTimetable = checkoutMapper.readTree(checkOutTimetable);
    } catch (IOException e) {
      throw new RuntimeException(
          String.format("Failed to parse timetables for `ReportWithLibrary` with ID %d", id));
    }
  }

  public static ReportWithLibrary instantiateFromRecord(
      SchoolReportsWithLibrariesRecord record, String userName, String schoolName) {
    return new ReportWithLibrary(
        record.getId(),
        record.getCreatedAt(),
        record.getUpdatedAt(),
        record.getSchoolId(),
        record.getUserId(),
        record.getNumberOfChildren(),
        record.getNumberOfBooks(),
        record.getMostRecentShipmentYear(),
        record.getIsSharedSpace(),
        record.getHasInvitingSpace(),
        record.getAssignedPersonRole(),
        record.getAssignedPersonTitle(),
        record.getApprenticeshipProgram(),
        record.getTrainsAndMentorsApprentices(),
        record.getHasCheckInTimetables(),
        record.getHasBookCheckoutSystem(),
        record.getNumberOfStudentLibrarians(),
        record.getReasonNoStudentLibrarians(),
        record.getHasSufficientTraining(),
        record.getTeacherSupport(),
        record.getParentSupport(),
        record.getVisitReason(),
        record.getActionPlan(),
        record.getSuccessStories(),
        Arrays.stream(record.getGradesAttended())
            .map(gradeString -> Grade.valueOf((String) gradeString))
            .collect(Collectors.toList()),
        record.getCheckinTimetable(),
        userName,
        schoolName,
        record.getCheckoutTimetable());
  }

  public Boolean getIsSharedSpace() {
    return isSharedSpace;
  }

  public void setIsSharedSpace(Boolean sharedSpace) {
    isSharedSpace = sharedSpace;
  }

  public Boolean getHasInvitingSpace() {
    return hasInvitingSpace;
  }

  public void setHasInvitingSpace(Boolean hasInvitingSpace) {
    this.hasInvitingSpace = hasInvitingSpace;
  }

  public TimeRole getAssignedPersonRole() {
    return assignedPersonRole;
  }

  public void setAssignedPersonRole(TimeRole assignedPersonRole) {
    this.assignedPersonRole = assignedPersonRole;
  }

  public AssignedPersonTitle getAssignedPersonTitle() {
    return assignedPersonTitle;
  }

  public void setAssignedPersonTitle(AssignedPersonTitle assignedPersonTitle) {
    this.assignedPersonTitle = assignedPersonTitle;
  }

  public ApprenticeshipProgram getApprenticeshipProgram() {
    return apprenticeshipProgram;
  }

  public void setApprenticeshipProgram(ApprenticeshipProgram apprenticeshipProgram) {
    this.apprenticeshipProgram = apprenticeshipProgram;
  }

  public Boolean getTrainsAndMentorsApprentices() {
    return trainsAndMentorsApprentices;
  }

  public void setTrainsAndMentorsApprentices(Boolean trainsAndMentorsApprentices) {
    this.trainsAndMentorsApprentices = trainsAndMentorsApprentices;
  }

  public Boolean getHasCheckInTimetables() {
    return hasCheckInTimetables;
  }

  public void setHasCheckInTimetables(Boolean hasCheckInTimetables) {
    this.hasCheckInTimetables = hasCheckInTimetables;
  }

  public Boolean getHasBookCheckoutSystem() {
    return hasBookCheckoutSystem;
  }

  public void setHasBookCheckoutSystem(Boolean hasBookCheckoutSystem) {
    this.hasBookCheckoutSystem = hasBookCheckoutSystem;
  }

  public Integer getNumberOfStudentLibrarians() {
    return numberOfStudentLibrarians;
  }

  public void setNumberOfStudentLibrarians(Integer numberOfStudentLibrarians) {
    this.numberOfStudentLibrarians = numberOfStudentLibrarians;
  }

  public String getReasonNoStudentLibrarians() {
    return reasonNoStudentLibrarians;
  }

  public void setReasonNoStudentLibrarians(String reasonNoStudentLibrarians) {
    this.reasonNoStudentLibrarians = reasonNoStudentLibrarians;
  }

  public Boolean getHasSufficientTraining() {
    return hasSufficientTraining;
  }

  public void setHasSufficientTraining(Boolean hasSufficientTraining) {
    this.hasSufficientTraining = hasSufficientTraining;
  }

  public String getTeacherSupport() {
    return teacherSupport;
  }

  public void setTeacherSupport(String teacherSupport) {
    this.teacherSupport = teacherSupport;
  }

  public String getParentSupport() {
    return parentSupport;
  }

  public void setParentSupport(String parentSupport) {
    this.parentSupport = parentSupport;
  }

  public JsonNode getCheckInTimetable() {
    return checkInTimetable;
  }

  public JsonNode getCheckOutTimetable() { return checkOutTimetable; }

  public void setCheckInTimetable(JsonNode checkInTimetable) {
    this.checkInTimetable = checkInTimetable;
  }

  public void setCheckOutTimetable(JsonNode checkOutTimetable) {
    this.checkOutTimetable = checkOutTimetable;
  }
}
