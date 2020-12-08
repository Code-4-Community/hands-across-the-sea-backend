package com.codeforcommunity.rest.subrouter.authenticated;

import static com.codeforcommunity.rest.ApiRouter.end;

import com.codeforcommunity.api.authenticated.IProtectedSchoolProcessor;
import com.codeforcommunity.auth.JWTData;
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

  private void handleGetAllSchoolsRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");
    SchoolListResponse response = processor.getAllSchools(userData);
    end(ctx.response(), 200, JsonObject.mapFrom(response).toString());
  }

  private void handleGetSchoolRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");

    long schoolId = RestFunctions.getPathParamAsLong(ctx, "school_id");
    School response = processor.getSchool(userData, schoolId);

    end(ctx.response(), 200, JsonObject.mapFrom(response).toString());
  }
}
