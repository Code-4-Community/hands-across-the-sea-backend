package com.codeforcommunity.rest.subrouter.authenticated;

import static com.codeforcommunity.rest.ApiRouter.end;

import com.codeforcommunity.api.authenticated.IProtectedSchoolProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.school.NewSchoolRequest;
import com.codeforcommunity.dto.school.School;
import com.codeforcommunity.dto.school.SchoolListResponse;
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

    registerGetAllSchools(router);
    registerGetSchool(router);
    registerCreateSchool(router);
    registerUpdateSchool(router);
    registerDeleteSchool(router);
    registerHidingSchool(router);
    registerUnHidingSchool(router);

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

    NewSchoolRequest newSchoolRequest =
        RestFunctions.getJsonBodyAsClass(ctx, NewSchoolRequest.class);
    School response = processor.createSchool(userData, newSchoolRequest);

    end(ctx.response(), 201, JsonObject.mapFrom(response).toString());
  }

  private void handleUpdateSchoolRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");

    int schoolId = RestFunctions.getPathParamAsInt(ctx, "school_id");
  }

  private void handleDeleteSchoolRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int schoolId = RestFunctions.getPathParamAsInt(ctx, "school_id");
  }

  private void handleHidingSchoolRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int schoolId = RestFunctions.getPathParamAsInt(ctx, "school_id");
  }

  private void handleUnHidingSchoolRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    int schoolId = RestFunctions.getPathParamAsInt(ctx, "school_id");
  }
}
