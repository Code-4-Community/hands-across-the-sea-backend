package com.codeforcommunity.api.authenticated;

import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.report.ReportGeneric;
import com.codeforcommunity.dto.report.ReportGenericListResponse;
import com.codeforcommunity.dto.report.ReportWithLibrary;
import com.codeforcommunity.dto.report.ReportWithoutLibrary;
import com.codeforcommunity.dto.report.UpsertReportWithLibrary;
import com.codeforcommunity.dto.report.UpsertReportWithoutLibrary;

public interface IProtectedReportProcessor {

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

  String getReportAsCsv(JWTData userData, int reportId, boolean hasLibrary);
}
