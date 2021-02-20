package com.codeforcommunity.dto.school;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UpsertBookLogRequest extends ApiDto {

  private Integer count;
  private Timestamp date;
  private String notes;

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

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    List<String> fields = new ArrayList<String>();
    if (count == null || count == 0) {
      fields.add("count");
    }
    return fields;
  }
}
