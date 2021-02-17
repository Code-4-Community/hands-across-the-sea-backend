package com.codeforcommunity.dto.report;

import com.codeforcommunity.enums.ApprenticeTitle;
import com.codeforcommunity.enums.ApprenticeshipProgram;
import com.codeforcommunity.enums.LibraryStatus;
import com.codeforcommunity.enums.TimeRole;
import java.sql.Timestamp;

public class ReportWithLibraryInProgress extends ReportGeneric {

  private Boolean isSharedSpace;
  private Boolean hasInvitingSpace;
  private TimeRole assignedPersonRole;
  private ApprenticeTitle apprenticeTitle;
  private ApprenticeshipProgram apprenticeshipProgram;
  private Boolean trainsAndMentorsApprentices;

  public ReportWithLibraryInProgress(
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
      ApprenticeTitle apprenticeTitle,
      ApprenticeshipProgram apprenticeshipProgram,
      Boolean trainsAndMentorsApprentices) {
    super(
        id,
        createdAt,
        updatedAt,
        schoolId,
        userId,
        numberOfChildren,
        numberOfBooks,
        mostRecentShipmentYear,
        LibraryStatus.IN_PROGRESS);
    this.isSharedSpace = isSharedSpace;
    this.hasInvitingSpace = hasInvitingSpace;
    this.assignedPersonRole = assignedPersonRole;
    this.apprenticeTitle = apprenticeTitle;
    this.apprenticeshipProgram = apprenticeshipProgram;
    this.trainsAndMentorsApprentices = trainsAndMentorsApprentices;
  }

  public Boolean getIsSharedSpace() {
    return this.isSharedSpace;
  }

  public Boolean getHasInvitingSpace() {
    return this.hasInvitingSpace;
  }

  public TimeRole getAssignedPersonRole() {
    return this.assignedPersonRole;
  }

  public ApprenticeTitle getApprenticeTitle() {
    return this.apprenticeTitle;
  }

  public ApprenticeshipProgram getApprenticeshipProgram() {
    return this.apprenticeshipProgram;
  }

  public Boolean getTrainsAndMentorsApprentices() {
    return this.trainsAndMentorsApprentices;
  }
}
