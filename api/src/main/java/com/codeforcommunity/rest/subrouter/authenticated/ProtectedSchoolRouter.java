package com.codeforcommunity.rest.subrouter.authenticated;

import static com.codeforcommunity.rest.ApiRouter.end;

import com.codeforcommunity.api.authenticated.IProtectedSchoolProcessor;
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
import com.codeforcommunity.rest.IRouter;
import com.codeforcommunity.rest.RestFunctions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class ProtectedSchoolRouter implements IRouter {

  private final IProtectedSchoolProcessor processor;

  public ProtectedSchoolRouter(IProtectedSchoolProcessor processor) {
    this.processor = processor;
  }

  @Override
  public Router initializeRouter(Vertx vertx) {
    Router router = Router.router(vertx);

    // Register all school routes
    registerGetAllSchools(router);
    registerGetSchool(router);
    registerCreateSchool(router);
    registerUpdateSchool(router);
    registerDeleteSchool(router);
    registerHidingSchool(router);
    registerUnHidingSchool(router);

    // Register all school contact routes
    registerGetAllSchoolContacts(router);
    registerGetSchoolContact(router);
    registerCreateSchoolContact(router);
    registerUpdateSchoolContact(router);
    registerDeleteSchoolContact(router);

    // Register all school report routes
    registerCreateReportWithLibrary(router);
    registerCreateReportWithoutLibrary(router);
    registerUpdateReportWithLibrary(router);
    registerUpdateReportWithoutLibrary(router);
    registerGetMostRecentReport(router);
    registerGetPaginatedReports(router);
    registerGetWithLibraryReportAsCsv(router);
    registerGetWithoutLibraryReportAsCsv(router);

    // Register all book tracking routes
    registerCreateBookLog(router);
    registerGetBookLog(router);
    registerUpdateBookLog(router);

    return router;
  }

  private void registerGetAllSchools(Router router) {
    Route getSchoolsRoute = router.get("/");
    getSchoolsRoute.handler(this::handleGetAllSchoolsRoute);
  }

  private void registerGetSchool(Router router) {
    Route getSchoolsRoute = router.get("/:school_id");
    getSchoolsRoute.handler(this::handleGetSchoolRoute);
  }

  private void registerCreateSchool(Router router) {
    Route createSchoolRoute = router.post("/");
    createSchoolRoute.handler(this::handleCreateSchoolRoute);
  }

  private void registerUpdateSchool(Router router) {
    Route updateSchoolRoute = router.put("/:school_id");
    updateSchoolRoute.handler(this::handleUpdateSchoolRoute);
  }

  private void registerDeleteSchool(Router router) {
    Route deleteSchoolRouter = router.delete("/:school_id");
    deleteSchoolRouter.handler(this::handleDeleteSchoolRoute);
  }

  private void registerHidingSchool(Router router) {
    Route hideSchoolRouter = router.put("/:school_id/hide");
    hideSchoolRouter.handler(this::handleHidingSchoolRoute);
  }

  private void registerUnHidingSchool(Router router) {
    Route hideSchoolRouter = router.put("/:school_id/unhide");
    hideSchoolRouter.handler(this::handleUnHidingSchoolRoute);
  }

  private void registerGetAllSchoolContacts(Router router) {
    Route getContactsRoute = router.get("/:school_id/contacts");
    getContactsRoute.handler(this::handleGetAllSchoolContactsRoute);
  }

  private void registerGetSchoolContact(Router router) {
    Route getContactRoute = router.get("/:school_id/contacts/:contact_id");
    getContactRoute.handler(this::handleGetSchoolContactRoute);
  }

  private void registerCreateSchoolContact(Router router) {
    Route createContactRoute = router.post("/:school_id/contacts");
    createContactRoute.handler(this::handleCreateSchoolContactRoute);
  }

  private void registerUpdateSchoolContact(Router router) {
    Route createContactRoute = router.put("/:school_id/contacts/:contact_id");
    createContactRoute.handler(this::handleUpdateSchoolContactRoute);
  }

  private void registerDeleteSchoolContact(Router router) {
    Route deleteContactRoute = router.delete("/:school_id/contacts/:contact_id");
    deleteContactRoute.handler(this::handleDeleteSchoolContactRoute);
  }

  private void registerCreateReportWithLibrary(Router router) {
    Route createReport = router.post("/:school_id/reports/with-library");
    createReport.handler(this::handleCreateReportWithLibrary);
  }

  private void registerUpdateReportWithLibrary(Router router) {
    Route createReport = router.put("/:school_id/reports/with-library/:report_id");
    createReport.handler(this::handleUpdateReportWithLibrary);
  }

  private void registerGetMostRecentReport(Router router) {
    Route getMostRecentReport = router.get("/:school_id/report");
    getMostRecentReport.handler(this::handleGetMostRecentReport);
  }

  private void registerCreateReportWithoutLibrary(Router router) {
    Route createReport = router.post("/:school_id/reports/without-library");
    createReport.handler(this::handleCreateReportWithoutLibrary);
  }

  private void registerUpdateReportWithoutLibrary(Router router) {
    Route createReport = router.put("/:school_id/reports/without-library/:report_id");
    createReport.handler(this::handleUpdateReportWithoutLibrary);
  }

  private void registerGetPaginatedReports(Router router) {
    Route getReports = router.get("/:school_id/reports");
    getReports.handler(this::handleGetPaginatedReport);
  }

  private void registerCreateBookLog(Router router) {
    Route createBookLog = router.post("/:school_id/books");
    createBookLog.handler(this::handleCreateBookLog);
  }

  private void registerUpdateBookLog(Router router) {
    Route createBookLog = router.put("/:school_id/books/:book_id");
    createBookLog.handler(this::handleUpdateBookLog);
  }

  private void registerGetBookLog(Router router) {
    Route getBookFlow = router.get("/:school_id/books");
    getBookFlow.handler(this::handleGetBookLog);
  }

  private void registerGetWithoutLibraryReportAsCsv(Router router) {
    Route getReportAsCsv = router.get("/reports/without-library/:report_id");
    getReportAsCsv.handler(this::handleGetWithoutLibraryReportAsCsv);
  }

  private void registerGetWithLibraryReportAsCsv(Router router) {
    Route getReportAsCsv = router.get("/reports/with-library/:report_id");
    getReportAsCsv.handler(this::handleGetWithLibraryReportAsCsv);
  }

  private void handleGetAllSchoolsRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    SchoolListResponse response = processor.getAllSchools(userData);
    end(ctx.response(), 200, JsonObject.mapFrom(response).toString());
  }

  private void handleGetSchoolRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int schoolId = RestFunctions.getPathParamAsInt(ctx, "school_id");
    School response = processor.getSchool(userData, schoolId);
    end(ctx.response(), 200, JsonObject.mapFrom(response).toString());
  }

  private void handleCreateSchoolRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    UpsertSchoolRequest upsertSchoolRequest =
        RestFunctions.getJsonBodyAsClass(ctx, UpsertSchoolRequest.class);
    School response = processor.createSchool(userData, upsertSchoolRequest);
    end(ctx.response(), 201, JsonObject.mapFrom(response).toString());
  }

  private void handleUpdateSchoolRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int schoolId = RestFunctions.getPathParamAsInt(ctx, "school_id");
    UpsertSchoolRequest upsertSchoolRequest =
        RestFunctions.getJsonBodyAsClass(ctx, UpsertSchoolRequest.class);
    processor.updateSchool(userData, schoolId, upsertSchoolRequest);
    end(ctx.response(), 200);
  }

  private void handleDeleteSchoolRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int schoolId = RestFunctions.getPathParamAsInt(ctx, "school_id");
    processor.deleteSchool(userData, schoolId);
    end(ctx.response(), 200);
  }

  private void handleHidingSchoolRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int schoolId = RestFunctions.getPathParamAsInt(ctx, "school_id");
    processor.hideSchool(userData, schoolId);
    end(ctx.response(), 200);
  }

  private void handleUnHidingSchoolRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int schoolId = RestFunctions.getPathParamAsInt(ctx, "school_id");
    processor.unHideSchool(userData, schoolId);
    end(ctx.response(), 200);
  }

  private void handleGetAllSchoolContactsRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int schoolId = RestFunctions.getPathParamAsInt(ctx, "school_id");
    SchoolContactListResponse response = processor.getAllSchoolContacts(userData, schoolId);
    end(ctx.response(), 200, JsonObject.mapFrom(response).toString());
  }

  private void handleGetSchoolContactRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int schoolId = RestFunctions.getPathParamAsInt(ctx, "school_id");
    int contactId = RestFunctions.getPathParamAsInt(ctx, "contact_id");
    SchoolContact response = processor.getSchoolContact(userData, schoolId, contactId);
    end(ctx.response(), 200, JsonObject.mapFrom(response).toString());
  }

  private void handleCreateSchoolContactRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    UpsertSchoolContactRequest request =
        RestFunctions.getJsonBodyAsClass(ctx, UpsertSchoolContactRequest.class);
    int schoolId = RestFunctions.getPathParamAsInt(ctx, "school_id");
    SchoolContact response = processor.createSchoolContact(userData, schoolId, request);
    end(ctx.response(), 201, JsonObject.mapFrom(response).toString());
  }

  private void handleUpdateSchoolContactRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    UpsertSchoolContactRequest request =
        RestFunctions.getJsonBodyAsClass(ctx, UpsertSchoolContactRequest.class);
    int schoolId = RestFunctions.getPathParamAsInt(ctx, "school_id");
    int contactId = RestFunctions.getPathParamAsInt(ctx, "contact_id");
    processor.updateSchoolContact(userData, schoolId, contactId, request);
    end(ctx.response(), 200);
  }

  private void handleDeleteSchoolContactRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int schoolId = RestFunctions.getPathParamAsInt(ctx, "school_id");
    int contactId = RestFunctions.getPathParamAsInt(ctx, "contact_id");
    processor.deleteSchoolContact(userData, schoolId, contactId);
    end(ctx.response(), 200);
  }

  private void handleCreateReportWithLibrary(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    UpsertReportWithLibrary request =
        RestFunctions.getJsonBodyAsClass(ctx, UpsertReportWithLibrary.class);
    int schoolId = RestFunctions.getPathParamAsInt(ctx, "school_id");
    ReportWithLibrary report = processor.createReportWithLibrary(userData, schoolId, request);
    end(ctx.response(), 201, JsonObject.mapFrom(report).toString());
  }

  private void handleUpdateReportWithLibrary(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    UpsertReportWithLibrary request =
        RestFunctions.getJsonBodyAsClass(ctx, UpsertReportWithLibrary.class);
    int schoolId = RestFunctions.getPathParamAsInt(ctx, "school_id");
    int reportId = RestFunctions.getPathParamAsInt(ctx, "report_id");
    processor.updateReportWithLibrary(userData, schoolId, reportId, request);
    end(ctx.response(), 200);
  }

  private void handleGetMostRecentReport(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int schoolId = RestFunctions.getPathParamAsInt(ctx, "school_id");
    ReportGeneric report = processor.getMostRecentReport(userData, schoolId);
    end(ctx.response(), 200, JsonObject.mapFrom(report).toString());
  }

  private void handleCreateReportWithoutLibrary(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    UpsertReportWithoutLibrary request =
        RestFunctions.getJsonBodyAsClass(ctx, UpsertReportWithoutLibrary.class);
    int schoolID = RestFunctions.getPathParamAsInt(ctx, "school_id");
    ReportWithoutLibrary report = processor.createReportWithoutLibrary(userData, schoolID, request);
    end(ctx.response(), 201, JsonObject.mapFrom(report).toString());
  }

  private void handleUpdateReportWithoutLibrary(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    UpsertReportWithoutLibrary request =
        RestFunctions.getJsonBodyAsClass(ctx, UpsertReportWithoutLibrary.class);
    int schoolID = RestFunctions.getPathParamAsInt(ctx, "school_id");
    int reportId = RestFunctions.getPathParamAsInt(ctx, "report_id");
    processor.updateReportWithoutLibrary(userData, schoolID, reportId, request);
    end(ctx.response(), 200);
  }

  private void handleGetPaginatedReport(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int schoolId = RestFunctions.getPathParamAsInt(ctx, "school_id");
    int page = RestFunctions.getRequestParameterAsInt(ctx.request(), "p");
    ReportGenericListResponse reports = processor.getPaginatedReports(userData, schoolId, page);
    end(ctx.response(), 200, JsonObject.mapFrom(reports).toString());
  }

  private void handleCreateBookLog(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    UpsertBookLogRequest request =
        RestFunctions.getJsonBodyAsClass(ctx, UpsertBookLogRequest.class);
    int schoolId = RestFunctions.getPathParamAsInt(ctx, "school_id");
    BookLog log = processor.createBookLog(userData, schoolId, request);
    end(ctx.response(), 201, JsonObject.mapFrom(log).toString());
  }

  private void handleGetBookLog(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int schoolId = RestFunctions.getPathParamAsInt(ctx, "school_id");
    BookLogListResponse response = processor.getBookLog(userData, schoolId);
    end(ctx.response(), 200, JsonObject.mapFrom(response).toString());
  }

  private void handleUpdateBookLog(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    UpsertBookLogRequest request =
        RestFunctions.getJsonBodyAsClass(ctx, UpsertBookLogRequest.class);
    int schoolId = RestFunctions.getPathParamAsInt(ctx, "school_id");
    int bookId = RestFunctions.getPathParamAsInt(ctx, "book_id");
    BookLog log = processor.updateBookLog(userData, schoolId, bookId, request);
    end(ctx.response(), 200, JsonObject.mapFrom(log).toString());
  }

  private void handleGetWithoutLibraryReportAsCsv(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int reportId = RestFunctions.getPathParamAsInt(ctx, "report_id");
    String response;
    response = processor.getReportAsCsv(userData, reportId, false);
    end(ctx.response(), 200, response, "text/csv");
  }

  private void handleGetWithLibraryReportAsCsv(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int reportId = RestFunctions.getPathParamAsInt(ctx, "report_id");
    String response;
    response = processor.getReportAsCsv(userData, reportId, true);
    end(ctx.response(), 200, response, "text/csv");
  }
}
