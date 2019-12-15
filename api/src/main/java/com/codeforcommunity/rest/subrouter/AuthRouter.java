package com.codeforcommunity.rest.subrouter;

import com.codeforcommunity.api.IAuthProcessor;
import com.codeforcommunity.dto.auth.LoginRequest;
import com.codeforcommunity.dto.auth.NewSessionRequest;
import com.codeforcommunity.dto.auth.NewUserRequest;
import com.codeforcommunity.dto.auth.RefreshSessionRequest;
import com.codeforcommunity.dto.auth.RefreshSessionResponse;
import com.codeforcommunity.dto.SessionResponse;
import com.codeforcommunity.rest.HttpConstants;
import com.codeforcommunity.rest.IRouter;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import static com.codeforcommunity.rest.ApiRouter.end;
import static com.codeforcommunity.rest.ApiRouter.endClientError;
import static com.codeforcommunity.rest.ApiRouter.endUnauthorized;

public class AuthRouter implements IRouter {
  private final IAuthProcessor authProcessor;

  public AuthRouter(IAuthProcessor authProcessor) {
    this.authProcessor = authProcessor;
  }

  @Override
  public Router initializeRouter(Vertx vertx) {
    Router router = Router.router(vertx);

    registerLoginUser(router);
    registerRefreshUser(router);
    registerNewUser(router);
    registerLogoutUser(router);

    return router;
  }


  private void registerLoginUser(Router router) {
    Route loginUserRoute = router.route(HttpMethod.POST, "/api/v1/user/getNewUserSession");
    loginUserRoute.handler(this::handlePostUserLoginRoute);
  }

  private void registerRefreshUser(Router router) {
    Route refreshUserRoute = router.route(HttpMethod.POST, "/api/v1/user/getNewUserSession/getNewAccessToken");
    refreshUserRoute.handler(this::handlePostRefreshUser);
  }

  private void registerNewUser(Router router) {
    Route newUserRoute = router.route(HttpMethod.POST, "/api/v1/user/signup");
    newUserRoute.handler(this::handlePostNewUser);
  }

  private void registerLogoutUser(Router router) {
    Route logoutUserRoute = router.route(HttpMethod.DELETE, "/api/v1/user/login");
    logoutUserRoute.handler(this::handleDeleteLogoutUser);
  }


  private void handlePostUserLoginRoute(RoutingContext ctx) {

    try {
      JsonObject reqBody = ctx.getBodyAsJson();

      LoginRequest userRequest = new LoginRequest() {{
        setPassword(reqBody.getString("password"));
        setUsername(reqBody.getString("username"));
      }};

      SessionResponse response = authProcessor.login(userRequest);

      end(ctx.response(), HttpConstants.ok_code, response.toJson());
    } catch (Exception e) {
      endUnauthorized(ctx.response());
    }
  }

  private void handlePostRefreshUser(RoutingContext ctx) {

    try {

      String refreshToken = ctx.getBodyAsJson().getString("refresh_token");

      RefreshSessionRequest request = new RefreshSessionRequest() {{
        setRefreshToken(refreshToken);
      }};

      RefreshSessionResponse response = authProcessor.refreshSession(request);

      end(ctx.response(), HttpConstants.created_code, response.toJson());

    } catch (Exception e) {
      endUnauthorized(ctx.response());
    }
  }

  private void handleDeleteLogoutUser(RoutingContext ctx) {

    try {
      String refreshToken = ctx.getBodyAsJson().getString("refreshToken");
      authProcessor.logout(refreshToken);
    } catch (Exception e) {
      endClientError(ctx.response());
    }

  }

  private void handlePostNewUser(RoutingContext ctx) {

    try {

      JsonObject body = ctx.getBodyAsJson();

      NewUserRequest userRequest = new NewUserRequest() {{
        setEmail(body.getString("email"));
        setUsername(body.getString("username"));
        setPassword(body.getString("password"));
        setFirstName(body.getString("first_name"));
        setLastName(body.getString("last_name"));
      }};

      authProcessor.newUser(userRequest);

    } catch (Exception e) {
      endClientError(ctx.response());
    }
  }
}
