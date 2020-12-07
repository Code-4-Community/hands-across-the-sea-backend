package com.codeforcommunity.processor.authenticated;

import static org.jooq.generated.Tables.SCHOOLS;

import com.codeforcommunity.api.authenticated.IProtectedCountryProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.school.School;
import com.codeforcommunity.dto.school.SchoolListResponse;
import com.codeforcommunity.enums.Country;
import java.util.List;
import org.jooq.DSLContext;

public class ProtectedCountryProcessorImpl implements IProtectedCountryProcessor {

  private final DSLContext db;

  public ProtectedCountryProcessorImpl(DSLContext db) {
    this.db = db;
  }

  @Override
  public SchoolListResponse getSchools(JWTData userData, Country country) {
    List<School> schools =
        db.select(SCHOOLS.ID, SCHOOLS.NAME, SCHOOLS.ADDRESS, SCHOOLS.COUNTRY)
            .from(SCHOOLS)
            .where(SCHOOLS.HIDDEN.isFalse())
            .and(SCHOOLS.DELETED_AT.isNull())
            .and(SCHOOLS.COUNTRY.eq(country))
            .fetchInto(School.class);

    return new SchoolListResponse(schools, schools.size());
  }
}
