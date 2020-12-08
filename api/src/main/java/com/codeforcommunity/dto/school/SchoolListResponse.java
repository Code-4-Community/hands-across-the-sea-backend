package com.codeforcommunity.dto.school;

import java.util.List;

public class SchoolListResponse {

  private int count;
  private List<School> schools;

  public SchoolListResponse(List<School> schools, int count) {
    this.schools = schools;
    this.count = count;
  }

  public List<School> getSchools() {
    return schools;
  }

  public void setSchools(List<School> schools) {
    this.schools = schools;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }
}
