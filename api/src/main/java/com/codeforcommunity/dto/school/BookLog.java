package com.codeforcommunity.dto.school;

import java.sql.Timestamp;
import java.util.Date;

public class BookLog {

  private Integer count;
  private Timestamp date;
  private String notes;
  private Integer id;

  public BookLog() {}

  public BookLog(Integer id, Integer count, Timestamp date, String notes) {
    this.id = id;
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

  public String getDate() {
    return new Date(date.getTime()).toString();
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
