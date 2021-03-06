package com.codeforcommunity.dto.report;

import java.util.List;

public class ReportGenericListResponse {

  private Integer count;
  private List<ReportGeneric> reports;

  public ReportGenericListResponse(List<ReportGeneric> reports, Integer count) {
    if (reports == null) {
      throw new IllegalArgumentException("Given `null` list of reports");
    }
    if (count == null) {
      throw new IllegalArgumentException("Given `null` coubnt of reports");
    }

    this.reports = reports;
    this.count = count;
  }

  public List<ReportGeneric> getReports() {
    return reports;
  }

  public void setReports(List<ReportGeneric> reports) {
    this.reports = reports;
  }

  public Integer getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }
}
