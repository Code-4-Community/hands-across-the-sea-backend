package com.codeforcommunity.dto.school;

import java.util.List;

public class SchoolListResponse {

  private Integer count;
  private List<SchoolSummary> schools;

  public SchoolListResponse(List<SchoolSummary> schools) {
    if (schools == null) {
      throw new IllegalArgumentException("Given `null` list of schools");
    }

    this.schools = schools;
    this.count = schools.size();
  }

  public List<SchoolSummary> getSchools() {
    return schools;
  }

  public void setSchools(List<SchoolSummary> schools) {
    this.schools = schools;
  }

  public Integer getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }
}
