package com.codeforcommunity.dto.report;

public class UpsertReportInProgressLibrary extends UpsertReportGeneric {

  private Boolean isSharedSpace;
  private Boolean hasInvitingSpace;
  private String assignedPersonRole;
  private String assignedPersonTitle;
  private String apprenticeshipProgram;
  private Boolean trainsAndMentorsApprentices;
  private Boolean hasCheckInTimetables;
  private Boolean hasBookCheckoutSystem;
  private Integer numberOfStudentLibrarians;
  private String reasonNoStudentLibrarians;
  private Boolean hasSufficientTraining;
  private String teacherSupport;
  private String parentSupport;

  public UpsertReportInProgressLibrary(
      Integer numberOfChildren,
      Integer numberOfBooks,
      Integer mostRecentShipmentYear,
      Boolean isSharedSpace,
      Boolean hasInvitingSpace,
      String assignedPersonRole,
      String assignedPersonTitle,
      String apprenticeshipProgram,
      Boolean trainsAndMentorsApprentices,
      Boolean hasCheckInTimetables,
      Boolean hasBookCheckoutSystem,
      Integer numberOfStudentLibrarians,
      String reasonNoStudentLibrarians,
      Boolean hasSufficientTraining,
      String teacherSupport,
      String parentSupport) {
    super(numberOfChildren, numberOfBooks, mostRecentShipmentYear);
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

  public Boolean getSharedSpace() {
    return isSharedSpace;
  }

  public void setSharedSpace(Boolean sharedSpace) {
    isSharedSpace = sharedSpace;
  }

  public Boolean getHasInvitingSpace() {
    return hasInvitingSpace;
  }

  public void setHasInvitingSpace(Boolean hasInvitingSpace) {
    this.hasInvitingSpace = hasInvitingSpace;
  }

  public String getAssignedPersonRole() {
    return assignedPersonRole;
  }

  public void setAssignedPersonRole(String assignedPersonRole) {
    this.assignedPersonRole = assignedPersonRole;
  }

  public String getAssignedPersonTitle() {
    return assignedPersonTitle;
  }

  public void setAssignedPersonTitle(String assignedPersonTitle) {
    this.assignedPersonTitle = assignedPersonTitle;
  }

  public String getApprenticeshipProgram() {
    return apprenticeshipProgram;
  }

  public void setApprenticeshipProgram(String apprenticeshipProgram) {
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
