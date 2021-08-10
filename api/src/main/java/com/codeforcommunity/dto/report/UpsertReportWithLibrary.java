package com.codeforcommunity.dto.report;

import com.codeforcommunity.enums.ApprenticeshipProgram;
import com.codeforcommunity.enums.AssignedPersonTitle;
import com.codeforcommunity.enums.Grade;
import com.codeforcommunity.enums.TimeRole;
import com.codeforcommunity.exceptions.HandledException;
import com.codeforcommunity.logger.SLogger;
import com.codeforcommunity.rest.RestFunctions;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UpsertReportWithLibrary extends UpsertReportGeneric {

  SLogger logger = new SLogger(UpsertReportWithLibrary.class);

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

  public JsonNode getCheckInTimetable() {
    return checkInTimetable;
  }

  public JsonNode getCheckOutTimetable() { return checkOutTimetable; }

  public void JsonNode(JsonNode checkInTimetable) {
    this.checkInTimetable = checkInTimetable;
  }

  public void setCheckOutTimetable(JsonNode checkOutTimetable) { this.checkOutTimetable = checkOutTimetable; }

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

    List<String> checkInTimetableFields = this.validateTimetable(checkInTimetable);
    List<String> checkOutTimetableFields = this.validateTimetable(checkOutTimetable);
    fields.addAll(checkInTimetableFields);
    fields.addAll(checkOutTimetableFields);
    return fields;
  }

  private List<String> validateTimetable(JsonNode timetable) {
    List<String> invalidFields = new ArrayList<String>();

    if (timetable.isNull()) {
      return invalidFields;
    }

    boolean hasMonthField = false;
    boolean hasYearField = false;

    Iterator<Map.Entry<String, JsonNode>> it = timetable.fields();
    while (it.hasNext()) {
      Map.Entry<String, JsonNode> entry = it.next();
      String fieldName = entry.getKey(); // e.g. "firstGrade"
      JsonNode value = entry.getValue(); // e.g. { "3": 19 }

      // Validate the "month" field
      if (fieldName.equals("month")) {
        hasMonthField = true;
        if (!value.isInt()) {
          logger.error("`UpsertReportWithLibrary` `timetable.month` must be an integer");
          invalidFields.add("timetable.month");
          continue;
        }

        int month = value.asInt();
        if (month < 1 || month > 12) {
          logger.error(
              String.format(
                  "`UpsertReportWithLibrary` `timetable` has an invalid field: `%s`", fieldName));
          invalidFields.add("timetable.month");
        }
        continue;
      }

      // Validate the "year" field
      if (fieldName.equals("year")) {
        hasYearField = true;
        if (!value.isInt()) {
          logger.error("`UpsertReportWithLibrary` `timetable.year` must be an integer");
          invalidFields.add("timetable.year");
          continue;
        }

        int year = value.asInt();
        if (year < 1900 || year > 2999) {
          logger.error(
              String.format(
                  "`UpsertReportWithLibrary` `timetable` has an invalid field: `%s`", fieldName));
          invalidFields.add("timetable.year");
        }
        continue;
      }

      try { // Validate the grade fields
        String gradeName = RestFunctions.getUpperSnakeFromCamel(fieldName);
        Grade grade = Grade.valueOf(gradeName);

        // Validate the values of each grade field
        if (!this.validateTimetableGrade(grade, value)) {
          invalidFields.add(String.format("timetable.%s", fieldName));
        }
      } catch (IllegalArgumentException e) {
        logger.error(
            String.format(
                "`UpsertReportWithLibrary` `timetable` has an invalid field: `%s`", fieldName));
        invalidFields.add(String.format("timetable.%s", fieldName));
      }
    }

    if (!hasYearField) { // "year" is a required field
      invalidFields.add("timetable.year");
    }
    if (!hasMonthField) { // "month" is a required field
      invalidFields.add("timetable.month");
    }
    return invalidFields;
  }

  private boolean validateTimetableGrade(Grade grade, JsonNode timetableGrade) {
    Iterator<Map.Entry<String, JsonNode>> it = timetableGrade.fields();
    while (it.hasNext()) {
      Map.Entry<String, JsonNode> entry = it.next();
      String dayStr = entry.getKey(); // e.g. "3"
      JsonNode countNode = entry.getValue(); // e.g. 19

      try {
        int day = Integer.parseInt(dayStr);
      } catch (NumberFormatException e) {
        logger.error(
            String.format(
                "`UpsertReportWithLibrary` `timetable.%s` has an invalid date field",
                grade.toString()));
        return false;
      }

      if (!countNode.isInt()) {
        logger.error(
            String.format(
                "`UpsertReportWithLibrary` `timetable.%s` has an invalid count field",
                grade.toString()));
        return false;
      }
    }
    return true;
  }
}
