package com.codeforcommunity.dto.data;

public class MetricGeneric {

  private Integer totalBooks;
  private Integer totalStudents;

  public MetricGeneric(Integer totalBooks, Integer totalStudents) {
    this.totalBooks = totalBooks;
    this.totalStudents = totalStudents;
  }

  public Integer getTotalBooks() {
    return totalBooks;
  }

  public Integer getTotalStudents() {
    return totalStudents;
  }

  public void setTotalBooks(Integer totalBooks) {
    this.totalBooks = totalBooks;
  }

  public void setTotalStudents(Integer totalStudents) {
    this.totalStudents = totalStudents;
  }
}
