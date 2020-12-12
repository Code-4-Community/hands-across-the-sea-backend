package com.codeforcommunity.api.authenticated;

import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.school.NewSchoolContactRequest;
import com.codeforcommunity.dto.school.NewSchoolRequest;
import com.codeforcommunity.dto.school.School;
import com.codeforcommunity.dto.school.SchoolContact;
import com.codeforcommunity.dto.school.SchoolListResponse;
import java.util.List;

public interface IProtectedSchoolProcessor {

  SchoolListResponse getAllSchools(JWTData userData);

  School getSchool(JWTData userData, int schoolId);

  School createSchool(JWTData userdata, NewSchoolRequest newSchoolRequest);

  List<SchoolContact> getAllSchoolContacts(JWTData userData, int schoolId);

  SchoolContact getSchoolContact(JWTData userData, int schoolId, int contactId);

  SchoolContact createSchoolContact(
      JWTData userData, int schoolId, NewSchoolContactRequest newSchoolContactRequest);

  SchoolContact updateSchoolContact(
      JWTData userData, int schoolId, NewSchoolContactRequest newSchoolContactRequest);

  void deleteSchoolContact(JWTData userData, int schoolId, int contactId);
}
