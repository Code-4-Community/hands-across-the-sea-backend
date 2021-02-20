package com.codeforcommunity.dto.school;

import java.util.List;

public class BookLogListResponse {

  private int count;
  private List<BookLog> bookLogs;

  public BookLogListResponse(List<BookLog> bookLogs) {
    if (bookLogs == null) {
      throw new IllegalArgumentException("Given `null` list of book logs");
    }

    this.bookLogs = bookLogs;
    this.count = bookLogs.size();
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public List<BookLog> getBookLogs() {
    return bookLogs;
  }

  public void setBookLogs(List<BookLog> bookLogs) {
    this.bookLogs = bookLogs;
  }
}
