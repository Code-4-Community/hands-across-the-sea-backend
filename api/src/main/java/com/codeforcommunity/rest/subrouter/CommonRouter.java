package com.codeforcommunity.rest.subrouter;

import com.codeforcommunity.auth.JWTAuthorizer;
import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.exceptions.TokenInvalidException;
import com.codeforcommunity.rest.FailureHandler;
import com.codeforcommunity.rest.IRouter;
import com.codeforcommunity.rest.RestFunctions;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import java.util.Optional;

public class CommonRouter implements IRouter {
  private final JWTAuthorizer jwtAuthorizer;
  private final FailureHandler failureHandler = new FailureHandler();

  public CommonRouter(JWTAuthorizer jwtAuthorizer) {
    this.jwtAuthorizer = jwtAuthorizer;
  }

  @Override
  public Router initializeRouter(Vertx vertx) {
    Router router = Router.router(vertx);

    router.route().handler(LoggerHandler.create()); // Adds request logging
    router.route().handler(BodyHandler.create(false)); // Add body handling
    router.route().failureHandler(failureHandler::handleFailure); // Add failure handling

    router
        .routeWithRegex(".*/protected/.*")
        .handler(this::handleAuthorizeUser); // Add auth checking

    return router;
  }

  /**
   * A handler to be called as the first handler for any request for a protected resource. If given
   * user is authorized this router will call the next router in which the desired response is
   * handled.
   *
   * <p>If user fails authorization this handler will end the handler with an unauthorized response
   * to the user.
   *
   * @param ctx routing context to handle.
   */
  private void handleAuthorizeUser(RoutingContext ctx) {
    String accessToken = RestFunctions.getRequestHeader(ctx.request(), "X-Access-Token");
    Optional<JWTData> jwtData = jwtAuthorizer.checkTokenAndGetData(accessToken);
    if (jwtData.isPresent()) {
      ctx.put("jwt_data", jwtData.get());
      ctx.next();
    } else {
      throw new TokenInvalidException("access");
    }
  }
}
