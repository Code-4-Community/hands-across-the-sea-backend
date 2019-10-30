package com.codeforcommunity.dto;

import java.util.List;

public class NoteRequest {
  private List<ContentNote> notes;

  public NoteRequest(List<ContentNote> notes) {
    this.notes = notes;
  }

  private NoteRequest() {}

  public List<ContentNote> getNotes() {
    return notes;
  }

  public void setNotes(List<ContentNote> notes) {
    this.notes = notes;
  }
}
