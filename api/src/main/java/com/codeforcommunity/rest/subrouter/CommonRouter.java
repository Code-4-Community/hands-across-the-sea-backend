package com.codeforcommunity.rest.subrouter;

import com.codeforcommunity.rest.IRouter;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class CommonRouter implements IRouter {

  public CommonRouter() {

  }

  @Override
  public Router initializeRouter(Vertx vertx) {
    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create(false));

    router.routeWithRegex(".*/authorized/.*").handler(this::handleAuthorizeUser);

    return router;
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
      ctx.fail(401, new IllegalArgumentException("AAAAAAAAAAAAHHHH!!!!!!!!!!!"));
    }
  }

  private boolean authorized(HttpServerRequest req) {
    String accessToken;

    try {
      accessToken = req.getHeader("access_token");
      return true; //TODO: Validate
    } catch (Exception e) {
      return false;
    }
  }
}
