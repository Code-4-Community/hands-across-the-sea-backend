package com.codeforcommunity.dto.report;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.enums.Grade;
import com.codeforcommunity.exceptions.HandledException;
import java.util.ArrayList;
import java.util.List;

public class UpsertReportGeneric extends ApiDto {

  private Integer numberOfChildren;
  private Integer numberOfBooks;
  private Integer mostRecentShipmentYear;
  private String visitReason;
  private String actionPlan;
  private String successStories;
  private Grade[] gradesAttended;

  public Integer getNumberOfChildren() {
    return numberOfChildren;
  }

  public void setNumberOfChildren(Integer numberOfChildren) {
    this.numberOfChildren = numberOfChildren;
  }

  public Integer getNumberOfBooks() {
    return numberOfBooks;
  }

  public void setNumberOfBooks(Integer numberOfBooks) {
    this.numberOfBooks = numberOfBooks;
  }

  public Integer getMostRecentShipmentYear() {
    return mostRecentShipmentYear;
  }

  public void setMostRecentShipmentYear(Integer mostRecentShipmentYear) {
    this.mostRecentShipmentYear = mostRecentShipmentYear;
  }

  public String getVisitReason() {
    return visitReason;
  }

  public void setVisitReason(String visitReason) {
    this.visitReason = visitReason;
  }

  public String getActionPlan() {
    return actionPlan;
  }

  public void setActionPlan(String actionPlan) {
    this.actionPlan = actionPlan;
  }

  public String getSuccessStories() {
    return successStories;
  }

  public void setSuccessStories(String successStories) {
    this.successStories = successStories;
  }

  public Grade[] getGradesAttended() {
    return gradesAttended;
  }

  public void setGradesAttended(Grade[] gradesAttended) {
    this.gradesAttended = gradesAttended;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    List<String> fields = new ArrayList<String>();
    if (numberOfChildren == null) {
      fields.add("numberOfChildren");
    }
    if (numberOfBooks == null) {
      fields.add("numberOfBooks");
    }
    if (mostRecentShipmentYear == null) {
      fields.add("mostRecentShipmentYear");
    }
    if (visitReason == null) {
      fields.add("visitReason");
    }
    if (gradesAttended == null) {
      fields.add("gradesAttended");
    }
    return fields;
  }
}
