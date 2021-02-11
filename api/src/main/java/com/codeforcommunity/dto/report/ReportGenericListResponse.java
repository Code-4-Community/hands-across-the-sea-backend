package com.codeforcommunity.dto.report;

import java.util.List;

public class ReportGenericListResponse {

  private int count;
  private List<ReportGeneric> reports;

  public ReportGenericListResponse(List<ReportGeneric> reports) {
    if (reports == null) {
      throw new IllegalArgumentException("Given `null` list of reports");
    }

    this.reports = reports;
    this.count = reports.size();
  }

  public List<ReportGeneric> getReports() {
    return reports;
  }

  public void setReports(List<ReportGeneric> reports) {
    this.reports = reports;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }
}
