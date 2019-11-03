package com.codeforcommunity.dto;

public class NoteRequest {

  private ContentNote note;

  public NoteRequest(ContentNote note) {
    this.note = note;
  }

  private NoteRequest() {}

  public ContentNote getNote() {
    return note;
  }

  public void setNote(ContentNote note) {
    this.note = note;
  }
}
