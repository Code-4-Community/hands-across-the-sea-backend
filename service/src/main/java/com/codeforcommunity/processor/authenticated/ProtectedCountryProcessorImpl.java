package com.codeforcommunity.processor.authenticated;

import static org.jooq.generated.Tables.SCHOOLS;

import com.codeforcommunity.api.authenticated.IProtectedCountryProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.school.SchoolListResponse;
import com.codeforcommunity.dto.school.SchoolSummary;
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
    List<SchoolSummary> schools =
        db.select(SCHOOLS.ID, SCHOOLS.NAME, SCHOOLS.COUNTRY)
            .from(SCHOOLS)
            .where(SCHOOLS.HIDDEN.isFalse())
            .and(SCHOOLS.DELETED_AT.isNull())
            .and(SCHOOLS.COUNTRY.eq(country))
            .fetchInto(SchoolSummary.class);

    return new SchoolListResponse(schools);
  }
}
