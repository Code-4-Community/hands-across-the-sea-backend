package com.codeforcommunity.api.authenticated;

import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.school.UpsertSchoolRequest;
import com.codeforcommunity.dto.school.School;
import com.codeforcommunity.dto.school.SchoolListResponse;

public interface IProtectedSchoolProcessor {

  SchoolListResponse getAllSchools(JWTData userData);

  School getSchool(JWTData userData, int schoolId);

  School createSchool(JWTData userdata, UpsertSchoolRequest upsertSchoolRequest);

  void updateSchool(JWTData userData, int schoolId, UpsertSchoolRequest upsertSchoolRequest);

  void deleteSchool(JWTData userData, int schoolId);

  void hideSchool(JWTData userData, int schoolId);

  void unHideSchool(JWTData userData, int schoolId);
}
