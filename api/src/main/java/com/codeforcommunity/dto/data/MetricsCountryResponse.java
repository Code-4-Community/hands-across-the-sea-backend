package com.codeforcommunity.dto.data;

public class MetricsCountryResponse {
  private Integer countSchools;
  private Integer countVolunteerAccounts;
  private Integer countAdminAccounts;
  private Float avgCountBooksPerStudent;
  private Float avgCountStudentLibrariansPerSchool;
  private Float percentSchoolsWithLibraries;
  private Integer countStudents;
  private Integer countBooks;

  public MetricsCountryResponse(
      Integer countSchools,
      Integer countVolunteerAccounts,
      Integer countAdminAccounts,
      Float avgCountBooksPerStudent,
      Float avgCountStudentLibrariansPerSchool,
      Float percentSchoolsWithLibraries,
      Integer countStudents,
      Integer countBooks) {
    this.countSchools = countSchools;
    this.countVolunteerAccounts = countVolunteerAccounts;
    this.countAdminAccounts = countAdminAccounts;
    this.avgCountBooksPerStudent = avgCountBooksPerStudent;
    this.avgCountStudentLibrariansPerSchool = avgCountStudentLibrariansPerSchool;
    this.percentSchoolsWithLibraries = percentSchoolsWithLibraries;
    this.countStudents = countStudents;
    this.countBooks = countBooks;
  }

  public Integer getCountSchools() {
    return countSchools;
  }

  public void setCountSchools(Integer countSchools) {
    this.countSchools = countSchools;
  }

  public Integer getCountVolunteerAccounts() {
    return countVolunteerAccounts;
  }

  public void setCountVolunteerAccounts(Integer countVolunteerAccounts) {
    this.countVolunteerAccounts = countVolunteerAccounts;
  }

  public Integer getCountAdminAccounts() {
    return countAdminAccounts;
  }

  public void setCountAdminAccounts(Integer countAdminAccounts) {
    this.countAdminAccounts = countAdminAccounts;
  }

  public Float getAvgCountBooksPerStudent() {
    return avgCountBooksPerStudent;
  }

  public void setAvgCountBooksPerStudent(Float avgCountBooksPerStudent) {
    this.avgCountBooksPerStudent = avgCountBooksPerStudent;
  }

  public Float getAvgCountStudentLibrariansPerSchool() {
    return avgCountStudentLibrariansPerSchool;
  }

  public void setAvgCountStudentLibrariansPerSchool(Float avgCountStudentLibrariansPerSchool) {
    this.avgCountStudentLibrariansPerSchool = avgCountStudentLibrariansPerSchool;
  }

  public Float getPercentSchoolsWithLibraries() {
    return percentSchoolsWithLibraries;
  }

  public void setPercentSchoolsWithLibraries(Float percentSchoolsWithLibraries) {
    this.percentSchoolsWithLibraries = percentSchoolsWithLibraries;
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
