package com.codeforcommunity.dto.data;

public class MetricsSchoolResponse {
  private Float countBooksPerStudent;
  private Integer countStudents;
  private Integer countStudentLibrarians;
  private Integer netBooksInOut;
  private Integer countBooks;

  public MetricsSchoolResponse(
      Float countBooksPerStudent,
      Integer countStudents,
      Integer countStudentLibrarians,
      Integer netBooksInOut,
      Integer countBooks) {
    this.countBooksPerStudent = countBooksPerStudent;
    this.countStudents = countStudents;
    this.countStudentLibrarians = countStudentLibrarians;
    this.netBooksInOut = netBooksInOut;
    this.countBooks = countBooks;
  }

  public Float getCountBooksPerStudent() {
    return countBooksPerStudent;
  }

  public void setCountBooksPerStudent(Float countBooksPerStudent) {
    this.countBooksPerStudent = countBooksPerStudent;
  }

  public Integer getCountStudents() {
    return countStudents;
  }

  public void setCountStudents(Integer countStudents) {
    this.countStudents = countStudents;
  }

  public Integer getCountStudentLibrarians() {
    return countStudentLibrarians;
  }

  public void setCountStudentLibrarians(Integer countStudentLibrarians) {
    this.countStudentLibrarians = countStudentLibrarians;
  }

  public Integer getNetBooksInOut() {
    return netBooksInOut;
  }

  public void setNetBooksInOut(Integer netBooksInOut) {
    this.netBooksInOut = netBooksInOut;
  }

  public Integer getCountBooks() {
    return countBooks;
  }

  public void setCountBooks(Integer countBooks) {
    this.countBooks = countBooks;
  }
}
