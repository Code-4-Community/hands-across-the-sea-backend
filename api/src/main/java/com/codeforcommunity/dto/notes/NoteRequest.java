package com.codeforcommunity.dto.notes;

import com.codeforcommunity.dto.ApiDto;
import java.util.ArrayList;
import java.util.List;

public class NoteRequest extends ApiDto {

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

  @Override
  public List<String> validateFields(String fieldPrefix) {
    String fieldName = fieldPrefix + "note_request.";
    List<String> fields = new ArrayList<>();

    if (note == null) {
      fields.add(fieldName + "note");
    } else {
      fields.addAll(note.validateFields(fieldName));
    }
    return fields;
  }
}
