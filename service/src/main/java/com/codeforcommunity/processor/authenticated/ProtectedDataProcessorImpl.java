package com.codeforcommunity.processor.authenticated;

import static org.jooq.generated.Tables.SCHOOLS;
import static org.jooq.generated.Tables.USERS;

import com.codeforcommunity.api.authenticated.IProtectedDataProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dataaccess.BookLogDatabaseOperations;
import com.codeforcommunity.dataaccess.SchoolDatabaseOperations;
import com.codeforcommunity.dto.data.MetricGeneric;
import com.codeforcommunity.dto.data.MetricsCountryResponse;
import com.codeforcommunity.dto.data.MetricsSchoolResponse;
import com.codeforcommunity.dto.data.MetricsTotalResponse;
import com.codeforcommunity.dto.report.ReportGeneric;
import com.codeforcommunity.dto.report.ReportWithLibrary;
import com.codeforcommunity.dto.school.BookLog;
import com.codeforcommunity.enums.Country;
import com.codeforcommunity.enums.LibraryStatus;
import com.codeforcommunity.enums.PrivilegeLevel;
import com.codeforcommunity.exceptions.SchoolDoesNotExistException;
import java.util.ArrayList;
import java.util.List;
import org.jooq.DSLContext;

public class ProtectedDataProcessorImpl implements IProtectedDataProcessor {

  private final SchoolDatabaseOperations schoolDatabaseOperations;
  private final DSLContext db;
  private final BookLogDatabaseOperations bookLogDb;

  public ProtectedDataProcessorImpl(DSLContext db) {
    this.schoolDatabaseOperations = new SchoolDatabaseOperations(db);
    this.db = db;
    this.bookLogDb = new BookLogDatabaseOperations(db);
  }

  @Override
  public MetricsTotalResponse getFixedTotalMetrics(JWTData userData) {
    int countSchools =
        db.fetchCount(
            db.selectFrom(SCHOOLS)
                .where(SCHOOLS.HIDDEN.isFalse())
                .and(SCHOOLS.DELETED_AT.isNull()));

    List<Integer> schoolIds =
        db.selectFrom(SCHOOLS)
            .where(SCHOOLS.HIDDEN.isFalse())
            .and(SCHOOLS.DELETED_AT.isNull())
            .fetch(SCHOOLS.ID);

    MetricGeneric metricGeneric = getGenericMetrics(schoolIds);

    return new MetricsTotalResponse(
        countSchools, metricGeneric.getTotalBooks(), metricGeneric.getTotalStudents());
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
                .and(USERS.PRIVILEGE_LEVEL.eq(PrivilegeLevel.VOLUNTEER)));

    int countOfficerAccounts =
        db.fetchCount(
            db.selectFrom(USERS)
                .where(USERS.DELETED_AT.isNull())
                .and(USERS.COUNTRY.eq(country))
                .and(USERS.PRIVILEGE_LEVEL.eq(PrivilegeLevel.OFFICER)));

    int countAdminAccounts =
        db.fetchCount(
            db.selectFrom(USERS)
                .where(USERS.DELETED_AT.isNull())
                .and(USERS.COUNTRY.eq(country))
                .and(USERS.PRIVILEGE_LEVEL.eq(PrivilegeLevel.ADMIN)));

    List<ReportGeneric> schoolReports = this.getCountryReports(country);

    List<Integer> schoolIds =
        db.selectFrom(SCHOOLS)
            .where(SCHOOLS.HIDDEN.isFalse())
            .and(SCHOOLS.DELETED_AT.isNull())
            .and(SCHOOLS.COUNTRY.eq(country))
            .fetch(SCHOOLS.ID);

    Float avgCountStudentLibrariansPerSchool =
        this.getCountryStudentLibrariansPerSchoolAverage(schoolReports);

    int countSchoolsWithLibrary =
        db.fetchCount(
            db.selectFrom(SCHOOLS)
                .where(SCHOOLS.HIDDEN.isFalse())
                .and(SCHOOLS.DELETED_AT.isNull())
                .and(SCHOOLS.COUNTRY.eq(country))
                .and(SCHOOLS.LIBRARY_STATUS.eq(LibraryStatus.EXISTS)));

    float percentSchoolsWithLibraries =
        (countSchools > 0) ? ((float) countSchoolsWithLibrary / (float) countSchools) * 100 : 0;
    Float percentOfSchoolsWithLibraries =
        percentSchoolsWithLibraries == 0 ? null : percentSchoolsWithLibraries;
    MetricGeneric metricGeneric = getGenericMetrics(schoolIds);
    Float avgCountBooksPerStudent =
        (metricGeneric.getTotalBooks() != null && metricGeneric.getTotalStudents() != null && metricGeneric.getTotalStudents() != 0)
        ? metricGeneric.getTotalBooks() / metricGeneric.getTotalStudents().floatValue() : null;
    return new MetricsCountryResponse(
        countSchools,
        countVolunteerAccounts,
        countOfficerAccounts,
        countAdminAccounts,
        avgCountBooksPerStudent,
        avgCountStudentLibrariansPerSchool,
        percentOfSchoolsWithLibraries,
        metricGeneric.getTotalStudents(),
        metricGeneric.getTotalBooks());
  }

  @Override
  public MetricsSchoolResponse getFixedSchoolMetrics(JWTData userData, int schoolId) {
    ReportGeneric report;

    try {
      report = schoolDatabaseOperations.getMostRecentReport(schoolId);
    } catch (IllegalArgumentException e) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    Integer countBooks = bookLogDb.getTotalNumberOfBooksForSchool(schoolId);
    Integer countStudents = schoolDatabaseOperations.getTotalNumberOfStudents(schoolId);
    Integer netBooksInOut = calculcateNetBooksIn(schoolId);

    Float countBooksPerStudent =
        (countBooks != null && countStudents != null && countStudents != 0)
            ? countBooks.floatValue() / countStudents.floatValue()
            : null;

    if (report == null) {
      return new MetricsSchoolResponse(countBooksPerStudent, countStudents, null, netBooksInOut, countBooks);
    }

    Integer countStudentLibrarians =
        (report instanceof ReportWithLibrary)
            ? ((ReportWithLibrary) report).getNumberOfStudentLibrarians()
            : null;
    return new MetricsSchoolResponse(
        countBooksPerStudent, countStudents, countStudentLibrarians, netBooksInOut, countBooks);
  }

  private List<ReportGeneric> getCountryReports(Country country) {
    // Get all schools for this country that are not deleted or hidden
    List<Integer> schoolIds =
        db.selectFrom(SCHOOLS)
            .where(SCHOOLS.DELETED_AT.isNull())
            .and(SCHOOLS.HIDDEN.isFalse())
            .and(SCHOOLS.COUNTRY.eq(country))
            .fetch(SCHOOLS.ID);

    return getReports(schoolIds);
  }

  private List<ReportGeneric> getReports(List<Integer> schoolIds) {
    List<ReportGeneric> reports = new ArrayList<>();
    if (schoolIds == null || schoolIds.size() == 0) {
      return reports;
    }
    for (int schoolId : schoolIds) {
      // For each school, get the most recent report
      ReportGeneric report = schoolDatabaseOperations.getMostRecentReport(schoolId);
      if (report != null) {
        reports.add(report);
      }
    }

    return reports;
  }

  // gets total books and students from a list of schools
  private MetricGeneric getGenericMetrics(List<Integer> schoolIds) {
    if (schoolIds == null || schoolIds.size() == 0) {
      return new MetricGeneric(null, null);
    }
    boolean updatedBookCount = false;
    boolean updatedStudentCount = false;
    Integer totalBooks = 0;
    Integer totalStudents = 0;

    for (Integer schoolId : schoolIds) {
      Integer schoolBookTotal = bookLogDb.getTotalNumberOfBooksForSchool(schoolId);
      Integer schoolStudentTotal = schoolDatabaseOperations.getTotalNumberOfStudents(schoolId);
      if (schoolBookTotal != null) {
        updatedBookCount = true;
        totalBooks += schoolBookTotal;
      }
      if (schoolStudentTotal != null) {
        updatedStudentCount = true;
        totalStudents += schoolStudentTotal;
      }
    }

    if (!updatedBookCount) {
      totalBooks = null;
    }
    if (!updatedStudentCount) {
      totalStudents = null;
    }
    return new MetricGeneric(totalBooks, totalStudents);
  }

  private Float getCountryStudentLibrariansPerSchoolAverage(List<ReportGeneric> schoolReports) {
    int totalCountStudentLibrarians = 0;
    int totalCountSchoolsWithLibraries = 0; // TODO: SHOULD THIS BE ALL SCHOOLS

    for (ReportGeneric report : schoolReports) {
      // For each report, get count student librarians
      if (report.getLibraryStatus() != LibraryStatus.EXISTS
          || !(report instanceof ReportWithLibrary)) {
        continue;
      }

      ReportWithLibrary reportWithLibrary = (ReportWithLibrary) report;

      Integer numStudentLibrarians = reportWithLibrary.getNumberOfStudentLibrarians();
      if (numStudentLibrarians == null) {
        continue;
      }

      // Otherwise, increment count of schools and add the number of librarians
      totalCountStudentLibrarians += numStudentLibrarians;
      totalCountSchoolsWithLibraries++;
    }

    if (totalCountSchoolsWithLibraries == 0) {
      return null;
    }

    return (float) totalCountStudentLibrarians / (float) totalCountSchoolsWithLibraries;
  }

  private Integer calculcateNetBooksIn(int schoolId) {
    List<BookLog> schoolBookLogs = bookLogDb.getAllBookLogsForASchool(schoolId);
    if (schoolBookLogs == null || schoolBookLogs.size() == 0) {
      return null;
    }
    Integer numberOfBookLogs = schoolBookLogs.size();
    Integer totalDifferenceOfBooks = 0;
    for (BookLog bookLog : schoolBookLogs) {
      totalDifferenceOfBooks += bookLog.getCount();
    }
    return totalDifferenceOfBooks / numberOfBookLogs;
  }
}
