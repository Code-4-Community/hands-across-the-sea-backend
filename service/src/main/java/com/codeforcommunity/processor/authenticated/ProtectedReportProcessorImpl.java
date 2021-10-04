package com.codeforcommunity.processor.authenticated;

import static org.jooq.generated.Tables.SCHOOL_REPORTS_WITHOUT_LIBRARIES;
import static org.jooq.generated.Tables.SCHOOL_REPORTS_WITH_LIBRARIES;

import com.codeforcommunity.api.authenticated.IProtectedReportProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.CsvSerializer;
import com.codeforcommunity.dto.report.ReportGeneric;
import com.codeforcommunity.dto.report.ReportGenericListResponse;
import com.codeforcommunity.dto.report.ReportWithLibrary;
import com.codeforcommunity.dto.report.ReportWithoutLibrary;
import com.codeforcommunity.dto.report.UpsertReportWithLibrary;
import com.codeforcommunity.dto.report.UpsertReportWithoutLibrary;
import com.codeforcommunity.enums.Grade;
import com.codeforcommunity.enums.LibraryStatus;
import com.codeforcommunity.exceptions.AdminOnlyRouteException;
import com.codeforcommunity.exceptions.CsvSerializerException;
import com.codeforcommunity.exceptions.InvalidShipmentYearException;
import com.codeforcommunity.exceptions.MalformedParameterException;
import com.codeforcommunity.exceptions.NoReportByIdFoundException;
import com.codeforcommunity.exceptions.NoReportFoundException;
import com.codeforcommunity.exceptions.SchoolDoesNotExistException;
import com.codeforcommunity.logger.SLogger;
import com.codeforcommunity.util.ProcessorUtility;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.SchoolReportsWithLibrariesRecord;
import org.jooq.generated.tables.records.SchoolReportsWithoutLibrariesRecord;
import org.jooq.generated.tables.records.SchoolsRecord;

public class ProtectedReportProcessorImpl implements IProtectedReportProcessor  {

  private final SLogger logger = new SLogger(ProtectedSchoolProcessorImpl.class);
  private final DSLContext db;
  private final ProcessorUtility util;

  public ProtectedReportProcessorImpl(DSLContext db) {
    this.db = db;
    this.util = new ProcessorUtility(db);
  }

  @Override
  public ReportWithLibrary createReportWithLibrary(
      JWTData userData, int schoolId, UpsertReportWithLibrary req) {
    SchoolsRecord school = this.util.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    // Update this school to have a new library status
    school.setLibraryStatus(LibraryStatus.EXISTS);
    school.store();

    // Save a record to the school_reports_with_libraries table
    SchoolReportsWithLibrariesRecord newReport = db.newRecord(SCHOOL_REPORTS_WITH_LIBRARIES);
    storeReportWithLibrary(userData, schoolId, req, newReport);
    newReport.refresh();

    return new ReportWithLibrary(
        newReport.getId(),
        newReport.getCreatedAt(),
        newReport.getUpdatedAt(),
        newReport.getSchoolId(),
        userData.getUserId(),
        newReport.getNumberOfChildren(),
        newReport.getNumberOfBooks(),
        newReport.getMostRecentShipmentYear(),
        newReport.getIsSharedSpace(),
        newReport.getHasInvitingSpace(),
        newReport.getAssignedPersonRole(),
        newReport.getAssignedPersonTitle(),
        newReport.getApprenticeshipProgram(),
        newReport.getTrainsAndMentorsApprentices(),
        newReport.getHasCheckInTimetables(),
        newReport.getHasBookCheckoutSystem(),
        newReport.getNumberOfStudentLibrarians(),
        newReport.getReasonNoStudentLibrarians(),
        newReport.getHasSufficientTraining(),
        newReport.getTeacherSupport(),
        newReport.getParentSupport(),
        newReport.getVisitReason(),
        newReport.getActionPlan(),
        newReport.getSuccessStories(),
        req.getGradesAttended(),
        req.getCheckInTimetable(),
        this.util.getUserName(userData.getUserId()),
        this.util.getSchoolName(schoolId),
        req.getCheckOutTimetable(),
        req.getNumberOfStudentLibrariansTrainers());
  }

  @Override
  public void updateReportWithLibrary(
      JWTData userData, int schoolId, int reportId, UpsertReportWithLibrary req) {
    SchoolsRecord school = this.util.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    // Save a record to the school_reports_with_libraries table
    SchoolReportsWithLibrariesRecord newReport =
        db.selectFrom(SCHOOL_REPORTS_WITH_LIBRARIES)
            .where(SCHOOL_REPORTS_WITH_LIBRARIES.ID.eq(reportId))
            .fetchOne();

    if (newReport == null) {
      throw new NoReportFoundException(schoolId);
    }

    if (!userData.isAdmin() && !newReport.getUserId().equals(userData.getUserId())) {
      throw new AdminOnlyRouteException();
    }


    if (req.getMostRecentShipmentYear() != null) {
      if (isShipmentYearInvalid(req.getMostRecentShipmentYear())) {
        throw new InvalidShipmentYearException(req.getMostRecentShipmentYear());
      }
    }

    storeReportWithLibrary(userData, schoolId, req, newReport);
  }

  private void storeReportWithLibrary(
      JWTData userData,
      int schoolId,
      UpsertReportWithLibrary req,
      SchoolReportsWithLibrariesRecord newReport) {
    newReport.setUserId(userData.getUserId());
    newReport.setSchoolId(schoolId);
    newReport.setNumberOfChildren(req.getNumberOfChildren());
    newReport.setNumberOfBooks(req.getNumberOfBooks());
    newReport.setMostRecentShipmentYear(req.getMostRecentShipmentYear());
    newReport.setIsSharedSpace(req.getIsSharedSpace());
    newReport.setHasInvitingSpace(req.getHasInvitingSpace());
    newReport.setAssignedPersonRole(req.getAssignedPersonRole());
    newReport.setAssignedPersonTitle(req.getAssignedPersonTitle());
    newReport.setApprenticeshipProgram(req.getApprenticeshipProgram());
    newReport.setTrainsAndMentorsApprentices(req.getTrainsAndMentorsApprentices());
    newReport.setHasCheckInTimetables(req.getHasCheckInTimetables());
    newReport.setHasBookCheckoutSystem(req.getHasBookCheckoutSystem());
    newReport.setNumberOfStudentLibrarians(req.getNumberOfStudentLibrarians());
    newReport.setReasonNoStudentLibrarians(req.getReasonNoStudentLibrarians());
    newReport.setHasSufficientTraining(req.getHasSufficientTraining());
    newReport.setTeacherSupport(req.getTeacherSupport());
    newReport.setParentSupport(req.getParentSupport());
    newReport.setVisitReason(req.getVisitReason());
    newReport.setActionPlan(req.getActionPlan());
    newReport.setSuccessStories(req.getSuccessStories());
    newReport.setGradesAttended(
        (Object[]) req.getGradesAttended().stream().map(Grade::name).toArray(String[]::new));

    newReport.setCheckinTimetable(req.getCheckInTimetable().toString());
    newReport.setCheckoutTimetable(req.getCheckOutTimetable().toString());
    newReport.setNumberOfStudentLibrariansTrainers(req.getNumberOfStudentLibrariansTrainers());
    newReport.store();
  }

  @Override
  public ReportGeneric getMostRecentReport(JWTData userData, int schoolId) {
    SchoolsRecord school = this.util.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    ReportGeneric report = null;
    LibraryStatus libraryStatus = school.getLibraryStatus();

    if (libraryStatus == LibraryStatus.EXISTS) {
      report =
          ReportWithLibrary.instantiateFromRecord(
              db.selectFrom(SCHOOL_REPORTS_WITH_LIBRARIES)
                  .where(SCHOOL_REPORTS_WITH_LIBRARIES.DELETED_AT.isNull())
                  .and(SCHOOL_REPORTS_WITH_LIBRARIES.SCHOOL_ID.eq(schoolId))
                  .orderBy(SCHOOL_REPORTS_WITH_LIBRARIES.ID.desc())
                  .limit(1)
                  .fetchOne(),
              this.util.getUserName(userData.getUserId()),
              this.util.getSchoolName(schoolId));
    } else if (libraryStatus == LibraryStatus.DOES_NOT_EXIST) {
      report =
          ReportWithoutLibrary.instantiateFromRecord(
              db.selectFrom(SCHOOL_REPORTS_WITHOUT_LIBRARIES)
                  .where(SCHOOL_REPORTS_WITHOUT_LIBRARIES.DELETED_AT.isNull())
                  .and(SCHOOL_REPORTS_WITHOUT_LIBRARIES.SCHOOL_ID.eq(schoolId))
                  .orderBy(SCHOOL_REPORTS_WITHOUT_LIBRARIES.ID.desc())
                  .limit(1)
                  .fetchOne(),
              this.util.getUserName(userData.getUserId()),
              this.util.getSchoolName(schoolId));
    }

    if (report == null) {
      logger.error(String.format("Report was not found for school with ID: %d", schoolId));
      throw new NoReportFoundException(schoolId);
    }

    return report;
  }

  @Override
  public ReportWithoutLibrary createReportWithoutLibrary(
      JWTData userData, int schoolId, UpsertReportWithoutLibrary req) {
    SchoolsRecord school = this.util.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    if (req.getMostRecentShipmentYear() != null) {
      if (isShipmentYearInvalid(req.getMostRecentShipmentYear())) {
        throw new InvalidShipmentYearException(req.getMostRecentShipmentYear());
      }
    }
    school.setLibraryStatus(LibraryStatus.DOES_NOT_EXIST);
    school.store();

    SchoolReportsWithoutLibrariesRecord newReport = db.newRecord(SCHOOL_REPORTS_WITHOUT_LIBRARIES);
    storeReportWithoutLibrary(userData, schoolId, req, newReport, req.getGradesAttended());
    newReport.refresh();

    return new ReportWithoutLibrary(
        newReport.getId(),
        newReport.getCreatedAt(),
        newReport.getUpdatedAt(),
        newReport.getSchoolId(),
        newReport.getUserId(),
        newReport.getNumberOfChildren(),
        newReport.getNumberOfBooks(),
        newReport.getMostRecentShipmentYear(),
        newReport.getWantsLibrary(),
        newReport.getHasSpace(),
        req.getCurrentStatus(),
        newReport.getReasonWhyNot(),
        newReport.getReadyTimeline(),
        newReport.getVisitReason(),
        newReport.getActionPlan(),
        newReport.getSuccessStories(),
        req.getGradesAttended(),
        this.util.getUserName(userData.getUserId()),
        this.util.getSchoolName(schoolId),
        newReport.getReasonNoLibrarySpace()
    );
  }

  @Override
  public void updateReportWithoutLibrary(
      JWTData userData, int schoolId, int reportId, UpsertReportWithoutLibrary req) {

    SchoolsRecord school = this.util.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    // Save a record to the school_reports_with_libraries table
    SchoolReportsWithoutLibrariesRecord newReport =
        db.selectFrom(SCHOOL_REPORTS_WITHOUT_LIBRARIES)
            .where(SCHOOL_REPORTS_WITHOUT_LIBRARIES.ID.eq(reportId))
            .fetchOne();

    if (newReport == null) {
      throw new NoReportFoundException(schoolId);
    }

    if (!userData.isAdmin() && !newReport.getUserId().equals(userData.getUserId())) {
      throw new AdminOnlyRouteException();
    }

    if (req.getMostRecentShipmentYear() != null) {
      if (isShipmentYearInvalid(req.getMostRecentShipmentYear())) {
        throw new InvalidShipmentYearException(req.getMostRecentShipmentYear());
      }
    }

    storeReportWithoutLibrary(userData, schoolId, req, newReport, req.getGradesAttended());
  }

  private void storeReportWithoutLibrary(
      JWTData userData,
      int schoolId,
      UpsertReportWithoutLibrary req,
      SchoolReportsWithoutLibrariesRecord newReport,
      List<Grade> gradesAttended) {
    newReport.setSchoolId(schoolId);
    newReport.setUserId(userData.getUserId());
    newReport.setNumberOfChildren(req.getNumberOfChildren());
    newReport.setNumberOfBooks(req.getNumberOfBooks());
    newReport.setMostRecentShipmentYear(req.getMostRecentShipmentYear());
    newReport.setHasSpace(req.getHasSpace());
    newReport.setCurrentStatus((Object[]) req.getCurrentStatus().toArray(new String[0]));
    newReport.setReasonWhyNot(req.getReason());
    newReport.setWantsLibrary(req.getWantsLibrary());
    newReport.setReadyTimeline(req.getReadyTimeline());
    newReport.setVisitReason(req.getVisitReason());
    newReport.setActionPlan(req.getActionPlan());
    newReport.setSuccessStories(req.getSuccessStories());
    newReport.setGradesAttended(
        (Object[]) gradesAttended.stream().map(Grade::name).toArray(String[]::new));
    newReport.setReasonNoLibrarySpace(req.getReasonNoLibrarySpace());
    newReport.store();
  }

  @Override
  public ReportGenericListResponse getPaginatedReports(JWTData userData, int schoolId, int page) {
    if (page < 1) {
      throw new MalformedParameterException("p");
    }

    SchoolsRecord school = this.util.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    List<ReportWithLibrary> withLibraryReports =
        db.selectFrom(SCHOOL_REPORTS_WITH_LIBRARIES)
            .where(SCHOOL_REPORTS_WITH_LIBRARIES.DELETED_AT.isNull())
            .and(SCHOOL_REPORTS_WITH_LIBRARIES.SCHOOL_ID.eq(schoolId)).fetch().stream()
            .map(
                record ->
                    ReportWithLibrary.instantiateFromRecord(
                        record, this.util.getUserName(userData.getUserId()), this.util.getSchoolName(schoolId)))
            .collect(Collectors.toList());

    List<ReportWithoutLibrary> noLibraryReports =
        db.selectFrom(SCHOOL_REPORTS_WITHOUT_LIBRARIES)
            .where(SCHOOL_REPORTS_WITHOUT_LIBRARIES.DELETED_AT.isNull())
            .and(SCHOOL_REPORTS_WITHOUT_LIBRARIES.SCHOOL_ID.eq(schoolId)).fetch().stream()
            .map(
                record ->
                    ReportWithoutLibrary.instantiateFromRecord(
                        record, this.util.getUserName(userData.getUserId()), this.util.getSchoolName(schoolId)))
            .collect(Collectors.toList());

    int countWithLibrary =
        db.fetchCount(
            db.selectFrom(SCHOOL_REPORTS_WITH_LIBRARIES)
                .where(SCHOOL_REPORTS_WITH_LIBRARIES.DELETED_AT.isNull())
                .and(SCHOOL_REPORTS_WITH_LIBRARIES.SCHOOL_ID.eq(schoolId)));

    int countWithoutLibrary =
        db.fetchCount(
            db.selectFrom(SCHOOL_REPORTS_WITHOUT_LIBRARIES)
                .where(SCHOOL_REPORTS_WITHOUT_LIBRARIES.DELETED_AT.isNull())
                .and(SCHOOL_REPORTS_WITHOUT_LIBRARIES.SCHOOL_ID.eq(schoolId)));

    List<ReportGeneric> reports = new ArrayList<>();
    reports.addAll(withLibraryReports);
    reports.addAll(noLibraryReports);
    reports.sort(Comparator.comparing(ReportGeneric::getCreatedAt));

    int maxCountPerPage = 10;
    int from = (page - 1) * maxCountPerPage;
    int to = Math.min(page * maxCountPerPage, reports.size());
    List<ReportGeneric> paginatedReports =
        (from >= reports.size()) ? new ArrayList<>() : reports.subList(from, to);

    return new ReportGenericListResponse(
        paginatedReports, (countWithLibrary + countWithoutLibrary));
  }

  @Override
  public String getReportAsCsv(JWTData userData, int reportId, boolean hasLibrary) {
    ReportGeneric report;
    if (hasLibrary) {
      report =
          db.selectFrom(SCHOOL_REPORTS_WITH_LIBRARIES)
              .where(SCHOOL_REPORTS_WITH_LIBRARIES.ID.eq(reportId))
              .fetchOneInto(ReportWithLibrary.class);
    } else {
      report =
          db.selectFrom(SCHOOL_REPORTS_WITHOUT_LIBRARIES)
              .where(SCHOOL_REPORTS_WITHOUT_LIBRARIES.ID.eq(reportId))
              .fetchOneInto(ReportWithoutLibrary.class);
    }
    if (report == null) {
      throw new NoReportByIdFoundException(reportId);
    }
    StringBuilder builder = new StringBuilder();
    try {
      builder.append(CsvSerializer.getObjectHeader(report));
      builder.append(CsvSerializer.toCsv(report));
    } catch (IllegalStateException e) {
      throw new CsvSerializerException(reportId);
    }

    return builder.toString();
  }

  private boolean isShipmentYearInvalid(Integer year) {
    return year <= 999 || year >= 10000;
  }

}
