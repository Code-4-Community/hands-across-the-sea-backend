package com.codeforcommunity.dataaccess;

import static org.jooq.generated.Tables.SCHOOLS;
import static org.jooq.generated.Tables.SCHOOL_CONTACTS;
import static org.jooq.generated.Tables.SCHOOL_REPORTS_WITHOUT_LIBRARIES;
import static org.jooq.generated.Tables.SCHOOL_REPORTS_WITH_LIBRARIES;

import com.codeforcommunity.dto.report.ReportGeneric;
import com.codeforcommunity.dto.report.ReportWithLibrary;
import com.codeforcommunity.dto.report.ReportWithoutLibrary;
import com.codeforcommunity.dto.school.SchoolContact;
import com.codeforcommunity.enums.LibraryStatus;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.SchoolsRecord;

public class SchoolDatabaseOperations {

  private final DSLContext db;

  public SchoolDatabaseOperations(DSLContext db) {
    this.db = db;
  }

  public SchoolsRecord getSchool(int schoolId) {
    return db.selectFrom(SCHOOLS)
        .where(SCHOOLS.ID.eq(schoolId))
        .and(SCHOOLS.DELETED_AT.isNull())
        .fetchOne();
  }

  public List<SchoolContact> getSchoolContacts(int schoolId) {
    return db.selectFrom(SCHOOL_CONTACTS)
        .where(SCHOOL_CONTACTS.DELETED_AT.isNull())
        .and(SCHOOL_CONTACTS.SCHOOL_ID.eq(schoolId))
        .fetchInto(SchoolContact.class);
  }

  public ReportGeneric getMostRecentReport(int schoolId) throws IllegalArgumentException {
    SchoolsRecord school = this.getSchool(schoolId);
    if (school == null) {
      throw new IllegalArgumentException(
          String.format("School with ID %d does not exist", schoolId));
    }

    ReportGeneric report = null;
    LibraryStatus libraryStatus = school.getLibraryStatus();

    if (libraryStatus == LibraryStatus.EXISTS) {
      //  System.out.println("Inside of library exists");
      report =
          db.selectFrom(SCHOOL_REPORTS_WITH_LIBRARIES)
              .where(SCHOOL_REPORTS_WITH_LIBRARIES.DELETED_AT.isNull())
              .and(SCHOOL_REPORTS_WITH_LIBRARIES.SCHOOL_ID.eq(schoolId))
              .orderBy(SCHOOL_REPORTS_WITH_LIBRARIES.ID.desc())
              .limit(1)
              .fetchOneInto(ReportWithLibrary.class);
    } else if (libraryStatus == LibraryStatus.DOES_NOT_EXIST) {
      // System.out.println("Inside of library does not exists");
      report =
          db.selectFrom(SCHOOL_REPORTS_WITHOUT_LIBRARIES)
              .where(SCHOOL_REPORTS_WITHOUT_LIBRARIES.DELETED_AT.isNull())
              .and(SCHOOL_REPORTS_WITHOUT_LIBRARIES.SCHOOL_ID.eq(schoolId))
              .orderBy(SCHOOL_REPORTS_WITHOUT_LIBRARIES.ID.desc())
              .limit(1)
              .fetchOneInto(ReportWithoutLibrary.class);
    }

    // System.out.println("The report number of books has a value of " + report.getNumberOfBooks());
    return report;
  }
}
