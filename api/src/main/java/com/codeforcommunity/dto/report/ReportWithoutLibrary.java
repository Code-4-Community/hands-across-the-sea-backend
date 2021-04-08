package com.codeforcommunity.dto.report;

import com.codeforcommunity.enums.LibraryStatus;
import com.codeforcommunity.enums.ReadyTimeline;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.sql.Timestamp;

public class ReportWithoutLibrary extends ReportGeneric {

  private Boolean wantsLibrary;
  private Boolean hasSpace;
  private String currentStatus;
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
      String currentStatus,
      String reason,
      ReadyTimeline readyTimeline,
      String visitReason) {
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
        visitReason);
    this.wantsLibrary = wantsLibrary;
    this.hasSpace = hasSpace;
    this.currentStatus = currentStatus;
    this.reason = reason;
    this.readyTimeline = readyTimeline;
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

  public ReadyTimeline getReadyTimeline() {
    return this.readyTimeline;
  }


  //  public String toHeaderCSV() {
  //    Field[] fields = ReportWithoutLibrary.class.getDeclaredFields();
  //    StringBuilder builder = new StringBuilder();
  //    for (int i = 0; i < fields.length; i++) {
  //      builder.append(fields[i].getName());
  //      builder.append(",");
  //    }
  //    return super.toHeaderCSV() + builder + "\n";
  //  }
  //
  //  public String toRowCSV() throws InvocationTargetException, IllegalAccessException {
  //    Method[] methods = ReportWithoutLibrary.class.getDeclaredMethods();
  //    StringBuilder builder = new StringBuilder();
  //    for (Method method : methods) {
  //      if (method.getName().startsWith("get")) {
  //        builder.append(method.getName());
  //        builder.append(method.invoke(this));
  //        builder.append(",");
  //      }
  //    }
  //    return super.toRowCSV() + builder + "\n";
  //  }

}
