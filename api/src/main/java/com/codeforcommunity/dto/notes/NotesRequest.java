package com.codeforcommunity.dto.notes;

import com.codeforcommunity.api.ApiDto;
import java.util.ArrayList;
import java.util.List;

public class NotesRequest extends ApiDto {
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

  @Override
  public List<String> validateFields(String fieldPrefix) {
    String fieldName = fieldPrefix + "notes_request.";
    List<String> fields = new ArrayList<>();

    if (notes == null) {
      fields.add(fieldName + "notes");
    } else {
      for (ContentNote note : notes) {
        fields.addAll(note.validateFields(fieldName));
      }
    }
    return fields;
  }
}
