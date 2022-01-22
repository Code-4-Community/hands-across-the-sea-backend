package com.codeforcommunity.util;

import static org.jooq.generated.Tables.SCHOOLS;
import static org.jooq.generated.Tables.SCHOOL_CONTACTS;
import static org.jooq.generated.Tables.USERS;

import com.codeforcommunity.dto.school.SchoolContact;
import com.codeforcommunity.exceptions.SchoolDoesNotExistException;
import com.codeforcommunity.exceptions.UserDoesNotExistException;
import com.codeforcommunity.logger.SLogger;
import com.codeforcommunity.processor.authenticated.ProtectedSchoolProcessorImpl;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.SchoolsRecord;
import org.jooq.generated.tables.records.UsersRecord;

public class ProcessorUtility {
  protected final SLogger logger = new SLogger(ProtectedSchoolProcessorImpl.class);
  protected final DSLContext db;

  public ProcessorUtility(DSLContext db) {
    this.db = db;
  }

  public String getSchoolName(int schoolId) {
    SchoolsRecord schoolRecord = db.selectFrom(SCHOOLS).where(SCHOOLS.ID.eq(schoolId)).fetchOne();
    if (schoolRecord == null) {
      logger.error(String.format("No school name found for schoolId:  %d", schoolId));
      throw new SchoolDoesNotExistException(schoolId);
    }
    return schoolRecord.getName();
  }

  public String getUserName(int userId) {
    UsersRecord userRecord = db.selectFrom(USERS).where(USERS.ID.eq(userId)).fetchOne();
    if (userRecord == null) {
      logger.error(String.format("No username found for userId:  %d", userId));
      throw new UserDoesNotExistException(userId);
    }
    return userRecord.getFirstName() + " " + userRecord.getLastName();
  }

  public SchoolsRecord queryForSchool(int schoolId) {
    return db.selectFrom(SCHOOLS)
        .where(SCHOOLS.ID.eq(schoolId))
        .and(SCHOOLS.DELETED_AT.isNull())
        .fetchOne();
  }

  public List<SchoolContact> queryForSchoolContacts(int schoolId) {
    return db.selectFrom(SCHOOL_CONTACTS)
        .where(SCHOOL_CONTACTS.DELETED_AT.isNull())
        .and(SCHOOL_CONTACTS.SCHOOL_ID.eq(schoolId))
        .fetchInto(SchoolContact.class);
  }
}
