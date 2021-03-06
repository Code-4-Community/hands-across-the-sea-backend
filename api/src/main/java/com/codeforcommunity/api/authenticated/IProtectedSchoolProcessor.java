package com.codeforcommunity.api.authenticated;

import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.report.ReportGeneric;
import com.codeforcommunity.dto.report.ReportGenericListResponse;
import com.codeforcommunity.dto.report.ReportWithLibrary;
import com.codeforcommunity.dto.report.ReportWithoutLibrary;
import com.codeforcommunity.dto.report.UpsertReportWithLibrary;
import com.codeforcommunity.dto.report.UpsertReportWithoutLibrary;
import com.codeforcommunity.dto.school.BookLog;
import com.codeforcommunity.dto.school.BookLogListResponse;
import com.codeforcommunity.dto.school.School;
import com.codeforcommunity.dto.school.SchoolContact;
import com.codeforcommunity.dto.school.SchoolContactListResponse;
import com.codeforcommunity.dto.school.SchoolListResponse;
import com.codeforcommunity.dto.school.UpsertBookLogRequest;
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

  ReportGeneric getMostRecentReport(JWTData userData, int schoolId);

  ReportWithLibrary createReportWithLibrary(
      JWTData userData, int schoolId, UpsertReportWithLibrary upsertRequest);

  ReportWithoutLibrary createReportWithoutLibrary(
      JWTData userData, int schoolId, UpsertReportWithoutLibrary upsertRequest);

  void updateReportWithLibrary(
      JWTData userData, int schoolId, int reportId, UpsertReportWithLibrary upsertRequest);

  void updateReportWithoutLibrary(
      JWTData userData, int schoolId, int reportId, UpsertReportWithoutLibrary upsertRequest);

  ReportGenericListResponse getPaginatedReports(JWTData userData, int schoolId, int page);

  BookLog createBookLog(JWTData userData, int schoolId, UpsertBookLogRequest request);

  BookLogListResponse getBookLog(JWTData userData, int schoolId);

  String getReportAsCsv(JWTData userData, int reportId, boolean hasLibrary);

  BookLog updateBookLog(JWTData userData, int schoolId, int bookId, UpsertBookLogRequest request);

  void deleteBookLog(JWTData userData, int schoolId, int bookId);

  SchoolListResponse getSchoolsFromUserIdReports(JWTData userData);
}
