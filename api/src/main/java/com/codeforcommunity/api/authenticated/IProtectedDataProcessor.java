package com.codeforcommunity.api.authenticated;

import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.data.MetricsCountryResponse;
import com.codeforcommunity.dto.data.MetricsSchoolResponse;
import com.codeforcommunity.enums.Country;

public interface IProtectedDataProcessor {

  MetricsCountryResponse getFixedCountryMetrics(JWTData userData, Country country);

  MetricsSchoolResponse getFixedSchoolMetrics(JWTData userData, int schoolId);
}
