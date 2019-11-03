package com.codeforcommunity.dto;

import java.util.List;

public class NotesRequest {
  private List<ContentNote> notes;

  public NotesRequest(List<ContentNote> notes) {
    this.notes = notes;
  }

  private NotesRequest() {}

  public List<ContentNote> getNotes() {
    return notes;
  }

  public void setNotes(List<ContentNote> notes) {
    this.notes = notes;
  }
}
