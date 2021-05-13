package com.codeforcommunity.processor.authenticated;

import static org.jooq.generated.Tables.SCHOOLS;
import static org.jooq.generated.Tables.USERS;

import com.codeforcommunity.api.authenticated.IProtectedDataProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dataaccess.SchoolDatabaseOperations;
import com.codeforcommunity.dto.data.MetricsCountryResponse;
import com.codeforcommunity.dto.data.MetricsSchoolResponse;
import com.codeforcommunity.dto.report.ReportGeneric;
import com.codeforcommunity.dto.report.ReportWithLibrary;
import com.codeforcommunity.enums.Country;
import com.codeforcommunity.enums.LibraryStatus;
import com.codeforcommunity.enums.PrivilegeLevel;
import com.codeforcommunity.exceptions.SchoolDoesNotExistException;
import org.jooq.DSLContext;

public class ProtectedDataProcessorImpl implements IProtectedDataProcessor {
  private final SchoolDatabaseOperations schoolDatabaseOperations;
  private final DSLContext db;

  public ProtectedDataProcessorImpl(DSLContext db) {
    this.schoolDatabaseOperations = new SchoolDatabaseOperations(db);
    this.db = db;
  }

  @Override
  public MetricsCountryResponse getFixedCountryMetrics(JWTData userData, Country country) {
    int countSchools =
        db.fetchCount(
            db.selectFrom(SCHOOLS)
                .where(SCHOOLS.HIDDEN.isFalse())
                .and(SCHOOLS.DELETED_AT.isNull())
                .and(SCHOOLS.COUNTRY.eq(country)));

    int countVolunteerAccounts =
        db.fetchCount(
            db.selectFrom(USERS)
                .where(USERS.DELETED_AT.isNull())
                .and(USERS.COUNTRY.eq(country))
                .and(USERS.PRIVILEGE_LEVEL.eq(PrivilegeLevel.STANDARD)));

    int countAdminAccounts =
        db.fetchCount(
            db.selectFrom(USERS)
                .where(USERS.DELETED_AT.isNull())
                .and(USERS.COUNTRY.eq(country))
                .and(USERS.PRIVILEGE_LEVEL.eq(PrivilegeLevel.ADMIN)));

    Float avgCountBooksPerStudent = null; // TODO
    Float avgCountStudentLibrariansPerSchool = null; // TODO

    int countSchoolsWithLibrary =
        db.fetchCount(
            db.selectFrom(SCHOOLS)
                .where(SCHOOLS.HIDDEN.isFalse())
                .and(SCHOOLS.DELETED_AT.isNull())
                .and(SCHOOLS.COUNTRY.eq(country))
                .and(SCHOOLS.LIBRARY_STATUS.eq(LibraryStatus.EXISTS)));

    float percentSchoolsWithLibraries =
        (countSchools > 0) ? ((float) countSchoolsWithLibrary / (float) countSchools) * 100 : 0;

    return new MetricsCountryResponse(
        countSchools,
        countVolunteerAccounts,
        countAdminAccounts,
        avgCountBooksPerStudent,
        avgCountStudentLibrariansPerSchool,
        percentSchoolsWithLibraries);
  }

  @Override
  public MetricsSchoolResponse getFixedSchoolMetrics(JWTData userData, int schoolId) {
    ReportGeneric report;

    try {
      report = schoolDatabaseOperations.getMostRecentReport(schoolId);
    } catch (IllegalArgumentException e) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    Integer countBooks = report.getNumberOfBooks();
    Integer countStudents = report.getNumberOfChildren();

    Float countBooksPerStudent =
        (countBooks != null && countStudents != null)
            ? ((float) countBooks / (float) countStudents)
            : null;

    Integer countStudentLibrarians =
        (report instanceof ReportWithLibrary)
            ? ((ReportWithLibrary) report).getNumberOfStudentLibrarians()
            : null;

    Integer netBooksInOut = null; // TODO

    return new MetricsSchoolResponse(
        countBooksPerStudent, countStudents, countStudentLibrarians, netBooksInOut);
  }
}
