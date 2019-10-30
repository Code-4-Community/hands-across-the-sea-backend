package com.codeforcommunity.dto;

import java.util.List;

public class NotesResponse {

  private String status;
  private List<NoteReturn> notes;

  public NotesResponse(String status, List<NoteReturn> notes) {
    this.status = status;
    this.notes = notes;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public List<NoteReturn> getNotes() {
    return notes;
  }

  public void setNotes(List<NoteReturn> notes) {
    this.notes = notes;
  }
}
