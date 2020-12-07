package com.codeforcommunity.rest.subrouter.authenticated;

import static com.codeforcommunity.rest.ApiRouter.end;

import com.codeforcommunity.api.authenticated.IProtectedCountryProcessor;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.school.SchoolListResponse;
import com.codeforcommunity.enums.Country;
import com.codeforcommunity.rest.IRouter;
import com.codeforcommunity.rest.RestFunctions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class ProtectedCountryRouter implements IRouter {

  private final IProtectedCountryProcessor processor;

  public ProtectedCountryRouter(IProtectedCountryProcessor processor) {
    this.processor = processor;
  }

  @Override
  public Router initializeRouter(Vertx vertx) {
    Router router = Router.router(vertx);

    registerGetSchools(router);

    return router;
  }

  private void registerGetSchools(Router router) {
    Route getSchoolsRoute = router.get("/:country/schools");
    getSchoolsRoute.handler(this::handleGetSchoolsRoute);
  }

  private void handleGetSchoolsRoute(RoutingContext ctx) {
    JWTData userData = ctx.get("jwt_data");

    Country country = RestFunctions.getCountryFromString(ctx.pathParam("country"));
    SchoolListResponse response = processor.getSchools(userData, country);

    end(ctx.response(), 200, JsonObject.mapFrom(response).toString());
  }
}
