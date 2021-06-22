package com.codeforcommunity.rest.subrouter.authenticated;

import static com.codeforcommunity.rest.ApiRouter.end;

import com.codeforcommunity.api.authenticated.IProtectedDataProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.data.MetricsCountryResponse;
import com.codeforcommunity.dto.data.MetricsSchoolResponse;
import com.codeforcommunity.enums.Country;
import com.codeforcommunity.rest.IRouter;
import com.codeforcommunity.rest.RestFunctions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class ProtectedDataRouter implements IRouter {

  private final IProtectedDataProcessor processor;

  public ProtectedDataRouter(IProtectedDataProcessor processor) {
    this.processor = processor;
  }

  @Override
  public Router initializeRouter(Vertx vertx) {
    Router router = Router.router(vertx);

    registerGetFixedCountryMetrics(router);
    registerGetFixedSchoolyMetrics(router);

    return router;
  }

  private void registerGetFixedCountryMetrics(Router router) {
    Route getCountryMetricsRoute = router.get("/country/:country");
    getCountryMetricsRoute.handler(this::handleGetFixedCountryMetrics);
  }

  private void registerGetFixedSchoolyMetrics(Router router) {
    Route getSchoolMetricsRoute = router.get("/school/:school_id");
    getSchoolMetricsRoute.handler(this::handleGetFixedSchoolMetrics);
  }

  private void handleGetFixedCountryMetrics(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");

    Country country = RestFunctions.getCountryFromString(ctx.pathParam("country"));
    MetricsCountryResponse response = processor.getFixedCountryMetrics(userData, country);

    end(ctx.response(), 200, JsonObject.mapFrom(response).toString());
  }

  private void handleGetFixedSchoolMetrics(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");

    int schoolId = RestFunctions.getPathParamAsInt(ctx, "school_id");
    MetricsSchoolResponse response = processor.getFixedSchoolMetrics(userData, schoolId);

    end(ctx.response(), 200, JsonObject.mapFrom(response).toString());
  }
}
