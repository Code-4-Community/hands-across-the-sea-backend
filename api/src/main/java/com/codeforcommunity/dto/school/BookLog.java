package com.codeforcommunity.dto.school;

import java.sql.Timestamp;

public class BookLog {

  private Integer count;
  private Timestamp date;
  private String notes;

  public BookLog(Integer count, Timestamp date, String notes) {
    this.count = count;
    this.date = date;
    this.notes = notes;
  }

  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public Timestamp getDate() {
    return date;
  }

  public void setDate(Timestamp date) {
    this.date = date;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }
}
