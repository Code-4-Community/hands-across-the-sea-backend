package com.codeforcommunity.dto.data;

public class MetricsCountryResponse {
  private Integer countSchools;
  private Integer countVolunteerAccounts;
  private Integer countAdminAccounts;
  private Float avgCountBooksPerStudent;
  private Float avgCountStudentLibrariansPerSchool;
  private Float percentSchoolsWithLibraries;

  public MetricsCountryResponse(
      Integer countSchools,
      Integer countVolunteerAccounts,
      Integer countAdminAccounts,
      Float avgCountBooksPerStudent,
      Float avgCountStudentLibrariansPerSchool,
      Float percentSchoolsWithLibraries) {
    this.countSchools = countSchools;
    this.countVolunteerAccounts = countVolunteerAccounts;
    this.countAdminAccounts = countAdminAccounts;
    this.avgCountBooksPerStudent = avgCountBooksPerStudent;
    this.avgCountStudentLibrariansPerSchool = avgCountStudentLibrariansPerSchool;
    this.percentSchoolsWithLibraries = percentSchoolsWithLibraries;
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
}
