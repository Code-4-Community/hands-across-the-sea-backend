package com.codeforcommunity.api.authenticated;

import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.report.ReportGenericListResponse;
import com.codeforcommunity.dto.report.ReportWithLibrary;
import com.codeforcommunity.dto.report.UpsertReportWithLibrary;
import com.codeforcommunity.dto.school.School;
import com.codeforcommunity.dto.school.SchoolContact;
import com.codeforcommunity.dto.school.SchoolContactListResponse;
import com.codeforcommunity.dto.school.SchoolListResponse;
import com.codeforcommunity.dto.school.UpsertSchoolContactRequest;
import com.codeforcommunity.dto.school.UpsertSchoolRequest;

public interface IProtectedSchoolProcessor {

  SchoolListResponse getAllSchools(JWTData userData);

  School getSchool(JWTData userData, int schoolId);

  SchoolContactListResponse getAllSchoolContacts(JWTData userData, int schoolId);

  SchoolContact getSchoolContact(JWTData userData, int schoolId, int contactId);

  SchoolContact createSchoolContact(
      JWTData userData, int schoolId, UpsertSchoolContactRequest upsertSchoolContactRequest);

  void updateSchoolContact(
      JWTData userData,
      int schoolId,
      int contactId,
      UpsertSchoolContactRequest upsertSchoolContactRequest);

  void deleteSchoolContact(JWTData userData, int schoolId, int contactId);

  School createSchool(JWTData userdata, UpsertSchoolRequest upsertSchoolRequest);

  void updateSchool(JWTData userData, int schoolId, UpsertSchoolRequest upsertSchoolRequest);

  void deleteSchool(JWTData userData, int schoolId);

  void hideSchool(JWTData userData, int schoolId);

  void unHideSchool(JWTData userData, int schoolId);

  ReportWithLibrary createReportWithLibrary(
      JWTData userData, int schoolId, UpsertReportWithLibrary upsertRequest);

  ReportGenericListResponse getPaginatedReports(JWTData userData, int schoolId);
}
