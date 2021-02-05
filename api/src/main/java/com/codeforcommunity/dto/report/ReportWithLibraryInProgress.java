package com.codeforcommunity.dto.report;

import com.codeforcommunity.enums.ApprenticeTitle;
import com.codeforcommunity.enums.TimeRole;

public class ReportWithLibraryInProgress extends ReportGeneric {

  private Boolean isSharedSpace;
  private Boolean hasInvitingSpace;
  private TimeRole assignedPersonRole;
  private ApprenticeTitle apprenticeTitle;
  private Boolean trainsAndMentorsApprentices;

  public ReportWithLibraryInProgress(Integer id,
      Integer schoolId,
      Integer userId,
      Integer numberOfChildren,
      Integer numberOfBooks,
      Integer mostRecentShipmentYear,
      Boolean isSharedSpace,
      Boolean hasInvitingSpace,
      TimeRole assignedPersonRole,
      ApprenticeTitle apprenticeTitle,
      Boolean trainsAndMentorsApprentices
      ) {
    super(id, schoolId, userId, numberOfChildren, numberOfBooks, mostRecentShipmentYear);
    this.isSharedSpace = isSharedSpace;
    this.hasInvitingSpace = hasInvitingSpace;
    this.assignedPersonRole = assignedPersonRole;
    this.apprenticeTitle = apprenticeTitle;
    this.trainsAndMentorsApprentices = trainsAndMentorsApprentices;
  }

  public Boolean getIsSharedSpace() { return this.isSharedSpace; }

  public Boolean getHasInvitingSpace() { return this.hasInvitingSpace; }

  public TimeRole getAssignedPersonRole() { return this.assignedPersonRole; }

  public ApprenticeTitle getApprenticeTitle() { return this.apprenticeTitle; }

  public Boolean getTrainsAndMentorsApprentices() { return this.trainsAndMentorsApprentices; }
}
