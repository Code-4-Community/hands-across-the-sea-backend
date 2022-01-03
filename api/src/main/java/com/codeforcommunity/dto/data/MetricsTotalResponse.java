package com.codeforcommunity.dto.data;

public class MetricsTotalResponse {
  private Integer countSchools;
  private Integer countBooks;
  private Integer countStudents;

  public MetricsTotalResponse(Integer countSchools, Integer countBooks, Integer countStudents) {
    this.countSchools = countSchools;
    this.countBooks = countBooks;
    this.countStudents = countStudents;
  }

  public Integer getCountSchools() {
    return countSchools;
  }

  public void setCountSchools(Integer countSchools) {
    this.countSchools = countSchools;
  }

  public Integer getCountBooks() {
    return countBooks;
  }

  public void setCountBooks(Integer countBooks) {
    this.countBooks = countBooks;
  }

  public Integer getCountStudents() {
    return countStudents;
  }

  public void setCountStudents(Integer countStudents) {
    this.countStudents = countStudents;
  }
}
