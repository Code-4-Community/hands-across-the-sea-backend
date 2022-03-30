package com.codeforcommunity.dataaccess;

import static org.jooq.generated.Tables.BOOK_LOGS;

import com.codeforcommunity.dto.school.BookLog;
import java.util.List;
import org.jooq.DSLContext;

public class BookLogDatabaseOperations {

  private final DSLContext db;

  public BookLogDatabaseOperations(DSLContext db) {
    this.db = db;
  }

  public Integer getTotalNumberOfBooksForSchool(int schoolId) {
    Integer bookCount = 0;
    List<BookLog> logs =
        db.selectFrom(BOOK_LOGS)
            .where(BOOK_LOGS.SCHOOL_ID.eq(schoolId))
            .and(BOOK_LOGS.DELETED_AT.isNull())
            .fetchInto(BookLog.class);
    if (logs == null) {
      return null;
    }
    for (BookLog log : logs) {
      bookCount += log.getCount();
    }
    return bookCount;
  }


  public List<BookLog> getAllBookLogsForASchool(int schoolId) {
    List<BookLog> logs =
        db.selectFrom(BOOK_LOGS)
            .where(BOOK_LOGS.SCHOOL_ID.eq(schoolId))
            .and(BOOK_LOGS.DELETED_AT.isNull())
            .fetchInto(BookLog.class);
    return logs;
  }

}
