package com.codeforcommunity.dto.data;

public class MetricsTotalResponse {
  private Integer countSchools;
  private Integer countBooks;

  public MetricsTotalResponse(
      Integer countSchools,
      Integer countBooks
  ) {
    this.countSchools = countSchools;
    this.countBooks = countBooks;
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
}
