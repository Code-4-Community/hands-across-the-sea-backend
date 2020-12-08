package com.codeforcommunity.api.authenticated;

import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.school.SchoolListResponse;
import com.codeforcommunity.enums.Country;

public interface IProtectedCountryProcessor {

  SchoolListResponse getSchools(JWTData userData, Country country);
}
