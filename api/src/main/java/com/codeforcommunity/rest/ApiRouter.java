package com.codeforcommunity.rest;

import com.codeforcommunity.api.authenticated.IProtectedBookLogProcessor;
import com.codeforcommunity.api.authenticated.IProtectedCountryProcessor;
import com.codeforcommunity.api.authenticated.IProtectedDataProcessor;
import com.codeforcommunity.api.authenticated.IProtectedReportProcessor;
import com.codeforcommunity.api.authenticated.IProtectedSchoolProcessor;
import com.codeforcommunity.api.authenticated.IProtectedUserProcessor;
import com.codeforcommunity.api.unauthenticated.IAuthProcessor;
import com.codeforcommunity.auth.JWTAuthorizer;
import com.codeforcommunity.rest.subrouter.CommonRouter;
import com.codeforcommunity.rest.subrouter.authenticated.ProtectedCountryRouter;
import com.codeforcommunity.rest.subrouter.authenticated.ProtectedDataRouter;
import com.codeforcommunity.rest.subrouter.authenticated.ProtectedSchoolRouter;
import com.codeforcommunity.rest.subrouter.authenticated.ProtectedUserRouter;
import com.codeforcommunity.rest.subrouter.unauthenticated.AuthRouter;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

public class ApiRouter implements IRouter {
  private final CommonRouter commonRouter;
  private final AuthRouter authRouter;
  private final ProtectedUserRouter protectedUserRouter;
  private final ProtectedCountryRouter protectedCountryRouter;
  private final ProtectedSchoolRouter protectedSchoolRouter;
  private final ProtectedDataRouter protectedDataRouter;

  public ApiRouter(
      JWTAuthorizer jwtAuthorizer,
      IAuthProcessor authProcessor,
      IProtectedUserProcessor protectedUserProcessor,
      IProtectedCountryProcessor protectedCountryProcessor,
      IProtectedSchoolProcessor protectedSchoolProcessor,
      IProtectedReportProcessor protectedReportProcessor,
      IProtectedBookLogProcessor protectedBookLogProcessor,
      IProtectedDataProcessor protectedDataProcessor) {
    this.commonRouter = new CommonRouter(jwtAuthorizer);
    this.authRouter = new AuthRouter(authProcessor);
    this.protectedUserRouter = new ProtectedUserRouter(protectedUserProcessor);
    this.protectedCountryRouter = new ProtectedCountryRouter(protectedCountryProcessor);
    this.protectedSchoolRouter =
        new ProtectedSchoolRouter(
            protectedSchoolProcessor, protectedReportProcessor, protectedBookLogProcessor);
    this.protectedDataRouter = new ProtectedDataRouter(protectedDataProcessor);
  }

  /** Initialize a router and register all route handlers on it. */
  public Router initializeRouter(Vertx vertx) {
    Router mainRouter = commonRouter.initializeRouter(vertx);
    definePublicRoutes(vertx, mainRouter);
    mainRouter.mountSubRouter("/protected", defineProtectedRoutes(vertx));
    return mainRouter;
  }

  /** Defines all publicly-accessible (i.e. unprotected) routes. */
  private void definePublicRoutes(Vertx vertx, Router mainRouter) {
    mainRouter.mountSubRouter("/user", authRouter.initializeRouter(vertx));
  }

  /**
   * Mounts all routes that require a user to be logged in. All routes defined here require a user
   * to have a valid JWT access token in their header.
   */
  private Router defineProtectedRoutes(Vertx vertx) {
    Router protectedSubRouter = Router.router(vertx);

    protectedSubRouter.mountSubRouter("/user", protectedUserRouter.initializeRouter(vertx));
    protectedSubRouter.mountSubRouter("/countries", protectedCountryRouter.initializeRouter(vertx));
    protectedSubRouter.mountSubRouter("/schools", protectedSchoolRouter.initializeRouter(vertx));
    protectedSubRouter.mountSubRouter("/data", protectedDataRouter.initializeRouter(vertx));

    return protectedSubRouter;
  }

  public static void end(HttpServerResponse response, int statusCode) {
    end(response, statusCode, null);
  }

  public static void end(HttpServerResponse response, int statusCode, String jsonBody) {
    end(response, statusCode, jsonBody, "application/json");
  }

  public static void end(
      HttpServerResponse response, int statusCode, String jsonBody, String contentType) {
    response
        .setStatusCode(statusCode)
        .putHeader("Content-Type", contentType)
        .putHeader("Access-Control-Allow-Origin", "*")
        .putHeader("Access-Control-Allow-Methods", "DELETE, POST, GET, OPTIONS")
        .putHeader(
            "Access-Control-Allow-Headers",
            "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
    if (jsonBody == null || jsonBody.isEmpty()) {
      response.end();
    } else {
      response.end(jsonBody);
    }
  }
}
