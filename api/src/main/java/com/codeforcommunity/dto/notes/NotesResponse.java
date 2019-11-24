package com.codeforcommunity.dto.notes;

import java.util.List;

public class NotesResponse {

  private String status;
  private List<FullNote> notes;

  public NotesResponse(String status, List<FullNote> notes) {
    this.status = status;
    this.notes = notes;
  }

  private NotesResponse() {}

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public List<FullNote> getNotes() {
    return notes;
  }

  public void setNotes(List<FullNote> notes) {
    this.notes = notes;
  }
}
