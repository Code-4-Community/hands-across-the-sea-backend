package com.codeforcommunity.api.authenticated;

import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.school.NewSchoolRequest;
import com.codeforcommunity.dto.school.School;
import com.codeforcommunity.dto.school.SchoolSummary;
import com.codeforcommunity.dto.school.SchoolListResponse;

public interface IProtectedSchoolProcessor {

  SchoolListResponse getAllSchools(JWTData userData);

  School getSchool(JWTData userData, long schoolId);

  School createSchool(JWTData userdata, NewSchoolRequest newSchoolRequest);
}
