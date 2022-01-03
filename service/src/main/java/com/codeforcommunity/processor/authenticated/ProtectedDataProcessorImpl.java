package com.codeforcommunity.processor.authenticated;

import static org.jooq.generated.Tables.SCHOOLS;
import static org.jooq.generated.Tables.USERS;

import com.codeforcommunity.api.authenticated.IProtectedDataProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dataaccess.SchoolDatabaseOperations;
import com.codeforcommunity.dto.data.MetricGeneric;
import com.codeforcommunity.dto.data.MetricsCountryResponse;
import com.codeforcommunity.dto.data.MetricsSchoolResponse;
import com.codeforcommunity.dto.data.MetricsTotalResponse;
import com.codeforcommunity.dto.report.ReportGeneric;
import com.codeforcommunity.dto.report.ReportWithLibrary;
import com.codeforcommunity.enums.Country;
import com.codeforcommunity.enums.LibraryStatus;
import com.codeforcommunity.enums.PrivilegeLevel;
import com.codeforcommunity.exceptions.SchoolDoesNotExistException;
import com.codeforcommunity.logger.SLogger;
import java.util.ArrayList;
import java.util.List;
import org.jooq.DSLContext;

public class ProtectedDataProcessorImpl implements IProtectedDataProcessor {

  private final SLogger logger = new SLogger(ProtectedDataProcessorImpl.class);
  private final SchoolDatabaseOperations schoolDatabaseOperations;
  private final DSLContext db;

  public ProtectedDataProcessorImpl(DSLContext db) {
    this.schoolDatabaseOperations = new SchoolDatabaseOperations(db);
    this.db = db;
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

    Float avgCountBooksPerStudent = this.getCountryBooksPerStudentAverage(country, schoolReports);
    Float avgCountStudentLibrariansPerSchool =
        this.getCountryStudentLibrariansPerSchoolAverage(country, schoolReports);

    int countSchoolsWithLibrary =
        db.fetchCount(
            db.selectFrom(SCHOOLS)
                .where(SCHOOLS.HIDDEN.isFalse())
                .and(SCHOOLS.DELETED_AT.isNull())
                .and(SCHOOLS.COUNTRY.eq(country))
                .and(SCHOOLS.LIBRARY_STATUS.eq(LibraryStatus.EXISTS)));

    float percentSchoolsWithLibraries =
        (countSchools > 0) ? ((float) countSchoolsWithLibrary / (float) countSchools) * 100 : 0;

    MetricGeneric metricGeneric = getGenericMetrics(schoolIds);

    return new MetricsCountryResponse(
        countSchools,
        countVolunteerAccounts,
        countOfficerAccounts,
        countAdminAccounts,
        avgCountBooksPerStudent,
        avgCountStudentLibrariansPerSchool,
        percentSchoolsWithLibraries,
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
    List<ReportGeneric> reports = new ArrayList<ReportGeneric>();

    for (int schoolId : schoolIds) {
      // For each school, get the most recent report
      ReportGeneric report = schoolDatabaseOperations.getMostRecentReport(schoolId);

      if (report == null) {
        logger.info(String.format("No report found for school with ID `%d`", schoolId));
        continue;
      }

      reports.add(report);
    }

    return reports;
  }

  private Float getCountryBooksPerStudentAverage(
      Country country, List<ReportGeneric> schoolReports) {
    List<Float> schoolAveragesBooksPerStudent = new ArrayList<Float>();

    for (ReportGeneric report : schoolReports) {
      // For each report, calculate books per student
      Integer countBooks = report.getNumberOfBooks();
      Integer countStudents = report.getNumberOfChildren();

      if (countBooks == null || countStudents == null) {
        logger.info(
            String.format(
                "School report with ID `%d` missing count books or count students",
                report.getId()));
        continue;
      }

      float schoolAvg = ((float) countBooks) / ((float) countStudents);
      schoolAveragesBooksPerStudent.add(schoolAvg);
    }

    if (schoolAveragesBooksPerStudent.isEmpty()) {
      return null;
    }

    return (float) schoolAveragesBooksPerStudent.stream().mapToDouble(d -> d).average().orElse(0.0);
  }

  // gets total books and students from a list of schools
  private MetricGeneric getGenericMetrics(List<Integer> schoolIds) {
    Integer totalBooks = 0;
    Integer totalStudents = 0;

    for (Integer schoolId : schoolIds) {
      totalBooks += schoolDatabaseOperations.getMostRecentReport(schoolId).getNumberOfBooks();
      totalStudents += schoolDatabaseOperations.getMostRecentReport(schoolId).getNumberOfChildren();
    }
    return new MetricGeneric(totalBooks, totalStudents);
  }

  private Float getCountryStudentLibrariansPerSchoolAverage(
      Country country, List<ReportGeneric> schoolReports) {
    int totalCountStudentLibrarians = 0;
    int totalCountSchoolsWithLibraries = 0; // TODO: SHOULD THIS BE ALL SCHOOLS

    for (ReportGeneric report : schoolReports) {
      // For each report, get count student librarians
      if (report.getLibraryStatus() != LibraryStatus.EXISTS
          || !(report instanceof ReportWithLibrary)) {
        // Skip reports with no libraries

        logger.info(
            String.format(
                "Skipping school report with ID `%d` since it has no library", report.getId()));
        continue;
      }

      ReportWithLibrary reportWithLibrary = (ReportWithLibrary) report;

      Integer numStudentLibrarians = reportWithLibrary.getNumberOfStudentLibrarians();
      if (numStudentLibrarians == null) {
        logger.info(
            String.format(
                "Skipping school report with ID `%d` since it has a `null` student librarian count",
                report.getId()));
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
}
