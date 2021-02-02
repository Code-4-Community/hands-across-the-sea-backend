package com.codeforcommunity.dto.school;

import com.codeforcommunity.dto.report.ReportGeneric;
import java.util.List;

public class SchoolReportListResponse {

  private int count;
  private List<ReportGeneric> schoolReports;

  public SchoolReportListResponse(List<ReportGeneric> schoolReports) {
    if (schoolReports == null) {
      throw new IllegalArgumentException("Given `null` list of school reports");
    }

    this.schoolReports = schoolReports;
    this.count = schoolReports.size();
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public List<ReportGeneric> getSchoolReports() {
    return schoolReports;
  }

  public void setSchoolReports(List<ReportGeneric> schoolReports) {
    this.schoolReports = schoolReports;
  }
}
