package com.codeforcommunity.processor.authenticated;

import static org.jooq.generated.Tables.SCHOOLS;
import static org.jooq.generated.Tables.SCHOOL_CONTACTS;

import com.codeforcommunity.api.authenticated.IProtectedSchoolProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.school.NewSchoolRequest;
import com.codeforcommunity.dto.school.School;
import com.codeforcommunity.dto.school.SchoolContact;
import com.codeforcommunity.dto.school.SchoolListResponse;
import com.codeforcommunity.dto.school.SchoolSummary;
import com.codeforcommunity.enums.Country;
import com.codeforcommunity.exceptions.SchoolAlreadyExistsException;
import java.util.List;
import org.jooq.DSLContext;
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

    return new SchoolListResponse(schools, schools.size());
  }

  @Override
  public School getSchool(JWTData userData, int schoolId) {
    School school =
        db.select(SCHOOLS.ID, SCHOOLS.NAME, SCHOOLS.ADDRESS, SCHOOLS.COUNTRY, SCHOOLS.HIDDEN)
            .from(SCHOOLS)
            .where(SCHOOLS.DELETED_AT.isNull())
            .and(SCHOOLS.ID.eq(schoolId))
            .fetchOneInto(School.class);

    List<SchoolContact> contacts = this.getSchoolContacts(schoolId);
    school.setContacts(contacts);
    return school;
  }

  @Override
  public School createSchool(JWTData userdata, NewSchoolRequest newSchoolRequest) {
    String name = newSchoolRequest.getName();
    String address = newSchoolRequest.getAddress();
    Country country = newSchoolRequest.getCountry();
    Boolean hidden = newSchoolRequest.getHidden();

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
      List<SchoolContact> contacts = this.getSchoolContacts(schoolId);
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
  public School updateSchool(JWTData userData, int schoolId) {
    return null;
  }

  @Override
  public void deleteSchool(JWTData userData, int schoolId) {
    SchoolsRecord school =
        db.selectFrom(SCHOOLS)
            .where(SCHOOLS.ID.eq(schoolId))
            .fetchOne();
    if (school == null) {
      // throw school does not exist exception
    }

  }

  @Override
  public void hideSchool(JWTData userData, int schoolId) {
    SchoolsRecord school =
        db.selectFrom(SCHOOLS)
            .where(SCHOOLS.ID.eq(schoolId))
            .fetchOne();
    if (school == null) {
      // throw school does not exist exception
    }
    school.setHidden(true);
    school.store();
  }

  @Override
  public void unHideSchool(JWTData userData, int schoolId) {
    SchoolsRecord school =
        db.selectFrom(SCHOOLS)
            .where(SCHOOLS.ID.eq(schoolId))
            .fetchOne();
    if (school == null) {
      // throw school does not exist exception
    }
    school.setHidden(false);
    school.store();
  }

  private List<SchoolContact> getSchoolContacts(int schoolId) {
    return db.select(
            SCHOOL_CONTACTS.ID,
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
