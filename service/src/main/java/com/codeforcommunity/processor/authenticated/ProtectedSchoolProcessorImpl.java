package com.codeforcommunity.processor.authenticated;

import static org.jooq.generated.Tables.BOOK_LOGS;
import static org.jooq.generated.Tables.SCHOOLS;
import static org.jooq.generated.Tables.SCHOOL_CONTACTS;
import static org.jooq.generated.Tables.SCHOOL_REPORTS_WITHOUT_LIBRARIES;
import static org.jooq.generated.Tables.SCHOOL_REPORTS_WITH_LIBRARIES;

import com.codeforcommunity.api.authenticated.IProtectedSchoolProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.CsvSerializer;
import com.codeforcommunity.dto.report.ReportGeneric;
import com.codeforcommunity.dto.report.ReportGenericListResponse;
import com.codeforcommunity.dto.report.ReportWithLibrary;
import com.codeforcommunity.dto.report.ReportWithoutLibrary;
import com.codeforcommunity.dto.report.UpsertReportWithLibrary;
import com.codeforcommunity.dto.report.UpsertReportWithoutLibrary;
import com.codeforcommunity.dto.school.BookLog;
import com.codeforcommunity.dto.school.BookLogListResponse;
import com.codeforcommunity.dto.school.School;
import com.codeforcommunity.dto.school.SchoolContact;
import com.codeforcommunity.dto.school.SchoolContactListResponse;
import com.codeforcommunity.dto.school.SchoolListResponse;
import com.codeforcommunity.dto.school.SchoolSummary;
import com.codeforcommunity.dto.school.UpsertBookLogRequest;
import com.codeforcommunity.dto.school.UpsertSchoolContactRequest;
import com.codeforcommunity.dto.school.UpsertSchoolRequest;
import com.codeforcommunity.enums.ContactType;
import com.codeforcommunity.enums.Country;
import com.codeforcommunity.enums.LibraryStatus;
import com.codeforcommunity.exceptions.AdminOnlyRouteException;
import com.codeforcommunity.exceptions.BookLogDoesNotExistException;
import com.codeforcommunity.exceptions.CsvSerializerException;
import com.codeforcommunity.exceptions.MalformedParameterException;
import com.codeforcommunity.exceptions.NoReportByIdFoundException;
import com.codeforcommunity.exceptions.NoReportFoundException;
import com.codeforcommunity.exceptions.SchoolAlreadyExistsException;
import com.codeforcommunity.exceptions.SchoolContactAlreadyExistsException;
import com.codeforcommunity.exceptions.SchoolContactDoesNotExistException;
import com.codeforcommunity.exceptions.SchoolDoesNotExistException;
import com.codeforcommunity.logger.SLogger;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.BookLogsRecord;
import org.jooq.generated.tables.records.SchoolContactsRecord;
import org.jooq.generated.tables.records.SchoolReportsWithLibrariesRecord;
import org.jooq.generated.tables.records.SchoolReportsWithoutLibrariesRecord;
import org.jooq.generated.tables.records.SchoolsRecord;

public class ProtectedSchoolProcessorImpl implements IProtectedSchoolProcessor {

  private final SLogger logger = new SLogger(ProtectedSchoolProcessorImpl.class);
  private final DSLContext db;

  public ProtectedSchoolProcessorImpl(DSLContext db) {
    this.db = db;
  }

  @Override
  public SchoolListResponse getAllSchools(JWTData userData) {
    List<SchoolSummary> schools =
        db.selectFrom(SCHOOLS)
            .where(SCHOOLS.HIDDEN.isFalse())
            .and(SCHOOLS.DELETED_AT.isNull())
            .fetchInto(SchoolSummary.class);

    return new SchoolListResponse(schools);
  }

  @Override
  public School getSchool(JWTData userData, int schoolId) {
    School school =
        db.selectFrom(SCHOOLS)
            .where(SCHOOLS.DELETED_AT.isNull())
            .and(SCHOOLS.ID.eq(schoolId))
            .fetchOneInto(School.class);

    if (school == null) {
      // Check to make sure the school exists first
      throw new SchoolDoesNotExistException(schoolId);
    }

    return school;
  }

  @Override
  public School createSchool(JWTData userData, UpsertSchoolRequest upsertSchoolRequest) {
    if (!userData.isAdmin()) {
      throw new AdminOnlyRouteException();
    }

    String name = upsertSchoolRequest.getName();
    String address = upsertSchoolRequest.getAddress();
    String phone = upsertSchoolRequest.getPhone();
    String email = upsertSchoolRequest.getEmail();
    String notes = upsertSchoolRequest.getNotes();
    String area = upsertSchoolRequest.getArea();
    Country country = upsertSchoolRequest.getCountry();
    Boolean hidden = upsertSchoolRequest.getHidden();
    LibraryStatus libraryStatus = upsertSchoolRequest.getLibraryStatus();

    SchoolsRecord school =
        db.selectFrom(SCHOOLS)
            .where(SCHOOLS.NAME.eq(name))
            .and(SCHOOLS.ADDRESS.eq(address))
            .and(SCHOOLS.COUNTRY.eq(country))
            .fetchOne();

    if (school == null) {
      // If the school doesn't already exist, create it
      SchoolsRecord newSchool = db.newRecord(SCHOOLS);
      newSchool.setName(name);
      newSchool.setAddress(address);
      newSchool.setPhone(phone);
      newSchool.setEmail(email);
      newSchool.setNotes(notes);
      newSchool.setArea(area);
      newSchool.setCountry(country);
      newSchool.setHidden(hidden);
      newSchool.setLibraryStatus(libraryStatus);
      newSchool.store();

      return new School(
          newSchool.getId(),
          newSchool.getName(),
          newSchool.getAddress(),
          newSchool.getEmail(),
          newSchool.getPhone(),
          newSchool.getNotes(),
          newSchool.getArea(),
          newSchool.getCountry(),
          newSchool.getHidden(),
          newSchool.getLibraryStatus());
    }

    if (school.getDeletedAt() != null || school.getHidden()) {
      // If the school was previously deleted, un-delete it
      school.setDeletedAt(null);
      school.setHidden(hidden);
      school.setPhone(phone);
      school.setEmail(email);
      school.setNotes(notes);
      school.setArea(area);
      school.setLibraryStatus(libraryStatus);
      school.store();

      Integer schoolId = school.getId();
      List<SchoolContact> contacts = this.queryForSchoolContacts(schoolId);

      return new School(
          school.getId(),
          school.getName(),
          school.getAddress(),
          school.getEmail(),
          school.getPhone(),
          school.getNotes(),
          school.getArea(),
          school.getCountry(),
          school.getHidden(),
          school.getLibraryStatus(),
          contacts);
    }

    throw new SchoolAlreadyExistsException(name, country);
  }

  @Override
  public SchoolContactListResponse getAllSchoolContacts(JWTData userData, int schoolId) {
    SchoolsRecord school = this.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    List<SchoolContact> schoolContacts =
        db.selectFrom(SCHOOL_CONTACTS)
            .where(SCHOOL_CONTACTS.SCHOOL_ID.eq(schoolId))
            .and(SCHOOL_CONTACTS.DELETED_AT.isNull())
            .fetchInto(SchoolContact.class);

    return new SchoolContactListResponse(schoolContacts);
  }

  @Override
  public SchoolContact getSchoolContact(JWTData userData, int schoolId, int contactId) {
    SchoolsRecord school = this.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    SchoolContact schoolContact =
        db.selectFrom(SCHOOL_CONTACTS)
            .where(SCHOOL_CONTACTS.ID.eq(contactId))
            .and(SCHOOL_CONTACTS.SCHOOL_ID.eq(schoolId))
            .and(SCHOOL_CONTACTS.DELETED_AT.isNull())
            .fetchOneInto(SchoolContact.class);

    if (schoolContact == null) {
      throw new SchoolContactDoesNotExistException(schoolId, contactId);
    }

    return schoolContact;
  }

  @Override
  public SchoolContact createSchoolContact(
      JWTData userData, int schoolId, UpsertSchoolContactRequest upsertSchoolContactRequest) {
    SchoolsRecord school = this.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    String firstName = upsertSchoolContactRequest.getFirstName();
    String lastName = upsertSchoolContactRequest.getLastName();
    String email = upsertSchoolContactRequest.getEmail();
    String address = upsertSchoolContactRequest.getAddress();
    String phone = upsertSchoolContactRequest.getPhone();
    ContactType type = upsertSchoolContactRequest.getType();

    SchoolContactsRecord contact =
        db.selectFrom(SCHOOL_CONTACTS)
            .where(SCHOOL_CONTACTS.FIRST_NAME.eq(firstName))
            .and(SCHOOL_CONTACTS.LAST_NAME.eq(lastName))
            .and(SCHOOL_CONTACTS.EMAIL.eq(email))
            .and(SCHOOL_CONTACTS.ADDRESS.eq(address))
            .and(SCHOOL_CONTACTS.PHONE.eq(phone))
            .and(SCHOOL_CONTACTS.SCHOOL_ID.eq(schoolId))
            .and(SCHOOL_CONTACTS.TYPE.eq(type))
            .fetchOne();

    if (contact == null) {
      // If the contact doesn't already exist, create it
      SchoolContactsRecord newContact = db.newRecord(SCHOOL_CONTACTS);
      newContact.setSchoolId(schoolId);
      newContact.setFirstName(firstName);
      newContact.setLastName(lastName);
      newContact.setEmail(email);
      newContact.setAddress(address);
      newContact.setPhone(phone);
      newContact.setType(type);
      newContact.store();

      return new SchoolContact(
          newContact.getId(),
          newContact.getSchoolId(),
          newContact.getFirstName(),
          newContact.getLastName(),
          newContact.getEmail(),
          newContact.getAddress(),
          newContact.getPhone(),
          newContact.getType());
    }

    if (contact.getDeletedAt() != null) {
      // If the contact was previously deleted, un-delete it
      contact.setDeletedAt(null);
      contact.store();

      return new SchoolContact(
          contact.getId(),
          contact.getSchoolId(),
          contact.getFirstName(),
          contact.getLastName(),
          contact.getEmail(),
          contact.getAddress(),
          contact.getPhone(),
          contact.getType());
    }

    throw new SchoolContactAlreadyExistsException(school.getName(), firstName, lastName);
  }

  @Override
  public void updateSchoolContact(
      JWTData userData,
      int schoolId,
      int contactId,
      UpsertSchoolContactRequest upsertSchoolContactRequest) {
    SchoolContactsRecord contactRecord =
        db.selectFrom(SCHOOL_CONTACTS)
            .where(SCHOOL_CONTACTS.DELETED_AT.isNull())
            .and(SCHOOL_CONTACTS.SCHOOL_ID.eq(schoolId))
            .and(SCHOOL_CONTACTS.ID.eq(contactId))
            .fetchOne();

    if (contactRecord == null) {
      throw new SchoolContactDoesNotExistException(schoolId, contactId);
    }

    String firstName = upsertSchoolContactRequest.getFirstName();
    String lastName = upsertSchoolContactRequest.getLastName();
    String email = upsertSchoolContactRequest.getEmail();
    String address = upsertSchoolContactRequest.getAddress();
    String phone = upsertSchoolContactRequest.getPhone();
    ContactType type = upsertSchoolContactRequest.getType();

    contactRecord.setFirstName(firstName);
    contactRecord.setLastName(lastName);
    contactRecord.setEmail(email);
    contactRecord.setAddress(address);
    contactRecord.setPhone(phone);
    contactRecord.setType(type);
    contactRecord.store();
  }

  @Override
  public void deleteSchoolContact(JWTData userData, int schoolId, int contactId) {
    SchoolContactsRecord schoolContactsRecord =
        db.selectFrom(SCHOOL_CONTACTS)
            .where(SCHOOL_CONTACTS.DELETED_AT.isNull())
            .and(SCHOOL_CONTACTS.SCHOOL_ID.eq(schoolId))
            .and(SCHOOL_CONTACTS.ID.eq(contactId))
            .fetchOne();

    if (schoolContactsRecord == null) {
      throw new SchoolContactDoesNotExistException(schoolId, contactId);
    }

    schoolContactsRecord.setDeletedAt(Timestamp.from(Instant.now()));
    schoolContactsRecord.store();
  }

  @Override
  public void updateSchool(
      JWTData userData, int schoolId, UpsertSchoolRequest upsertSchoolRequest) {
    SchoolsRecord school = this.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    String name = upsertSchoolRequest.getName();
    String address = upsertSchoolRequest.getAddress();
    String phone = upsertSchoolRequest.getPhone();
    String email = upsertSchoolRequest.getEmail();
    String notes = upsertSchoolRequest.getNotes();
    String area = upsertSchoolRequest.getArea();
    Country country = upsertSchoolRequest.getCountry();
    Boolean hidden = upsertSchoolRequest.getHidden();
    LibraryStatus libraryStatus = upsertSchoolRequest.getLibraryStatus();

    school.setName(name);
    school.setAddress(address);
    school.setPhone(phone);
    school.setEmail(email);
    school.setNotes(notes);
    school.setArea(area);
    school.setCountry(country);
    school.setHidden(hidden);
    school.setLibraryStatus(libraryStatus);
    school.store();
  }

  @Override
  public void deleteSchool(JWTData userData, int schoolId) {
    if (!userData.isAdmin()) {
      throw new AdminOnlyRouteException();
    }

    SchoolsRecord school = this.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    school.setDeletedAt(new Timestamp(System.currentTimeMillis()));
    school.store();
  }

  @Override
  public void hideSchool(JWTData userData, int schoolId) {
    SchoolsRecord school = this.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }
    school.setHidden(true);
    school.store();
  }

  @Override
  public void unHideSchool(JWTData userData, int schoolId) {
    SchoolsRecord school = this.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    school.setHidden(false);
    school.store();
  }

  @Override
  public ReportWithLibrary createReportWithLibrary(
      JWTData userData, int schoolId, UpsertReportWithLibrary req) {
    SchoolsRecord school = this.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    // Update this school to have a new library status
    school.setLibraryStatus(LibraryStatus.EXISTS);
    school.store();

    // Save a record to the school_reports_with_libraries table
    SchoolReportsWithLibrariesRecord newReport = db.newRecord(SCHOOL_REPORTS_WITH_LIBRARIES);
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

    // save record and refresh to fetch report ID and timestamps
    newReport.store();
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
        newReport.getSuccessStories());
  }

  @Override
  public void updateReportWithLibrary(
      JWTData userData, int schoolId, int reportId, UpsertReportWithLibrary req) {
    SchoolsRecord school = this.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    // Save a record to the school_reports_with_libraries table
    SchoolReportsWithLibrariesRecord newReport =
        db.selectFrom(SCHOOL_REPORTS_WITH_LIBRARIES)
            .where(SCHOOL_REPORTS_WITH_LIBRARIES.ID.eq(reportId))
            .fetchOne();

    if (!userData.isAdmin() && !newReport.getUserId().equals(userData.getUserId())) {
      throw new AdminOnlyRouteException();
    }

    if (newReport == null) {
      throw new NoReportFoundException(schoolId);
    }

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

    newReport.store();
  }

  @Override
  public ReportGeneric getMostRecentReport(JWTData userData, int schoolId) {
    SchoolsRecord school = this.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    ReportGeneric report = null;
    LibraryStatus libraryStatus = school.getLibraryStatus();

    if (libraryStatus == LibraryStatus.EXISTS) {
      report =
          db.selectFrom(SCHOOL_REPORTS_WITH_LIBRARIES)
              .where(SCHOOL_REPORTS_WITH_LIBRARIES.DELETED_AT.isNull())
              .and(SCHOOL_REPORTS_WITH_LIBRARIES.SCHOOL_ID.eq(schoolId))
              .orderBy(SCHOOL_REPORTS_WITH_LIBRARIES.ID.desc())
              .limit(1)
              .fetchOneInto(ReportWithLibrary.class);
    } else if (libraryStatus == LibraryStatus.DOES_NOT_EXIST) {
      report =
          db.selectFrom(SCHOOL_REPORTS_WITHOUT_LIBRARIES)
              .where(SCHOOL_REPORTS_WITHOUT_LIBRARIES.DELETED_AT.isNull())
              .and(SCHOOL_REPORTS_WITHOUT_LIBRARIES.SCHOOL_ID.eq(schoolId))
              .orderBy(SCHOOL_REPORTS_WITHOUT_LIBRARIES.ID.desc())
              .limit(1)
              .fetchOneInto(ReportWithoutLibrary.class);
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
    SchoolsRecord school = this.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    school.setLibraryStatus(LibraryStatus.DOES_NOT_EXIST);
    school.store();

    SchoolReportsWithoutLibrariesRecord newReport = db.newRecord(SCHOOL_REPORTS_WITHOUT_LIBRARIES);
    newReport.setSchoolId(schoolId);
    newReport.setUserId(userData.getUserId());
    newReport.setNumberOfChildren(req.getNumberOfChildren());
    newReport.setNumberOfBooks(req.getNumberOfBooks());
    newReport.setMostRecentShipmentYear(req.getMostRecentShipmentYear());
    newReport.setHasSpace(req.getHasSpace());
    newReport.setCurrentStatus(req.getCurrentStatus());
    newReport.setReasonWhyNot(req.getReasonWhyNot());
    newReport.setWantsLibrary(req.getWantsLibrary());
    newReport.setReadyTimeline(req.getReadyTimeline());
    newReport.setVisitReason(req.getVisitReason());
    newReport.setActionPlan(req.getActionPlan());
    newReport.setSuccessStories(req.getSuccessStories());

    // save record and refresh to fetch report ID and timestamps
    newReport.store();
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
        newReport.getCurrentStatus(),
        newReport.getReasonWhyNot(),
        newReport.getReadyTimeline(),
        newReport.getVisitReason(),
        newReport.getActionPlan(),
        newReport.getSuccessStories());
  }

  @Override
  public void updateReportWithoutLibrary(
      JWTData userData, int schoolId, int reportId, UpsertReportWithoutLibrary req) {

    SchoolsRecord school = this.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    // Save a record to the school_reports_with_libraries table
    SchoolReportsWithoutLibrariesRecord newReport =
        db.selectFrom(SCHOOL_REPORTS_WITHOUT_LIBRARIES)
            .where(SCHOOL_REPORTS_WITHOUT_LIBRARIES.ID.eq(reportId))
            .fetchOne();

    if (!userData.isAdmin() && !newReport.getUserId().equals(userData.getUserId())) {
      throw new AdminOnlyRouteException();
    }

    if (newReport == null) {
      throw new NoReportFoundException(schoolId);
    }

    newReport.setSchoolId(schoolId);
    newReport.setUserId(userData.getUserId());
    newReport.setNumberOfChildren(req.getNumberOfChildren());
    newReport.setNumberOfBooks(req.getNumberOfBooks());
    newReport.setMostRecentShipmentYear(req.getMostRecentShipmentYear());
    newReport.setHasSpace(req.getHasSpace());
    newReport.setCurrentStatus(req.getCurrentStatus());
    newReport.setReasonWhyNot(req.getReasonWhyNot());
    newReport.setWantsLibrary(req.getWantsLibrary());
    newReport.setReadyTimeline(req.getReadyTimeline());
    newReport.setVisitReason(req.getVisitReason());
    newReport.setActionPlan(req.getActionPlan());
    newReport.setSuccessStories(req.getSuccessStories());

    newReport.store();
  }

  @Override
  public ReportGenericListResponse getPaginatedReports(JWTData userData, int schoolId, int page) {
    if (page < 1) {
      throw new MalformedParameterException("p");
    }

    SchoolsRecord school = this.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    List<ReportWithLibrary> withLibraryReports =
        db.selectFrom(SCHOOL_REPORTS_WITH_LIBRARIES)
            .where(SCHOOL_REPORTS_WITH_LIBRARIES.DELETED_AT.isNull())
            .and(SCHOOL_REPORTS_WITH_LIBRARIES.SCHOOL_ID.eq(schoolId))
            .fetchInto(ReportWithLibrary.class);

    List<ReportWithoutLibrary> noLibraryReports =
        db.selectFrom(SCHOOL_REPORTS_WITHOUT_LIBRARIES)
            .where(SCHOOL_REPORTS_WITHOUT_LIBRARIES.DELETED_AT.isNull())
            .and(SCHOOL_REPORTS_WITHOUT_LIBRARIES.SCHOOL_ID.eq(schoolId))
            .fetchInto(ReportWithoutLibrary.class);

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

    List<ReportGeneric> reports = new ArrayList<ReportGeneric>();
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
  public BookLog createBookLog(JWTData userData, int schoolId, UpsertBookLogRequest request) {
    if (!userData.isAdmin()) {
      throw new AdminOnlyRouteException();
    }

    SchoolsRecord school = this.queryForSchool(schoolId);
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
    SchoolsRecord school = this.queryForSchool(schoolId);
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
    SchoolsRecord school = this.queryForSchool(schoolId);
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
  public SchoolListResponse getSchoolsFromUserIdReports(JWTData userData) {
    Set<Integer> schoolIds = new HashSet<>();

    List<ReportWithLibrary> withLibraryReports =
        db.selectFrom(SCHOOL_REPORTS_WITH_LIBRARIES)
            .where(SCHOOL_REPORTS_WITH_LIBRARIES.DELETED_AT.isNull())
            .and(SCHOOL_REPORTS_WITH_LIBRARIES.USER_ID.eq(userData.getUserId()))
            .fetchInto(ReportWithLibrary.class);

    List<ReportWithoutLibrary> noLibraryReports =
        db.selectFrom(SCHOOL_REPORTS_WITHOUT_LIBRARIES)
            .where(SCHOOL_REPORTS_WITHOUT_LIBRARIES.DELETED_AT.isNull())
            .and(SCHOOL_REPORTS_WITHOUT_LIBRARIES.SCHOOL_ID.eq(userData.getUserId()))
            .fetchInto(ReportWithoutLibrary.class);

    for (ReportWithLibrary report : withLibraryReports) {
      schoolIds.add(report.getSchoolId());
    }

    for (ReportWithoutLibrary report : noLibraryReports) {
      schoolIds.add(report.getSchoolId());
    }

    List<SchoolSummary> schools =
        db.selectFrom(SCHOOLS)
            .where(SCHOOLS.HIDDEN.isFalse())
            .and(SCHOOLS.DELETED_AT.isNull())
            .and(SCHOOLS.ID.in(schoolIds))
            .fetchInto(SchoolSummary.class);

    return new SchoolListResponse(schools);
  }

  @Override
  public BookLogListResponse getBookLog(JWTData userData, int schoolId) {
    SchoolsRecord school = this.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    List<BookLog> logs = db.selectFrom(BOOK_LOGS).fetchInto(BookLog.class);

    return (logs != null)
        ? new BookLogListResponse(logs)
        : new BookLogListResponse(new ArrayList<>());
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

  private SchoolsRecord queryForSchool(int schoolId) {
    return db.selectFrom(SCHOOLS)
        .where(SCHOOLS.ID.eq(schoolId))
        .and(SCHOOLS.DELETED_AT.isNull())
        .fetchOne();
  }

  private List<SchoolContact> queryForSchoolContacts(int schoolId) {
    return db.selectFrom(SCHOOL_CONTACTS)
        .where(SCHOOL_CONTACTS.DELETED_AT.isNull())
        .and(SCHOOL_CONTACTS.SCHOOL_ID.eq(schoolId))
        .fetchInto(SchoolContact.class);
  }
}
