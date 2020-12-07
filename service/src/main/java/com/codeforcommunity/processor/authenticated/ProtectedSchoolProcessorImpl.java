package com.codeforcommunity.processor.authenticated;

import static org.jooq.generated.Tables.SCHOOLS;

import com.codeforcommunity.api.authenticated.IProtectedSchoolProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.school.School;
import com.codeforcommunity.dto.school.SchoolListResponse;
import java.util.List;
import org.jooq.DSLContext;

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
}
