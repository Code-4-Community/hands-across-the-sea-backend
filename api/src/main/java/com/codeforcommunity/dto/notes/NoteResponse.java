package com.codeforcommunity.dto.notes;

public class NoteResponse {

  private String status;
  private FullNote note;

  public NoteResponse(String status, FullNote note) {
    this.status = status;
    this.note = note;
  }

  private NoteResponse() {}

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public FullNote getNote() {
    return note;
  }

  public void setNote(FullNote note) {
    this.note = note;
  }
}
