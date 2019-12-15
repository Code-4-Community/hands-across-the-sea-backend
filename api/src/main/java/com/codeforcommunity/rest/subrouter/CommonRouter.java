package com.codeforcommunity.rest.subrouter;

import com.codeforcommunity.api.IAuthProcessor;
import com.codeforcommunity.auth.JWTAuthorizer;
import com.codeforcommunity.exceptions.AuthException;
import com.codeforcommunity.rest.IRouter;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class CommonRouter implements IRouter {
  private final JWTAuthorizer jwtAuthorizer;

  public CommonRouter(JWTAuthorizer jwtAuthorizer) {
    this.jwtAuthorizer = jwtAuthorizer;
  }

  @Override
  public Router initializeRouter(Vertx vertx) {
    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create(false)); //Add body handling

    router.route().failureHandler(this::handleFailures); //Add failure handling

    router.routeWithRegex(".*/authorized/.*").handler(this::handleAuthorizeUser); //Add auth checking

    return router;
  }

  /**
   * TODO: When is this activated
   */
  private void handleFailures(RoutingContext ctx) {
    Throwable exceptionThrown = ctx.failure();

    if (exceptionThrown instanceof AuthException) {
      ctx.response().setStatusCode(401).end();
    } else if (exceptionThrown instanceof IllegalStateException) {
      ctx.response().setStatusCode(400).end();
    }
  }

  /**
   * A handler to be called as the first handler for any request for a protected resource. If given user is
   * authorization this router will call the next router in which the desired response is handled. If user fails
   * authorization this handler will end the handler with an unauthorized response to the user.
   *
   * @param ctx routing context to handle.
   */
  private void handleAuthorizeUser(RoutingContext ctx) {
    if (authorized(ctx.request())) {
      ctx.next();
    } else {
      ctx.fail(new AuthException("Unauthorized user"));
    }
  }

  private boolean authorized(HttpServerRequest req) {
    String accessToken;

    try {
      accessToken = req.getHeader("access_token");
     return jwtAuthorizer.isAuthorized(accessToken);
    } catch (Exception e) {
      return false;
    }
  }
}
