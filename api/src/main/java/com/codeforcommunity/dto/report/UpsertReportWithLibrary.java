package com.codeforcommunity.dto.report;

import com.codeforcommunity.enums.ApprenticeshipProgram;
import com.codeforcommunity.enums.AssignedPersonTitle;
import com.codeforcommunity.enums.TimeRole;
import com.codeforcommunity.exceptions.HandledException;
import java.util.List;

public class UpsertReportWithLibrary extends UpsertReportGeneric {

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

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    List<String> fields = super.validateFields(fieldPrefix);

    if (isSharedSpace == null) {
      fields.add("isSharedSpace");
    }
    if (hasInvitingSpace == null) {
      fields.add("hasInvitingSpace");
    }
    if (trainsAndMentorsApprentices == null) {
      fields.add("trainsAndMentorsApprentices");
    }
    if (hasCheckInTimetables == null) {
      fields.add("hasCheckInTimetables");
    }
    if (hasBookCheckoutSystem == null) {
      fields.add("hasBookCheckoutSystem");
    }
    if (numberOfStudentLibrarians == null) {
      fields.add("numberOfStudentLibrarians");
    }
    if (hasSufficientTraining == null) {
      fields.add("hasSufficientTraining");
    }

    return fields;
  }
}
