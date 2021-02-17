package com.codeforcommunity.dto.report;

import com.codeforcommunity.enums.ApprenticeTitle;
import com.codeforcommunity.enums.ApprenticeshipProgram;
import com.codeforcommunity.enums.LibraryStatus;
import com.codeforcommunity.enums.TimeRole;
import java.sql.Timestamp;

public class ReportWithLibrary extends ReportGeneric {

  private Boolean isSharedSpace;
  private Boolean hasInvitingSpace;
  private TimeRole assignedPersonRole;
  private ApprenticeTitle assignedPersonTitle;
  private ApprenticeshipProgram apprenticeshipProgram;
  private Boolean trainsAndMentorsApprentices;
  private Boolean hasCheckInTimetables;
  private Boolean hasBookCheckoutSystem;
  private Integer numberOfStudentLibrarians;
  private String reasonNoStudentLibrarians;
  private Boolean hasSufficientTraining;
  private String teacherSupport;
  private String parentSupport;

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
      ApprenticeTitle assignedPersonTitle,
      ApprenticeshipProgram apprenticeshipProgram,
      Boolean trainsAndMentorsApprentices,
      Boolean hasCheckInTimetables,
      Boolean hasBookCheckoutSystem,
      Integer numberOfStudentLibrarians,
      String reasonNoStudentLibrarians,
      Boolean hasSufficientTraining,
      String teacherSupport,
      String parentSupport) {
    super(
        id,
        createdAt,
        updatedAt,
        schoolId,
        userId,
        numberOfChildren,
        numberOfBooks,
        mostRecentShipmentYear,
        LibraryStatus.EXISTS);
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

  public ApprenticeTitle getAssignedPersonTitle() {
    return assignedPersonTitle;
  }

  public void setAssignedPersonTitle(ApprenticeTitle assignedPersonTitle) {
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
}
