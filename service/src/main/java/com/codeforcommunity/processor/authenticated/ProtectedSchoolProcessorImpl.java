package com.codeforcommunity.processor.authenticated;

import com.codeforcommunity.api.authenticated.IProtectedSchoolProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.school.NewSchoolRequest;
import com.codeforcommunity.dto.school.School;
import com.codeforcommunity.dto.school.SchoolListResponse;
import com.codeforcommunity.enums.Country;
import com.codeforcommunity.exceptions.SchoolAlreadyExistsException;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.SchoolsRecord;

import java.util.List;

import static org.jooq.generated.Tables.SCHOOLS;

public class ProtectedSchoolProcessorImpl implements IProtectedSchoolProcessor {

  private final DSLContext db;

  public ProtectedSchoolProcessorImpl(DSLContext db) {
    this.db = db;
  }

  @Override
  public SchoolListResponse getAllSchools(JWTData userData) {
    List<School> schools =
        db.select(SCHOOLS.ID, SCHOOLS.NAME, SCHOOLS.ADDRESS, SCHOOLS.COUNTRY)
            .from(SCHOOLS)
            .where(SCHOOLS.HIDDEN.isFalse())
            .and(SCHOOLS.DELETED_AT.isNull())
            .fetchInto(School.class);

    return new SchoolListResponse(schools, schools.size());
  }

  @Override
  public School getSchool(JWTData userData, long schoolId) {
    return db.select(SCHOOLS.ID, SCHOOLS.NAME, SCHOOLS.ADDRESS, SCHOOLS.COUNTRY)
        .from(SCHOOLS)
        .where(SCHOOLS.HIDDEN.isFalse())
        .and(SCHOOLS.DELETED_AT.isNull())
        .and(SCHOOLS.ID.eq(schoolId))
        .fetchOneInto(School.class);
  }

  @Override
  public School createSchool(JWTData userdata, NewSchoolRequest newSchoolRequest) {
    String name = newSchoolRequest.getName();
    String address = newSchoolRequest.getAddress();
    Country country = newSchoolRequest.getCountry();
    Boolean hidden = newSchoolRequest.getHidden();

    SchoolsRecord school = db.selectFrom(SCHOOLS)
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
      return new School(newSchool.getId(), newSchool.getName(), newSchool.getAddress(), newSchool.getCountry());
    }

    if (school.getDeletedAt() != null || school.getHidden()) {
      // If the school was previously deleted, un-delete it
      school.setDeletedAt(null);
      // If the school is hidden, un-hide it
      school.setHidden(false);
      school.store();
      return new School(school.getId(), school.getName(), school.getAddress(), school.getCountry());
    }

    throw new SchoolAlreadyExistsException(name, country);
  }
}
