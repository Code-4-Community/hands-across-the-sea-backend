package com.codeforcommunity;

import static com.codeforcommunity.rest.ApiRouter.end;

import com.codeforcommunity.logger.SLogger;
import com.codeforcommunity.rest.ApiRouter;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;

/** The main point for the API. */
public class ApiMain {
  private final SLogger logger = new SLogger(ApiMain.class);
  private final ApiRouter apiRouter;

  public ApiMain(ApiRouter apiRouter) {
    this.apiRouter = apiRouter;
  }

  /** Start the API to start listening on a port. */
  public void startApi(Vertx vertx) {
    HttpServer server = vertx.createHttpServer();

    CorsHandler corsHandler =
        CorsHandler.create("*")
            .allowedMethod(HttpMethod.GET)
            .allowedMethod(HttpMethod.POST)
            .allowedMethod(HttpMethod.PUT)
            .allowedMethod(HttpMethod.DELETE)
            .allowedMethod(HttpMethod.OPTIONS)
            .allowedHeader("Content-Type")
            .allowedHeader("origin")
            .allowedHeader("Access-Control-Allow-Origin")
            .allowedHeader("Access-Control-Allow-Credentials")
            .allowedHeader("Access-Control-Allow-Headers")
            .allowedHeader("Access-Control-Request-Method")
            .allowedHeader("X-Access-Token")
            .allowedHeader("X-Refresh-Token");

    Router router = Router.router(vertx);
    router.route().handler(corsHandler);

    Route homeRoute = router.route("/");
    homeRoute.handler(this::handleHealthCheck);

    router.mountSubRouter("/api/v1", apiRouter.initializeRouter(vertx));

    logger.info("Starting server on port 8081", true);
    server.requestHandler(router).listen(8081);
  }

  /**
   * Returns a 200 OK from the home route. This is required for the AWS Elastic Beanstalk "health
   * check".
   *
   * @param ctx the routing context.
   */
  private void handleHealthCheck(RoutingContext ctx) {
    end(ctx.response(), 200);
  }
}
