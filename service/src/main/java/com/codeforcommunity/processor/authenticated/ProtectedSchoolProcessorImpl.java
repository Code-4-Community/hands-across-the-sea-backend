package com.codeforcommunity.processor.authenticated;

import static org.jooq.generated.Tables.SCHOOLS;
import static org.jooq.generated.Tables.SCHOOL_CONTACTS;

import com.codeforcommunity.api.authenticated.IProtectedSchoolProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.school.School;
import com.codeforcommunity.dto.school.SchoolContact;
import com.codeforcommunity.dto.school.SchoolContactListResponse;
import com.codeforcommunity.dto.school.SchoolListResponse;
import com.codeforcommunity.dto.school.SchoolSummary;
import com.codeforcommunity.dto.school.UpsertSchoolContactRequest;
import com.codeforcommunity.dto.school.UpsertSchoolRequest;
import com.codeforcommunity.enums.Country;
import com.codeforcommunity.exceptions.AdminOnlyRouteException;
import com.codeforcommunity.exceptions.SchoolAlreadyExistsException;
import com.codeforcommunity.exceptions.SchoolContactAlreadyExistsException;
import com.codeforcommunity.exceptions.SchoolContactDoesNotExistException;
import com.codeforcommunity.exceptions.SchoolDoesNotExistException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.SchoolContactsRecord;
import org.jooq.generated.tables.records.SchoolsRecord;

public class ProtectedSchoolProcessorImpl implements IProtectedSchoolProcessor {

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
        db.select(SCHOOLS.ID, SCHOOLS.NAME, SCHOOLS.ADDRESS, SCHOOLS.COUNTRY, SCHOOLS.HIDDEN)
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
    Country country = upsertSchoolRequest.getCountry();
    Boolean hidden = upsertSchoolRequest.getHidden();

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
      newSchool.setCountry(country);
      newSchool.setHidden(hidden);
      newSchool.store();

      return new School(
          newSchool.getId(),
          newSchool.getName(),
          newSchool.getAddress(),
          newSchool.getCountry(),
          newSchool.getHidden());
    }

    if (school.getDeletedAt() != null || school.getHidden()) {
      // If the school was previously deleted, un-delete it
      school.setDeletedAt(null);
      school.setHidden(hidden);
      school.store();

      Integer schoolId = school.getId();
      List<SchoolContact> contacts = this.queryForSchoolContacts(schoolId);

      return new School(
          school.getId(),
          school.getName(),
          school.getAddress(),
          school.getCountry(),
          school.getHidden(),
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
                SCHOOL_CONTACTS.NAME,
                SCHOOL_CONTACTS.EMAIL,
                SCHOOL_CONTACTS.ADDRESS,
                SCHOOL_CONTACTS.PHONE)
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
                SCHOOL_CONTACTS.NAME,
                SCHOOL_CONTACTS.EMAIL,
                SCHOOL_CONTACTS.ADDRESS,
                SCHOOL_CONTACTS.PHONE)
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

    String name = upsertSchoolContactRequest.getName();
    String email = upsertSchoolContactRequest.getEmail();
    String address = upsertSchoolContactRequest.getAddress();
    String phone = upsertSchoolContactRequest.getPhone();

    SchoolContactsRecord contact =
        db.selectFrom(SCHOOL_CONTACTS)
            .where(SCHOOL_CONTACTS.NAME.eq(name))
            .and(SCHOOL_CONTACTS.EMAIL.eq(email))
            .and(SCHOOL_CONTACTS.ADDRESS.eq(address))
            .and(SCHOOL_CONTACTS.PHONE.eq(phone))
            .and(SCHOOL_CONTACTS.SCHOOL_ID.eq(schoolId))
            .fetchOne();

    if (contact == null) {
      // If the contact doesn't already exist, create it
      SchoolContactsRecord newContact = db.newRecord(SCHOOL_CONTACTS);
      newContact.setSchoolId(schoolId);
      newContact.setName(name);
      newContact.setEmail(email);
      newContact.setAddress(address);
      newContact.setPhone(phone);
      newContact.store();

      return new SchoolContact(
          newContact.getId(),
          newContact.getSchoolId(),
          newContact.getName(),
          newContact.getEmail(),
          newContact.getAddress(),
          newContact.getPhone());
    }

    if (contact.getDeletedAt() != null) {
      // If the contact was previously deleted, un-delete it
      contact.setDeletedAt(null);
      contact.store();

      return new SchoolContact(
          contact.getId(),
          contact.getSchoolId(),
          contact.getName(),
          contact.getEmail(),
          contact.getAddress(),
          contact.getPhone());
    }

    throw new SchoolContactAlreadyExistsException(school.getName(), name);
  }

  @Override
  public SchoolContact updateSchoolContact(
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

    String name = upsertSchoolContactRequest.getName();
    String email = upsertSchoolContactRequest.getEmail();
    String address = upsertSchoolContactRequest.getAddress();
    String phone = upsertSchoolContactRequest.getPhone();

    contactRecord.setName(name);
    contactRecord.setEmail(email);
    contactRecord.setAddress(address);
    contactRecord.setPhone(phone);
    contactRecord.store();

    return new SchoolContact(contactId, schoolId, name, email, address, phone);
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
    Country country = upsertSchoolRequest.getCountry();
    Boolean hidden = upsertSchoolRequest.getHidden();

    school.setName(name);
    school.setAddress(address);
    school.setCountry(country);
    school.setHidden(hidden);
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
            SCHOOL_CONTACTS.NAME,
            SCHOOL_CONTACTS.ADDRESS,
            SCHOOL_CONTACTS.EMAIL,
            SCHOOL_CONTACTS.PHONE)
        .from(SCHOOL_CONTACTS)
        .where(SCHOOL_CONTACTS.DELETED_AT.isNull())
        .and(SCHOOL_CONTACTS.SCHOOL_ID.eq(schoolId))
        .fetchInto(SchoolContact.class);
  }
}
