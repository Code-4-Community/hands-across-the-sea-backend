package com.codeforcommunity.dto.notes;

import com.codeforcommunity.dto.ApiDto;
import java.util.ArrayList;
import java.util.List;

public class ContentNote extends ApiDto {
  private String title;
  private String content;

  public ContentNote(String title, String content) {
    this.title = title;
    this.content = content;
  }

  private ContentNote() {}

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) {
    String fieldName = fieldPrefix + "content_note.";
    List<String> fields = new ArrayList<>();

    if (isEmpty(title)) {
      fields.add(fieldName + "title");
    }
    if (content == null) {
      fields.add(fieldName + "content");
    }
    return fields;
  }
}
