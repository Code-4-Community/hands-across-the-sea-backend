package com.codeforcommunity.processor.authenticated;

import static org.jooq.generated.Tables.BOOK_LOGS;

import com.codeforcommunity.api.authenticated.IProtectedBookLogProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.school.BookLog;
import com.codeforcommunity.dto.school.BookLogListResponse;
import com.codeforcommunity.dto.school.UpsertBookLogRequest;
import com.codeforcommunity.exceptions.AdminOnlyRouteException;
import com.codeforcommunity.exceptions.BookLogDoesNotExistException;
import com.codeforcommunity.exceptions.SchoolDoesNotExistException;
import com.codeforcommunity.util.ProcessorUtility;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.BookLogsRecord;
import org.jooq.generated.tables.records.SchoolsRecord;

public class ProtectedBookLogProcessorImpl implements IProtectedBookLogProcessor {

  private final DSLContext db;
  private final ProcessorUtility util;

  public ProtectedBookLogProcessorImpl(DSLContext db) {
    this.db = db;
    this.util = new ProcessorUtility(db);
  }

  @Override
  public BookLog createBookLog(JWTData userData, int schoolId, UpsertBookLogRequest request) {
    if (!userData.isAdmin()) {
      throw new AdminOnlyRouteException();
    }

    SchoolsRecord school = this.util.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    Integer count = request.getCount();
    Timestamp date = request.getDate();
    String notes = request.getNotes();

    BookLogsRecord log = db.newRecord(BOOK_LOGS);
    log.setSchoolId(schoolId);
    log.setCount(count);
    log.setDate(date);
    log.setNotes(notes);
    log.store();

    return new BookLog(log.getId(), log.getCount(), log.getDate(), log.getNotes());
  }

  @Override
  public BookLog updateBookLog(
      JWTData userData, int schoolId, int bookId, UpsertBookLogRequest request) {
    if (!userData.isAdmin()) {
      throw new AdminOnlyRouteException();
    }
    SchoolsRecord school = this.util.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    BookLogsRecord log =
        db.selectFrom(BOOK_LOGS)
            .where(BOOK_LOGS.DELETED_AT.isNull())
            .and(BOOK_LOGS.ID.eq(bookId))
            .fetchOne();

    if (log == null) {
      throw new BookLogDoesNotExistException(bookId);
    }

    Integer count = request.getCount();
    Timestamp date = request.getDate();
    String notes = request.getNotes();
    log.setSchoolId(schoolId);
    log.setCount(count);
    log.setDate(date);
    log.setNotes(notes);
    log.store();

    return new BookLog(log.getId(), log.getCount(), log.getDate(), log.getNotes());
  }

  @Override
  public void deleteBookLog(JWTData userData, int schoolId, int bookId) {
    if (!userData.isAdmin()) {
      throw new AdminOnlyRouteException();
    }
    SchoolsRecord school = this.util.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    BookLogsRecord log = db.selectFrom(BOOK_LOGS).where(BOOK_LOGS.ID.eq(bookId)).fetchOne();

    if (log == null) {
      throw new BookLogDoesNotExistException(bookId);
    }

    log.setDeletedAt(Timestamp.from(Instant.now()));
    log.store();
  }

  @Override
  public BookLogListResponse getBookLog(JWTData userData, int schoolId) {
    SchoolsRecord school = this.util.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    List<BookLog> logs =
        db.selectFrom(BOOK_LOGS)
            .where(BOOK_LOGS.SCHOOL_ID.eq(schoolId))
            .and(BOOK_LOGS.DELETED_AT.isNull())
            .fetchInto(BookLog.class);

    return (logs != null)
        ? new BookLogListResponse(logs)
        : new BookLogListResponse(new ArrayList<>());
  }
}
