package com.codeforcommunity.processor.authenticated;

import static org.jooq.generated.Tables.SCHOOLS;
import static org.jooq.generated.Tables.SCHOOL_CONTACTS;
import static org.jooq.generated.Tables.SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES;
import static org.jooq.generated.Tables.SCHOOL_REPORTS_WITHOUT_LIBRARIES;
import static org.jooq.generated.Tables.SCHOOL_REPORTS_WITH_LIBRARIES;

import com.codeforcommunity.api.authenticated.IProtectedSchoolProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.report.ReportGeneric;
import com.codeforcommunity.dto.report.ReportGenericListResponse;
import com.codeforcommunity.dto.report.ReportWithLibrary;
import com.codeforcommunity.dto.report.ReportWithLibraryInProgress;
import com.codeforcommunity.dto.report.ReportWithoutLibrary;
import com.codeforcommunity.dto.report.UpsertReportInProgressLibrary;
import com.codeforcommunity.dto.report.UpsertReportWithLibrary;
import com.codeforcommunity.dto.report.UpsertReportWithoutLibrary;
import com.codeforcommunity.dto.school.School;
import com.codeforcommunity.dto.school.SchoolContact;
import com.codeforcommunity.dto.school.SchoolContactListResponse;
import com.codeforcommunity.dto.school.SchoolListResponse;
import com.codeforcommunity.dto.school.SchoolSummary;
import com.codeforcommunity.dto.school.UpsertSchoolContactRequest;
import com.codeforcommunity.dto.school.UpsertSchoolRequest;
import com.codeforcommunity.enums.ContactType;
import com.codeforcommunity.enums.Country;
import com.codeforcommunity.enums.LibraryStatus;
import com.codeforcommunity.exceptions.AdminOnlyRouteException;
import com.codeforcommunity.exceptions.MalformedParameterException;
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
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.SchoolContactsRecord;
import org.jooq.generated.tables.records.SchoolReportsInProgressLibrariesRecord;
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
        db.select(SCHOOLS.ID, SCHOOLS.NAME, SCHOOLS.COUNTRY)
            .from(SCHOOLS)
            .where(SCHOOLS.HIDDEN.isFalse())
            .and(SCHOOLS.DELETED_AT.isNull())
            .fetchInto(SchoolSummary.class);

    return new SchoolListResponse(schools);
  }

  @Override
  public School getSchool(JWTData userData, int schoolId) {
    School school =
        db.select(
                SCHOOLS.ID,
                SCHOOLS.NAME,
                SCHOOLS.ADDRESS,
                SCHOOLS.EMAIL,
                SCHOOLS.PHONE,
                SCHOOLS.NOTES,
                SCHOOLS.AREA,
                SCHOOLS.COUNTRY,
                SCHOOLS.HIDDEN,
                SCHOOLS.LIBRARY_STATUS)
            .from(SCHOOLS)
            .where(SCHOOLS.DELETED_AT.isNull())
            .and(SCHOOLS.ID.eq(schoolId))
            .fetchOneInto(School.class);

    if (school == null) {
      // Check to make sure the school exists first
      throw new SchoolDoesNotExistException(schoolId);
    }

    List<SchoolContact> contacts = this.queryForSchoolContacts(schoolId);
    school.setContacts(contacts);
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
        db.select(
                SCHOOL_CONTACTS.ID,
                SCHOOL_CONTACTS.SCHOOL_ID,
                SCHOOL_CONTACTS.FIRST_NAME,
                SCHOOL_CONTACTS.LAST_NAME,
                SCHOOL_CONTACTS.EMAIL,
                SCHOOL_CONTACTS.ADDRESS,
                SCHOOL_CONTACTS.PHONE,
                SCHOOL_CONTACTS.TYPE)
            .from(SCHOOL_CONTACTS)
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
        db.select(
                SCHOOL_CONTACTS.ID,
                SCHOOL_CONTACTS.SCHOOL_ID,
                SCHOOL_CONTACTS.FIRST_NAME,
                SCHOOL_CONTACTS.LAST_NAME,
                SCHOOL_CONTACTS.EMAIL,
                SCHOOL_CONTACTS.ADDRESS,
                SCHOOL_CONTACTS.PHONE,
                SCHOOL_CONTACTS.TYPE)
            .from(SCHOOL_CONTACTS)
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
        newReport.getParentSupport());
  }

  @Override
  public ReportGeneric getMostRecentReport(JWTData userData, int schoolId) {
    SchoolsRecord school = this.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }

    LibraryStatus libraryStatus = school.getLibraryStatus();

    if (libraryStatus == LibraryStatus.EXISTS) {
      ReportWithLibrary temp =
          db.select(
                  SCHOOL_REPORTS_WITH_LIBRARIES.ID,
                  SCHOOL_REPORTS_WITH_LIBRARIES.CREATED_AT,
                  SCHOOL_REPORTS_WITH_LIBRARIES.UPDATED_AT,
                  SCHOOL_REPORTS_WITH_LIBRARIES.SCHOOL_ID,
                  SCHOOL_REPORTS_WITH_LIBRARIES.USER_ID,
                  SCHOOL_REPORTS_WITH_LIBRARIES.NUMBER_OF_CHILDREN,
                  SCHOOL_REPORTS_WITH_LIBRARIES.NUMBER_OF_BOOKS,
                  SCHOOL_REPORTS_WITH_LIBRARIES.MOST_RECENT_SHIPMENT_YEAR,
                  SCHOOL_REPORTS_WITH_LIBRARIES.IS_SHARED_SPACE,
                  SCHOOL_REPORTS_WITH_LIBRARIES.HAS_INVITING_SPACE,
                  SCHOOL_REPORTS_WITH_LIBRARIES.ASSIGNED_PERSON_ROLE,
                  SCHOOL_REPORTS_WITH_LIBRARIES.ASSIGNED_PERSON_TITLE,
                  SCHOOL_REPORTS_WITH_LIBRARIES.APPRENTICESHIP_PROGRAM,
                  SCHOOL_REPORTS_WITH_LIBRARIES.TRAINS_AND_MENTORS_APPRENTICES,
                  SCHOOL_REPORTS_WITH_LIBRARIES.HAS_CHECK_IN_TIMETABLES,
                  SCHOOL_REPORTS_WITH_LIBRARIES.HAS_BOOK_CHECKOUT_SYSTEM,
                  SCHOOL_REPORTS_WITH_LIBRARIES.NUMBER_OF_STUDENT_LIBRARIANS,
                  SCHOOL_REPORTS_WITH_LIBRARIES.REASON_NO_STUDENT_LIBRARIANS,
                  SCHOOL_REPORTS_WITH_LIBRARIES.HAS_SUFFICIENT_TRAINING,
                  SCHOOL_REPORTS_WITH_LIBRARIES.TEACHER_SUPPORT,
                  SCHOOL_REPORTS_WITH_LIBRARIES.PARENT_SUPPORT)
              .from(SCHOOL_REPORTS_WITH_LIBRARIES)
              .where(SCHOOL_REPORTS_WITH_LIBRARIES.DELETED_AT.isNull())
              .and(SCHOOL_REPORTS_WITH_LIBRARIES.SCHOOL_ID.eq(schoolId))
              .fetchOneInto(ReportWithLibrary.class);
      if (temp == null) {
        logger.error("Report was not found in table");
        throw new NoReportFoundException(schoolId);
      }
    }
    if (libraryStatus == LibraryStatus.DOES_NOT_EXIST) {
      ReportWithoutLibrary temp =
          db.select(
                  SCHOOL_REPORTS_WITHOUT_LIBRARIES.ID,
                  SCHOOL_REPORTS_WITHOUT_LIBRARIES.CREATED_AT,
                  SCHOOL_REPORTS_WITHOUT_LIBRARIES.UPDATED_AT,
                  SCHOOL_REPORTS_WITHOUT_LIBRARIES.SCHOOL_ID,
                  SCHOOL_REPORTS_WITHOUT_LIBRARIES.USER_ID,
                  SCHOOL_REPORTS_WITHOUT_LIBRARIES.NUMBER_OF_CHILDREN,
                  SCHOOL_REPORTS_WITHOUT_LIBRARIES.NUMBER_OF_BOOKS,
                  SCHOOL_REPORTS_WITHOUT_LIBRARIES.MOST_RECENT_SHIPMENT_YEAR,
                  SCHOOL_REPORTS_WITHOUT_LIBRARIES.WANTS_LIBRARY,
                  SCHOOL_REPORTS_WITHOUT_LIBRARIES.HAS_SPACE,
                  SCHOOL_REPORTS_WITHOUT_LIBRARIES.CURRENT_STATUS,
                  SCHOOL_REPORTS_WITHOUT_LIBRARIES.REASON_WHY_NOT,
                  SCHOOL_REPORTS_WITHOUT_LIBRARIES.READY_TIMELINE)
              .from(SCHOOL_REPORTS_WITHOUT_LIBRARIES)
              .where(SCHOOL_REPORTS_WITHOUT_LIBRARIES.DELETED_AT.isNull())
              .and(SCHOOL_REPORTS_WITHOUT_LIBRARIES.SCHOOL_ID.eq(schoolId))
              .fetchOneInto(ReportWithoutLibrary.class);
      if (temp == null) {
        logger.error("Report was not found in table");
        throw new NoReportFoundException(schoolId);
      }
    }
    if (libraryStatus == LibraryStatus.IN_PROGRESS) {
      ReportWithLibraryInProgress temp =
          db.select(
                  SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.ID,
                  SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.CREATED_AT,
                  SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.UPDATED_AT,
                  SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.SCHOOL_ID,
                  SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.USER_ID,
                  SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.NUMBER_OF_CHILDREN,
                  SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.NUMBER_OF_BOOKS,
                  SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.MOST_RECENT_SHIPMENT_YEAR,
                  SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.IS_SHARED_SPACE,
                  SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.HAS_INVITING_SPACE,
                  SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.ASSIGNED_PERSON_ROLE,
                  SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.ASSIGNED_PERSON_TITLE,
                  SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.APPRENTICESHIP_PROGRAM,
                  SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.TRAINS_AND_MENTORS_APPRENTICES)
              .from(SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES)
              .where(SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.DELETED_AT.isNull())
              .and(SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.SCHOOL_ID.eq(schoolId))
              .fetchOneInto(ReportWithLibraryInProgress.class);
      if (temp == null) {
        logger.error("Report was not found in table");
        throw new NoReportFoundException(schoolId);
      }
    }
    throw new NoReportFoundException(schoolId);
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
        newReport.getReadyTimeline());
  }

  @Override
  public ReportWithLibraryInProgress createReportWithLibraryInProgress(
      JWTData userData, int schoolId, UpsertReportInProgressLibrary upsertRequest) {
    SchoolsRecord school = this.queryForSchool(schoolId);
    if (school == null) {
      throw new SchoolDoesNotExistException(schoolId);
    }
    school.setLibraryStatus(LibraryStatus.IN_PROGRESS);
    school.store();

    SchoolReportsInProgressLibrariesRecord newReport =
        db.newRecord(SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES);

    newReport.setSchoolId(schoolId);
    newReport.setUserId(userData.getUserId());
    newReport.setNumberOfChildren(upsertRequest.getNumberOfChildren());
    newReport.setNumberOfBooks(upsertRequest.getNumberOfBooks());
    newReport.setMostRecentShipmentYear(upsertRequest.getMostRecentShipmentYear());
    newReport.setApprenticeshipProgram(upsertRequest.getApprenticeshipProgram());
    newReport.setHasInvitingSpace(upsertRequest.getHasInvitingSpace());
    newReport.setIsSharedSpace(upsertRequest.getIsSharedSpace());
    newReport.setAssignedPersonRole(upsertRequest.getAssignedPersonRole());
    newReport.setAssignedPersonTitle(upsertRequest.getAssignedPersonTitle());
    newReport.setTrainsAndMentorsApprentices(upsertRequest.getTrainsAndMentorsApprentices());

    newReport.store();
    newReport.refresh();

    return new ReportWithLibraryInProgress(
        newReport.getId(),
        newReport.getCreatedAt(),
        newReport.getUpdatedAt(),
        newReport.getSchoolId(),
        newReport.getUserId(),
        newReport.getNumberOfChildren(),
        newReport.getNumberOfBooks(),
        newReport.getMostRecentShipmentYear(),
        newReport.getIsSharedSpace(),
        newReport.getHasInvitingSpace(),
        newReport.getAssignedPersonRole(),
        newReport.getAssignedPersonTitle(),
        newReport.getApprenticeshipProgram(),
        newReport.getTrainsAndMentorsApprentices());
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
        db.select(
                SCHOOL_REPORTS_WITH_LIBRARIES.ID,
                SCHOOL_REPORTS_WITH_LIBRARIES.CREATED_AT,
                SCHOOL_REPORTS_WITH_LIBRARIES.UPDATED_AT,
                SCHOOL_REPORTS_WITH_LIBRARIES.SCHOOL_ID,
                SCHOOL_REPORTS_WITH_LIBRARIES.USER_ID,
                SCHOOL_REPORTS_WITH_LIBRARIES.NUMBER_OF_CHILDREN,
                SCHOOL_REPORTS_WITH_LIBRARIES.NUMBER_OF_BOOKS,
                SCHOOL_REPORTS_WITH_LIBRARIES.MOST_RECENT_SHIPMENT_YEAR,
                SCHOOL_REPORTS_WITH_LIBRARIES.IS_SHARED_SPACE,
                SCHOOL_REPORTS_WITH_LIBRARIES.HAS_INVITING_SPACE,
                SCHOOL_REPORTS_WITH_LIBRARIES.ASSIGNED_PERSON_ROLE,
                SCHOOL_REPORTS_WITH_LIBRARIES.ASSIGNED_PERSON_TITLE,
                SCHOOL_REPORTS_WITH_LIBRARIES.APPRENTICESHIP_PROGRAM,
                SCHOOL_REPORTS_WITH_LIBRARIES.TRAINS_AND_MENTORS_APPRENTICES,
                SCHOOL_REPORTS_WITH_LIBRARIES.HAS_CHECK_IN_TIMETABLES,
                SCHOOL_REPORTS_WITH_LIBRARIES.HAS_BOOK_CHECKOUT_SYSTEM,
                SCHOOL_REPORTS_WITH_LIBRARIES.NUMBER_OF_STUDENT_LIBRARIANS,
                SCHOOL_REPORTS_WITH_LIBRARIES.REASON_NO_STUDENT_LIBRARIANS,
                SCHOOL_REPORTS_WITH_LIBRARIES.HAS_SUFFICIENT_TRAINING,
                SCHOOL_REPORTS_WITH_LIBRARIES.TEACHER_SUPPORT,
                SCHOOL_REPORTS_WITH_LIBRARIES.PARENT_SUPPORT)
            .from(SCHOOL_REPORTS_WITH_LIBRARIES)
            .where(SCHOOL_REPORTS_WITH_LIBRARIES.DELETED_AT.isNull())
            .and(SCHOOL_REPORTS_WITH_LIBRARIES.SCHOOL_ID.eq(schoolId))
            .fetchInto(ReportWithLibrary.class);

    List<ReportWithoutLibrary> noLibraryReports =
        db.select(
                SCHOOL_REPORTS_WITHOUT_LIBRARIES.ID,
                SCHOOL_REPORTS_WITHOUT_LIBRARIES.CREATED_AT,
                SCHOOL_REPORTS_WITHOUT_LIBRARIES.UPDATED_AT,
                SCHOOL_REPORTS_WITHOUT_LIBRARIES.SCHOOL_ID,
                SCHOOL_REPORTS_WITHOUT_LIBRARIES.USER_ID,
                SCHOOL_REPORTS_WITHOUT_LIBRARIES.NUMBER_OF_CHILDREN,
                SCHOOL_REPORTS_WITHOUT_LIBRARIES.NUMBER_OF_BOOKS,
                SCHOOL_REPORTS_WITHOUT_LIBRARIES.MOST_RECENT_SHIPMENT_YEAR,
                SCHOOL_REPORTS_WITHOUT_LIBRARIES.WANTS_LIBRARY,
                SCHOOL_REPORTS_WITHOUT_LIBRARIES.HAS_SPACE,
                SCHOOL_REPORTS_WITHOUT_LIBRARIES.CURRENT_STATUS,
                SCHOOL_REPORTS_WITHOUT_LIBRARIES.REASON_WHY_NOT,
                SCHOOL_REPORTS_WITHOUT_LIBRARIES.READY_TIMELINE)
            .from(SCHOOL_REPORTS_WITHOUT_LIBRARIES)
            .where(SCHOOL_REPORTS_WITHOUT_LIBRARIES.DELETED_AT.isNull())
            .and(SCHOOL_REPORTS_WITHOUT_LIBRARIES.SCHOOL_ID.eq(schoolId))
            .fetchInto(ReportWithoutLibrary.class);

    List<ReportWithLibraryInProgress> inProgressLibraryReports =
        db.select(
                SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.ID,
                SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.CREATED_AT,
                SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.UPDATED_AT,
                SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.SCHOOL_ID,
                SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.USER_ID,
                SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.NUMBER_OF_CHILDREN,
                SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.NUMBER_OF_BOOKS,
                SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.MOST_RECENT_SHIPMENT_YEAR,
                SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.IS_SHARED_SPACE,
                SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.HAS_INVITING_SPACE,
                SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.ASSIGNED_PERSON_ROLE,
                SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.ASSIGNED_PERSON_TITLE,
                SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.APPRENTICESHIP_PROGRAM,
                SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.TRAINS_AND_MENTORS_APPRENTICES)
            .from(SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES)
            .where(SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.DELETED_AT.isNull())
            .and(SCHOOL_REPORTS_IN_PROGRESS_LIBRARIES.SCHOOL_ID.eq(schoolId))
            .fetchInto(ReportWithLibraryInProgress.class);

    List<ReportGeneric> reports = new ArrayList<ReportGeneric>();
    reports.addAll(withLibraryReports);
    reports.addAll(noLibraryReports);
    reports.addAll(inProgressLibraryReports);
    reports.sort(Comparator.comparing(ReportGeneric::getCreatedAt));

    int from = (page - 1) * 10;
    int to = Math.min(page * 10, reports.size());
    List<ReportGeneric> paginatedReports =
        (from >= reports.size()) ? new ArrayList<>() : reports.subList(from, to);

    return new ReportGenericListResponse(paginatedReports);
  }

  private SchoolsRecord queryForSchool(int schoolId) {
    return db.selectFrom(SCHOOLS)
        .where(SCHOOLS.ID.eq(schoolId))
        .and(SCHOOLS.DELETED_AT.isNull())
        .fetchOne();
  }

  private List<SchoolContact> queryForSchoolContacts(int schoolId) {
    return db.select(
            SCHOOL_CONTACTS.ID,
            SCHOOL_CONTACTS.SCHOOL_ID,
            SCHOOL_CONTACTS.FIRST_NAME,
            SCHOOL_CONTACTS.LAST_NAME,
            SCHOOL_CONTACTS.EMAIL,
            SCHOOL_CONTACTS.ADDRESS,
            SCHOOL_CONTACTS.PHONE,
            SCHOOL_CONTACTS.TYPE)
        .from(SCHOOL_CONTACTS)
        .where(SCHOOL_CONTACTS.DELETED_AT.isNull())
        .and(SCHOOL_CONTACTS.SCHOOL_ID.eq(schoolId))
        .fetchInto(SchoolContact.class);
  }
}
